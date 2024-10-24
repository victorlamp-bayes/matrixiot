package com.victorlamp.matrixiot.service.agent.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.victorlamp.matrixiot.configuration.message.RocketMQTemplateProducer;
import com.victorlamp.matrixiot.service.agent.service.Agent;
import com.victorlamp.matrixiot.service.agent.utils.mqtt.MqttClientConnect;
import com.victorlamp.matrixiot.service.agent.utils.mqtt.MqttClientManager;
import com.victorlamp.matrixiot.service.common.constant.ThingTopic;
import com.victorlamp.matrixiot.service.management.api.ProductService;
import com.victorlamp.matrixiot.service.management.api.ThingService;
import com.victorlamp.matrixiot.service.management.dto.thing.ThingCreateReqDTO;
import com.victorlamp.matrixiot.service.management.entity.product.Product;
import com.victorlamp.matrixiot.service.management.entity.thing.Thing;
import com.victorlamp.matrixiot.service.management.entity.thing.ThingExternalConfig;
import com.victorlamp.matrixiot.service.management.entity.thingdata.ThingData;
import com.victorlamp.matrixiot.service.management.entity.thingdata.ThingPropertyData;
import com.victorlamp.matrixiot.service.management.entity.thingdata.ThingServiceData;
import com.victorlamp.matrixiot.service.management.enums.ExternalTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.victorlamp.matrixiot.common.exception.util.ServiceExceptionUtil.exception;
import static com.victorlamp.matrixiot.service.agent.constant.ErrorCodeConstants.THING_AGENT_MQTT_SUB_TOPIC_FAILED;

@Service
@Slf4j
public class DuyunMqttAgent extends Agent implements MqttCallbackExtended {

    private static final String EXTERNAL_TYPE = ExternalTypeEnum.DUYUN_MQTT.name();
    private final Cache<String, Product> productCache = Caffeine.newBuilder().build();
    @DubboReference
    private ProductService productService;
    @DubboReference
    private ThingService thingService;
    @Resource
    private RocketMQTemplateProducer mqProducer;
    @Resource
    private MqttClientManager mqttClientManager;

    @Resource
    private ApplicationContext applicationContext;
    @Value("${environment.type}")
    private String evn;

    private static final String ENV_STANDALONE = "standalone";

    // TODO 需要考虑已经在线上运行时，创建产品后能自动加载新配置。可考虑定时检测或开放触发接口（直接调用或消息队列）
    @PostConstruct
    public void init() {
        List<Product> productList = new ArrayList<>();

        if (StrUtil.equals(evn, ENV_STANDALONE)) {
            log.info("单机版调用listProductByExternalType");
            ProductService service = (ProductService) applicationContext.getBean("productService");
            service.listProductByExternalType(EXTERNAL_TYPE);
        } else {
            log.info("微服务调用listProductByExternalType");
            productList = productService.listProductByExternalType(EXTERNAL_TYPE);
        }

        for (Product product : productList) {
            if (validateProductExternalType(product, EXTERNAL_TYPE)) {
                productCache.put(product.getId(), product);
                try {
                    mqttClientManager.setClientConnect(product, this);
                } catch (MqttException e) {
                    log.error("连接服务器或订阅主题失败[{}]:{}", product.getId(), product.getExternalConfig().getConfig());
                }
            }
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        String payload = StrUtil.str(message.getPayload(), StandardCharsets.UTF_8);
        log.debug("接收到MQTT消息:\n{}", payload);

        JSONObject jsonObj = JSON.parseObject(payload);

        // TODO 校验数据格式，可考虑使用JSON Schema

        // 解析设备属性
        int battery = jsonObj.getIntValue("battery");
        long timestamp = DateUtil.parseDateTime(jsonObj.getString("ts")).getTime();
        String gatewayId = jsonObj.getString("gatewayId");

        // 解析设备读数数据
        Thing thing = null;
        JSONArray dataArray = jsonObj.getJSONObject("payload").getJSONArray("data");
        for (JSONObject dataJsonObj : dataArray.toList(JSONObject.class)) {
            String addr = dataJsonObj.getString("addr"); // 表编号，作为ThingExternalConfigId
            String deviceId = dataJsonObj.getString("deviceId"); // 设备Id
            float value = dataJsonObj.getFloatValue("value"); // 读数
            long ts = DateUtil.parseDateTime(dataJsonObj.getString("ts")).getTime(); // 采集时间

            // 获取Thing
            if (thing == null) {
                thing = thingService.getThingByExternalConfigId(addr);
                // 创建Thing
                if (thing == null) {
                    // 找到所属的产品
                    Product product = null;
                    for (Product p : productCache.asMap().values()) {
                        // TODO: 要求topic相同，当前场景下，不允许有多个产品订阅完全一致的topic
                        if (StrUtil.equals(topic, p.getExternalConfig().getConfig().getString("topic"))) {
                            product = p;
                            break;
                        }
                    }
                    assert product != null;

                    JSONObject externalConfig = new JSONObject();
                    externalConfig.put("id", addr);
                    externalConfig.put("addr", addr);
                    externalConfig.put("deviceId", deviceId);
                    externalConfig.put("gatewayId", gatewayId);

                    ThingCreateReqDTO createReqDTO = ThingCreateReqDTO.builder()
                            .productId(product.getId())
                            .name(addr)
                            .enabled(true)
                            .externalConfig(new ThingExternalConfig(EXTERNAL_TYPE, externalConfig))
                            .build();

                    thing = thingService.createThing(createReqDTO);
                }
            }

            if (thing == null) {
                log.error("获取设备失败");
                break;
            }

            // 构造ThingData
            ThingData thingData = ThingData.builder().productId(thing.getProduct().getId()).thingId(thing.getId()).timestamp(ts).build();
            List<ThingPropertyData> propertyDataList = CollUtil.newArrayList();
            propertyDataList.add(new ThingPropertyData("value", StrUtil.toString(value), ts));
            propertyDataList.add(new ThingPropertyData("battery", StrUtil.toString(battery), ts));
            // 附加电子表号
            propertyDataList.add(new ThingPropertyData("electronicNo", addr, ts));
            thingData.setProperties(propertyDataList);

            // TODO 使用批量消息或顺序消息
            // 发送到消息队列
            mqProducer.asyncSendMessage(ThingTopic.THING_PROPERTY_EVENT_POST, thingData);
        }
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        if (reconnect) {
            log.info("重连MQTT服务器成功");
            for (Map.Entry<String, MqttClientConnect> entry : mqttClientManager.mqttClients.asMap().entrySet()) {
                Product product = productCache.getIfPresent(entry.getKey());
                assert product != null;
                String topic = product.getExternalConfig().getConfig().getString("topic");
                try {
                    entry.getValue().sub(topic);
                    log.info("重连后订阅主题成功[{}]", topic);
                } catch (MqttException e) {
                    log.error("重连后订阅主题失败[{}]", topic);
                    throw exception(THING_AGENT_MQTT_SUB_TOPIC_FAILED);  // TODO 抛出自定义异常
                }
            }
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        log.info("MQTT服务器连接断开:{}", cause.getMessage());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }

    @Override
    public void pullThing(Product product) {
    }

    @Override
    public void pullThingData(Product product) {
    }

    @Override
    public void pullThingData(Thing thing) {
    }

    @Override
    public void postThingData(String originData) {

    }

    @Override
    public void registerDevice(Product product, Thing thing) {

    }

    @Override
    public ThingServiceData sendCommand(Product product, Thing thing, ThingServiceData serviceData, String rawData) {
        return null;
    }

    @Override
    public void replyCommand(String message) {

    }
}
