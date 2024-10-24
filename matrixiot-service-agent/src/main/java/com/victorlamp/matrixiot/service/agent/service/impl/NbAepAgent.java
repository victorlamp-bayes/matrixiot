package com.victorlamp.matrixiot.service.agent.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.schema.JSONSchema;
import com.ctg.ag.sdk.biz.AepDeviceCommandClient;
import com.ctg.ag.sdk.biz.AepDeviceManagementClient;
import com.ctg.ag.sdk.biz.aep_device_command.CreateCommandRequest;
import com.ctg.ag.sdk.biz.aep_device_management.CreateDeviceRequest;
import com.ctg.ag.sdk.core.http.RequestFormat;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.victorlamp.matrixiot.common.util.cache.CaffeineCacheUtils;
import com.victorlamp.matrixiot.configuration.message.RocketMQTemplateProducer;
import com.victorlamp.matrixiot.service.agent.service.Agent;
import com.victorlamp.matrixiot.service.agent.utils.script.ThingModelScriptExecutor;
import com.victorlamp.matrixiot.service.common.constant.ThingTopic;
import com.victorlamp.matrixiot.service.management.api.ProductService;
import com.victorlamp.matrixiot.service.management.api.ThingDataService;
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
import java.util.ArrayList;
import java.util.List;

import static com.victorlamp.matrixiot.common.exception.util.ServiceExceptionUtil.exception;
import static com.victorlamp.matrixiot.service.agent.constant.ErrorCodeConstants.*;

@Component()
@Slf4j
public class NbAepAgent extends Agent {

    private static final String EXTERNAL_TYPE = ExternalTypeEnum.NB_AEP.name();

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

    @DubboReference
    private ThingDataService thingDataService;

    @Resource
    private RocketMQTemplateProducer mqProducer;

    @Override
    public void postThingData(String originData) {
        // 校验原始数据
        validatePayload(originData);

        // 获取设备信息
        JSONObject originDataJson = JSON.parseObject(originData);
        Thing thing = validateThingExists(originDataJson);
        // AEP平台上报设备数据时，很多基于产品级推送，如果设备未在HVIOT平台注册，直接返回，不抛异常
        if (thing == null) {
            return;
        }

        // 获取数据内容
        String contentStr = originDataJson.getJSONObject("payload").getJSONObject("serviceData").getString("content");
        if (StrUtil.isBlank(contentStr)) {
            log.info("上报数据为空");
            return;
        }

        // 转换为内部协议。忽略解析的空数据，不抛异常
        Product product = thing.getProduct();
        String protocolData = ThingModelScriptExecutor.rawDataToProtocol(product.getId(), contentStr);
        if (StrUtil.isBlank(protocolData)) {
            return;
        }

        // 获取物模型
        ThingModel thingModel = thingModelCache.get(product.getId());
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
        // 获取AEP配置
        JSONObject productExternalConfig = product.getExternalConfig().getConfig();
        String appId = productExternalConfig.getString("appId");
        String appSecret = productExternalConfig.getString("appSecret");
        String masterKey = productExternalConfig.getString("masterKey");

        // 构造请求
        AepDeviceManagementClient client = AepDeviceManagementClient.newClient().appKey(appId).appSecret(appSecret).build();
        CreateDeviceRequest deviceReq = new CreateDeviceRequest();
        deviceReq.setPath("/device");
        deviceReq.setMethod(RequestFormat.POST());
        deviceReq.setParamMasterKey(masterKey);

        // 构造请求体
        JSONObject reqBody = new JSONObject();
        // 设备参数
        JSONObject thingExternalConfig = thing.getExternalConfig().getConfig();
        reqBody.put("deviceName", thing.getName());
        reqBody.put("imei", thingExternalConfig.getString("imei"));
        reqBody.put("deviceSn", thingExternalConfig.getString("deviceSn"));
        reqBody.put("productId", thingExternalConfig.getString("productId"));
        reqBody.put("operator", "system");
        reqBody.put("other", JSON.parse("{'autoObserver': 0}"));

        deviceReq.setBody(JSON.toJSONBytes(reqBody));

        // 发送请求
        try {
            log.info("下发AEP设备注册 - 发送");
            log.debug("请求数据: {}", reqBody);
            JSONObject jsonObject = JSON.parseObject(client.CreateDevice(deviceReq).getBody());

//            ThingRequestDTO dto = deviceDataUtil.parseThirdDevice(thingRequestDTO, jsonObject.toJSONString());
//            log.info("获取响应，发送到队列：{}", jsonObject);
//            producer.sendMessage(ThingTopic.THIRD_DEVICE_REGISTER_REPLY, dto);
        } catch (Exception e) {
            log.error("下发AEP设备注册 - 失败\n{}", e.getMessage());
            throw exception(AGENT_REGISTER_DEVICE_TO_AEP_FAILED_BY_REQ);
        }
    }

    @Override
    public ThingServiceData sendCommand(Product product, Thing thing, ThingServiceData serviceData, String rawData) {
        // 获取AEP配置
        JSONObject productExternalConfig = product.getExternalConfig().getConfig();
        String appId = productExternalConfig.getString("appId");
        String appSecret = productExternalConfig.getString("appSecret");
        String masterKey = productExternalConfig.getString("masterKey");

        // 构造请求
        AepDeviceCommandClient client = AepDeviceCommandClient.newClient().appKey(appId).appSecret(appSecret).build();
        CreateCommandRequest commandReq = new CreateCommandRequest();
        commandReq.setPath("/command");
        commandReq.setMethod(RequestFormat.POST());
        commandReq.setParamMasterKey(masterKey);

        // 构造请求体
        JSONObject reqBody = new JSONObject();
        // 设备参数
        JSONObject thingExternalConfig = thing.getExternalConfig().getConfig();
        reqBody.put("productId", productExternalConfig.getString("productId"));
        reqBody.put("deviceId", thingExternalConfig.getString("id"));
        reqBody.put("operator", "system");
        reqBody.put("ttl", 86400);
        reqBody.put("content", JSONObject.parseObject(rawData));

        commandReq.setBody(JSON.toJSONBytes(reqBody));

        // 发送请求
        JSONObject respBody = null;
        try {
            log.info("下发AEP设备命令 - 发送");
            log.debug(reqBody.toString());
            respBody = JSON.parseObject(client.CreateCommand(commandReq).getBody());
            log.debug("下发AEP设备命令 - 响应");
            log.debug(respBody.toString());

        } catch (Exception e) {
            log.error("下发AEP设备指令 - 失败\n{}", e.getMessage());
            serviceData.setStatus(false);
        }

        // 处理响应
        if (respBody != null && StrUtil.equals(respBody.getString("code"), "0")) {
            JSONObject result = respBody.getJSONObject("result");
            serviceData.setCommandId(result.getString("commandId"));
            serviceData.setStatus(true);

            List<ThingServiceData.CallbackRecord> list = new ArrayList<>();
            list.add(ThingServiceData.CallbackRecord.builder()
                    .status(result.getString("commandStatus"))
                    .message(result.getString("commandStatus"))
                    .originData(respBody.toString())
                    .timestamp(Long.parseLong(result.getString("createTime")))
                    .build());
            serviceData.setCallbackRecords(list);
        } else {
            log.error("下发AEP设备指令 - 失败\n{}", respBody);
            serviceData.setStatus(false);
        }

        return serviceData;
    }

    @Override
    public void replyCommand(String message) {
        String SCHEMA = """
            {
              "type": "object",
              "properties": {
                "timestamp": {
                  "type": "integer"
                },
                "deviceId": {
                  "type": "string"
                },
                "productId": {
                  "type": "string"
                },
                "imei": {
                  "type": "string"
                },
                "taskId": {
                  "type": "integer"
                },
                "tenantId": {
                  "type": "string"
                },
                "messageType": {
                  "type": "string"
                },
                "protocol": {
                  "type": "string"
                },
                "result": {
                  "type": "object",
                  "properties": {
                    "resultDetail": {
                      "oneOf": [
                        { "type": "null"},
                        { "type": "string"}
                      ]
                    },
                    "resultCode": {
                      "type": "string"
                    }
                  },
                  "required": ["resultCode"]
                }
              },
              "required": ["deviceId", "taskId", "result"]
            }
            """;

        // 校验指令回调数据
        if (!JSONSchema.parseSchema(SCHEMA).isValid(JSONObject.parseObject(message))) {
            log.info(AGENT_UNKNOWN_DATA_SOURCE.getMsg());
            return;
        }
        log.info("设备来源[{}]", EXTERNAL_TYPE);

        // 获取设备信息
        JSONObject messageJson = JSON.parseObject(message);
        Thing thing = validateThingExists(messageJson);
        if (thing == null) {
            return;
        }

        // 获取指令Id
        String commandId = messageJson.getString("taskId");
        if (StrUtil.isBlank(commandId)) {
            return;
        }

        // 查询thingData
        ThingData thingData = thingDataService.getThingDataByThingIdAndCommandId(thing.getId(), commandId);
        if (!ObjUtil.isNotNull(thingData)) {
            return;
        }

        // 调用脚本解析指令回调数据
        String callback = ThingModelScriptExecutor.rawDataToProtocol(thingData.getProductId(), messageJson.toString());
        if (StrUtil.isBlank(callback)) {
            return;
        }

        JSONObject callbackJson = JSONObject.parseObject(callback);

        // 处理回调状态和历史记录
        thingData.getService().setStatus((Boolean) callbackJson.get("status"));
        ThingServiceData.CallbackRecord callbackRecord = JSON.parseObject(callbackJson.getString("callbackRecord"), ThingServiceData.CallbackRecord.class);
        callbackRecord.setOriginData(message);
        thingData.getService().getCallbackRecords().add(callbackRecord);

        // 更新
        thingDataService.updateServiceData(thing.getId(), thingData);
    }

    @Override
    protected void validatePayload(String originData) {
        super.validatePayload(originData);

        JSONSchema schema = jsonSchemaCache.get(EXTERNAL_TYPE);
        if (schema == null) {
            return;
        }

        if (!schema.isValid(JSON.parseObject(originData))) {
            throw exception(AGENT_AEP_DEVICE_POST_INVALID_DATA);
        }
    }

    private Thing validateThingExists(JSONObject originDataJson) {
        String deviceId = originDataJson.getString("deviceId");
        Thing thing = thingService.getThingByExternalConfigId(deviceId);
        if (ObjUtil.isNull(thing)) {
            log.info("设备不存在[{}]", deviceId);
        } else {
            log.info("匹配到设备[{}]", deviceId);
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
