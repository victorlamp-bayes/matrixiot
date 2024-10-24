package com.victorlamp.matrixiot.service.agent.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.schema.JSONSchema;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.victorlamp.matrixiot.common.util.cache.CaffeineCacheUtils;
import com.victorlamp.matrixiot.configuration.message.RocketMQTemplateProducer;
import com.victorlamp.matrixiot.service.agent.service.Agent;
import com.victorlamp.matrixiot.service.agent.utils.script.ThingModelScriptExecutor;
import com.victorlamp.matrixiot.service.common.constant.ThingTopic;
import com.victorlamp.matrixiot.service.management.api.ProductService;
import com.victorlamp.matrixiot.service.management.api.ThingModelService;
import com.victorlamp.matrixiot.service.management.api.ThingService;
import com.victorlamp.matrixiot.service.management.entity.externalconfig.ExternalConfigTemplate;
import com.victorlamp.matrixiot.service.management.entity.product.Product;
import com.victorlamp.matrixiot.service.management.entity.thing.Thing;
import com.victorlamp.matrixiot.service.management.entity.thingdata.ThingData;
import com.victorlamp.matrixiot.service.management.entity.thingdata.ThingPropertyData;
import com.victorlamp.matrixiot.service.management.entity.thingdata.ThingServiceData;
import com.victorlamp.matrixiot.service.management.entity.thingmodel.ThingModel;
import com.victorlamp.matrixiot.service.management.enums.ExternalTypeEnum;
import com.victorlamp.matrixiot.service.agent.utils.ThingModelUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.time.Duration;

import static com.victorlamp.matrixiot.common.exception.util.ServiceExceptionUtil.exception;
import static com.victorlamp.matrixiot.service.agent.constant.ErrorCodeConstants.AGENT_LORA_DEVICE_POST_INVALID_DATA;

@Component
@Slf4j
public class HuaxuLoraAgent extends Agent {

    private static final String EXTERNAL_TYPE = ExternalTypeEnum.HUAXU_LORA.name();

    @DubboReference
    private ProductService productService;
    private final LoadingCache<String, JSONSchema> jsonSchemaCache = CaffeineCacheUtils.buildLoadingCache(
            Duration.ofMinutes(1L),
            new CacheLoader<>() {
                @Override
                public @Nullable JSONSchema load(String key) {
                    ExternalConfigTemplate template = productService.getExternalTemplateByType(key);
                    return JSONSchema.parseSchema(JSON.toJSONString(template.getJsonSchema()));
                }
            }
    );

    @DubboReference
    private ThingService thingService;
    @DubboReference
    private ThingModelService thingModelService;
    private final LoadingCache<String, ThingModel> thingModelCache = CaffeineCacheUtils.buildLoadingCache(
            Duration.ofMinutes(1L),
            new CacheLoader<>() {
                @Override
                public @Nullable ThingModel load(String key) {
                    return thingModelService.describeThingModel(key);
                }
            }
    );

    @Resource
    private RocketMQTemplateProducer mqProducer;

    @Override
    public void postThingData(String originData) {
        // 校验原始数据
        validatePayload(originData);

        // 获取设备信息
        JSONObject originDataJson = JSON.parseObject(originData);
        Thing thing = validateThingExists(originDataJson);
        // LoRa平台上报设备数据时，很多基于产品级推送，如果设备未在HVIOT平台注册，直接返回，不抛异常
        if (thing == null) {
            return;
        }

        // 获取数据内容
        String contentStr = originDataJson.getString("data");
        if (StrUtil.isBlank(contentStr)) {
            log.info("上报数据为空");
            return;
        }

        // TODO: LoRa设备暂时过滤心跳包和错误包
        Integer dataType = originDataJson.getInteger("data_type");
        if (dataType == 223 || dataType == 408) {
            log.info("忽略心跳和错误");
            return;
        }

        // 转换为内部协议。忽略解析的空数据，不抛异常
        String protocolData = ThingModelScriptExecutor.rawDataToProtocol(thing.getProduct().getId(), contentStr);
        if (StrUtil.isBlank(protocolData)) {
            return;
        }
        if (JSON.parseObject(protocolData).containsKey("message")) {
            log.info("LoRaWAN设备数据解析返回错误: {}", protocolData);
            return;
        }

        // 获取物模型
        ThingModel thingModel = thingModelCache.get(thing.getProduct().getId());
        // 检查数据是否合法
        ThingModelUtil.validateThingData(thingModel, protocolData);
        // 构造ThingData
        ThingData thingData = ThingModelUtil.buildThingData(thingModel, thing, protocolData, originData);
        // 附加电子表号
        // TODO 处理异常情况。正常情况下至少应该有一个属性
        assert CollUtil.size(thingData.getProperties()) > 0;
        ThingPropertyData property = ThingPropertyData.builder()
                .identifier("electronicNo")
                .value(thing.getExternalConfig().getConfig().getString("electronicNo"))
                .timestamp(thingData.getProperties().get(0).getTimestamp()).build();
        thingData.getProperties().add(property);

        // 发送到消息队列
        mqProducer.asyncSendMessage(ThingTopic.THING_PROPERTY_EVENT_POST, thingData);
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

    @Override
    protected void validatePayload(String originData) {
        super.validatePayload(originData);

        JSONSchema schema = jsonSchemaCache.get(EXTERNAL_TYPE);
        if (schema == null) {
            return;
        }

        if (!schema.isValid(JSON.parseObject(originData))) {
            throw exception(AGENT_LORA_DEVICE_POST_INVALID_DATA);
        }
    }

    private Thing validateThingExists(JSONObject originDataJson) {
        String mac = originDataJson.getString("mac");
        Thing thing = thingService.getThingByExternalConfigId(mac);
        if (ObjUtil.isNull(thing)) {
            log.info("设备不存在[{}]", mac);
        } else {
            log.info("匹配到设备[{}]", mac);
        }
        return thing;
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

}
