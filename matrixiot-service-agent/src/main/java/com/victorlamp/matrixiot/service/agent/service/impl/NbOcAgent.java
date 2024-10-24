package com.victorlamp.matrixiot.service.agent.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.victorlamp.matrixiot.common.exception.util.ServiceExceptionUtil.exception;
import static com.victorlamp.matrixiot.service.agent.constant.ErrorCodeConstants.*;

@Component
@Slf4j
public class NbOcAgent extends Agent {

    private static final String EXTERNAL_TYPE = ExternalTypeEnum.NB_OC.name();

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
        // OC平台上报设备数据时，很多基于产品级推送，如果设备未在HVIOT平台注册，直接返回，不抛异常
        if (thing == null) {
            return;
        }
        // 获取数据内容
        String contentStr = originDataJson.getJSONObject("service").getJSONObject("data").getString("content");
        if (StrUtil.isBlank(contentStr)) {
            log.info("上报数据为空");
            return;
        }

        // 转换为内部协议。忽略解析的空数据，不抛异常
        String protocolData = ThingModelScriptExecutor.rawDataToProtocol(thing.getProduct().getId(), contentStr);
        if (StrUtil.isBlank(protocolData)) {
            log.info("上报数据转换后为空");
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
        // 获取OC配置
        JSONObject productExternalConfig = product.getExternalConfig().getConfig();
        String appId = productExternalConfig.getString("appId");
        String appSecret = productExternalConfig.getString("appSecret");
        String appAuthUrl = productExternalConfig.getString("appAuthUrl");
        String registerUrl = productExternalConfig.getString("registerUrl");

        // 获取accessToken
        String accessToken = getAccessToken4OC(appId, appSecret, appAuthUrl);

        // 构造请求体
        JSONObject reqBody = new JSONObject();
        // 产品参数
        reqBody.put("productId", productExternalConfig.getString("productId"));
        reqBody.put("deviceInfo", productExternalConfig.getString("deviceInfo"));
        reqBody.put("isSecure", productExternalConfig.getString("isSecure"));
        // 设备参数
        JSONObject thingExternalConfig = thing.getExternalConfig().getConfig();
        reqBody.put("imsi", thingExternalConfig.getString("imsi"));
        reqBody.put("psk", thingExternalConfig.getString("psk"));
        reqBody.put("nodeId", thingExternalConfig.getString("nodeId"));
        reqBody.put("timeOut", thingExternalConfig.getString("timeOut"));
        reqBody.put("verifyCode", thingExternalConfig.getString("verifyCode"));
        reqBody.put("endUserId", thingExternalConfig.getString("endUserId"));
        reqBody.put("deviceName", thing.getName());

        // 构造请求头
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("app_key", appId);
        headerMap.put("Authorization", "Bearer " + accessToken);

        // 发送请求
        HttpResponse response = null;
        try {
            log.info("下发OC设备注册 - 发送");
            response = HttpUtil.createPost(registerUrl).headerMap(headerMap, true).body(reqBody.toString()).execute();
            if (!response.isOk()) {
                log.error("下发OC设备注册 - 失败响应\n{}", response.body());
                throw exception(AGENT_REGISTER_DEVICE_TO_OC_FAILED_BY_RESP);
            }

            log.info("下发OC设备注册 - 成功");
//            ThingRequestDTO dto = deviceDataUtil.parseThirdDevice(thingRequestDTO, response.body());
//            log.info("获取响应，发送到队列：{}", response);
//            producer.sendMessage(ThingTopic.THIRD_DEVICE_REGISTER_REPLY, dto);

        } catch (Exception e) {
            log.warn("下发OC设备注册 - 失败\n{}", e.getMessage());
            throw exception(AGENT_REGISTER_DEVICE_TO_OC_FAILED_BY_REQ);
        } finally {
            assert response != null;
            response.close();
        }
    }

    @Override
    public ThingServiceData sendCommand(Product product, Thing thing, ThingServiceData serviceData, String rawData) {
        // 获取OC配置
        JSONObject productExternalConfig = product.getExternalConfig().getConfig();
        String appId = productExternalConfig.getString("appId");
        String appSecret = productExternalConfig.getString("appSecret");
        String appAuthUrl = productExternalConfig.getString("appAuthUrl");
        String commandUrl = productExternalConfig.getString("commandUrl");

        // 获取accessToken
        String accessToken = getAccessToken4OC(appId, appSecret, appAuthUrl);

        // 构造请求体
        JSONObject reqBody = new JSONObject();
        // 产品参数
//        reqBody.put("callbackUrl", productExternalConfig.getString("commandCallbackUrl"));
        // 设备参数
        JSONObject thingExternalConfig = thing.getExternalConfig().getConfig();
        reqBody.put("deviceId", thingExternalConfig.getString("id"));
        reqBody.put("command", JSONObject.parseObject(rawData));
        reqBody.put("maxRetransmit", 3);
        reqBody.put("expireTime", 86400);

        // 构造请求头
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("app_key", appId);
        headerMap.put("Authorization", "Bearer " + accessToken);

        // 发送请求
        HttpResponse response = null;
        try {
            log.info("下发OC平台设备指令 - 发送");
            log.debug("请求数据: {}", reqBody);
            response = HttpUtil.createPost(commandUrl).headerMap(headerMap, true).body(reqBody.toString()).execute();
            if (!response.isOk()) {
                log.error("下发OC平台设备指令 - 失败响应\n{}", response.body());
                serviceData.setStatus(false);
            }

            log.info("下发OC平台设备指令 - 成功");
        } catch (Exception e) {
            log.error("下发OC平台设备指令 - 失败\n{}", e.getMessage());
            serviceData.setStatus(false);
        } finally {
            assert response != null;
            response.close();
        }

        int code = response.getStatus();
        JSONObject respBody = JSON.parseObject(response.body());
        if (code == 201 && ObjUtil.isNotNull(respBody)) {
            serviceData.setCommandId(respBody.getString("commandId"));
            serviceData.setStatus(true);

            long timestamp = parseRespTimestamp(respBody.getString("creationTime"));

            List<ThingServiceData.CallbackRecord> list = new ArrayList<>();
            list.add(ThingServiceData.CallbackRecord.builder()
                    .status(respBody.getString("status"))
                    .message("命令已保存")
                    .originData(respBody.toString())
                    .timestamp(timestamp)
                    .build());
            serviceData.setCallbackRecords(list);
        } else {
            log.error("下发OC平台设备指令 - 失败\n{}", respBody);
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
                "deviceId": {
                  "type": "string"
                },
                "commandId": {
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
              "required": ["deviceId", "commandId", "result"]
            }
            """;

        // 校验指令回调数据
        if (!JSONSchema.parseSchema(SCHEMA).isValid(JSON.parseObject(message))) {
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
        String commandId = messageJson.getString("commandId");
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
        callbackRecord.setTimestamp(System.currentTimeMillis());
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
            throw exception(AGENT_OC_DEVICE_POST_INVALID_DATA);
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

    private static long parseRespTimestamp(String timestamp) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'"));
            return DateUtil.parse(dateTime.toString()).getTime();
        } catch (Exception e) {
            log.error("时间格式错误");
            return System.currentTimeMillis();
        }
    }

    private static String getAccessToken4OC(String appId, String appSecret, String appAuthUrl) {
        String accessToken;
        Map<String, Object> paramMap = MapUtil.newHashMap();
        paramMap.put("appId", appId);
        paramMap.put("secret", appSecret);
        try {
            String resp = HttpUtil.post(appAuthUrl, paramMap);
            JSONObject respBody = JSON.parseObject(resp);
            accessToken = respBody.getString("accessToken");
        } catch (Exception e) {
            log.error("OC平台 - 请求AT失败\n{}", e.getMessage());
            throw exception(AGENT_AUTH_OC_FAILED_BY_AT_REQ);
        }

        if (StrUtil.isBlank(accessToken)) {
            log.error("OC平台 - 获取AT为空");
            throw exception(AGENT_AUTH_OC_FAILED_BY_AT_EMPTY);
        }

        return accessToken;
    }
}
