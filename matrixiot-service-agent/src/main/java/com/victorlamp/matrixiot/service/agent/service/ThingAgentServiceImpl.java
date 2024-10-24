package com.victorlamp.matrixiot.service.agent.service;

import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.schema.JSONSchema;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.victorlamp.matrixiot.common.util.cache.CaffeineCacheUtils;
import com.victorlamp.matrixiot.service.agent.dto.ReplyInvokeThingServiceRequestDTO;
import com.victorlamp.matrixiot.service.agent.dto.ReplySetThingPropertyRequestDTO;
import com.victorlamp.matrixiot.configuration.message.RocketMQTemplateProducer;
import com.victorlamp.matrixiot.service.agent.utils.script.ThingModelScriptExecutor;
import com.victorlamp.matrixiot.service.common.constant.ThingTopic;
import com.victorlamp.matrixiot.service.common.exception.ExceptionInfoBuilder;
import com.victorlamp.matrixiot.service.common.exception.ExceptionTemplate;
import com.victorlamp.matrixiot.service.common.exception.ServiceException;
import com.victorlamp.matrixiot.service.management.api.ProductService;
import com.victorlamp.matrixiot.service.management.api.ThingDataService;
import com.victorlamp.matrixiot.service.management.api.ThingModelService;
import com.victorlamp.matrixiot.service.management.api.ThingService;
import com.victorlamp.matrixiot.service.management.entity.externalconfig.ExternalConfigTemplate;
import com.victorlamp.matrixiot.service.management.entity.product.Product;
import com.victorlamp.matrixiot.service.management.entity.thing.Thing;
import com.victorlamp.matrixiot.service.management.entity.thingdata.ThingData;
import com.victorlamp.matrixiot.service.management.entity.thingdata.ThingServiceData;
import com.victorlamp.matrixiot.service.management.entity.thingmodel.ThingModel;
import com.victorlamp.matrixiot.service.management.enums.ExternalTypeEnum;
import com.victorlamp.matrixiot.service.agent.utils.ThingModelUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.time.Duration;
import java.util.List;

import static com.victorlamp.matrixiot.common.exception.util.ServiceExceptionUtil.exception;
import static com.victorlamp.matrixiot.service.agent.constant.ErrorCodeConstants.*;

@Service("thingAgentService")
@RequiredArgsConstructor
@Slf4j
public class ThingAgentServiceImpl implements ThingAgentService {
    private final RocketMQTemplateProducer mqProducer;

    @DubboReference
    private ProductService productService;

    private List<ExternalConfigTemplate> externalTemplates;

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
    private ThingService thingService;

    @DubboReference
    private ThingDataService thingDataService;

    @Override
    @SneakyThrows
    public void postThingPropertyEvent(String thingId, String payload) {
        validatePayload(payload);

        Thing thing = thingService.getThing(thingId);
        if (ObjUtil.isNull(thing)) {
            throw exception(AGENT_DEVICE_NOT_EXISTS);
        }

        String productId = thing.getProduct().getId();

        // 脚本解析
        String protocolData = payload;
        if (ThingModelScriptExecutor.existsScriptExecutor(productId)) {
            protocolData = ThingModelScriptExecutor.rawDataToProtocol(productId, JSONObject.parseObject(payload).getString("payload"));
        }
        // 获取物模型
        ThingModel thingModel = thingModelCache.get(productId);
        // 检查数据是否合法
        ThingModelUtil.validateThingData(thingModel, protocolData);
        // 构造ThingData
        ThingData thingData = ThingModelUtil.buildThingData(thingModel, thing, protocolData, null);
        // 发送到消息队列
        mqProducer.asyncSendMessage(ThingTopic.THING_PROPERTY_EVENT_POST, thingData);
    }

    @Override
    public void replySetThingProperty(String thingId, ReplySetThingPropertyRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new ServiceException(
                    ServiceException.ExceptionType.INVALID_REQUEST,
                    ExceptionInfoBuilder.build(ExceptionTemplate.INVALID_REQUEST_NULL_VALUE, "属性设置回调信息为空"));
        }

        mqProducer.sendMessage(ThingTopic.THING_PROPERTY_SET_REPLY, requestDTO);
    }

    @Override
    public void replyInvokeThingService(String thingId, ReplyInvokeThingServiceRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new ServiceException(
                    ServiceException.ExceptionType.INVALID_REQUEST,
                    ExceptionInfoBuilder.build(ExceptionTemplate.INVALID_REQUEST_NULL_VALUE, "服务调用回调信息为空"));
        }

        mqProducer.sendMessage(ThingTopic.THING_SERVICE_INVOKE_REPLY, requestDTO);
    }

    @Override
    @SneakyThrows
    public void postOriginData(String originData) {
        log.info("设备上报数据长度[{}]", originData.length());
        log.debug("设备上报数据:{}", originData);

        // 识别外部数据来源类型
        ExternalTypeEnum externalType = identifyExternalType(originData);

        // 根据外部数据来源类型调用对应Agent
        // TODO 重要约定：ExternalType以下划线连接命名，Agent以驼峰命名，命名需保持一致，忽略大小写
        String[] agentNames = SpringUtil.getBeanNamesForType(Agent.class);
        for (String agentName : agentNames) {
            if (StrUtil.startWithIgnoreCase(agentName, StrUtil.toCamelCase(externalType.name()))) {
                Agent agent = SpringUtil.getBean(agentName, Agent.class);
                agent.postThingData(originData);
                return;
            }
        }
    }

    @Override
    public void registerDeviceToExternal(Product product, Thing thing) {
        ExternalTypeEnum externalType = identifyExternalType(product);

        Agent agent = findAgent(externalType.name());
        if (ObjUtil.isNotNull(agent)) {
            agent.registerDevice(product, thing);
        }
    }

    @Override
    public void subscribeToExternal() {

    }

    @Override
    @SneakyThrows
    public void sendCommand(ThingData thingData) {
        Product product = productService.getProduct(thingData.getProductId());
        Thing thing = thingService.getThing(thingData.getThingId());

        // 获取符合条件的Agent
        ExternalTypeEnum externalType = identifyExternalType(product);
        Agent agent = findAgent(externalType.name());

        boolean scriptExecutorExists = ThingModelScriptExecutor.existsScriptExecutor(thingData.getProductId());

        if (ObjUtil.isNotNull(agent)) {
            // 转换脚本
            String rawData = serviceDataTOJsonString(thingData.getService());
            if (scriptExecutorExists) {
                rawData = ThingModelScriptExecutor.protocolToRawData(thingData.getProductId(), rawData);
            }

            log.debug("原始数据[{}]", rawData);

            // 下发命令并获取回调数据
            ThingServiceData callBackData = agent.sendCommand(product, thing, thingData.getService(), rawData);
            if (ObjUtil.isNotNull(callBackData)) {
                thingData.setService(callBackData);
                thingData.setOriginData(rawData);
            }
        }

        // 更新 ThingServiceData
        thingDataService.updateServiceData(thingData.getThingId(), thingData);
    }

    @Override
    public void replyCommandResponse(String message) {
        log.info("AEP指令回调长度[{}]", message.length());
        log.debug("AEP指令回调数据:{}", message);

        // TODO NB_AEP类型
        Agent agent = findAgent(ExternalTypeEnum.NB_AEP.name());
        if (ObjUtil.isNotNull(agent)) {
            agent.replyCommand(message);
        }
    }

    @Override
    public void replyService(String message) {
        log.info("OC指令回调长度[{}]", message.length());
        log.debug("OC指令回调数据:{}", message);

        // TODO NB_OC类型
        Agent agent = findAgent(ExternalTypeEnum.NB_OC.name());
        if (ObjUtil.isNotNull(agent)) {
            agent.replyCommand(message);
        }
    }

    private String serviceDataTOJsonString(ThingServiceData data) {
        JSONObject jsonObject = new JSONObject();
        JSONObject params = new JSONObject();

        jsonObject.put("identifier", data.getIdentifier());
        data.getArgs().forEach(arg -> params.put(arg.getIdentifier(), arg.getValue()));
        jsonObject.put("params", params);

        return jsonObject.toString();
    }

    private Agent findAgent(String externalType) {
        String[] agentNames = SpringUtil.getBeanNamesForType(Agent.class);
        for (String agentName : agentNames) {
            if (StrUtil.startWithIgnoreCase(agentName, StrUtil.toCamelCase(externalType))) {
                return SpringUtil.getBean(agentName, Agent.class);
            }
        }
        return null;
    }

    private ExternalTypeEnum identifyExternalType(String originData) {
        if (externalTemplates == null) {
            externalTemplates = productService.listExternalTemplate();
        }

        for (ExternalConfigTemplate template : externalTemplates) {
            if (ObjUtil.isEmpty(template.getJsonSchema())) {
                continue;
            }

            if (JSONSchema.parseSchema(JSON.toJSONString(template.getJsonSchema())).isValid(JSON.parseObject(originData))) {
                return template.getType();
            }
        }

        throw exception(AGENT_UNKNOWN_DATA_SOURCE);
    }

    private ExternalTypeEnum identifyExternalType(Product product) {
        ExternalTypeEnum externalType = EnumUtil.fromStringQuietly(ExternalTypeEnum.class, StringUtils.toRootUpperCase(product.getExternalConfig().getType()));
        if (externalType == null) {
            throw exception(THING_AGENT_INVALID_EXTERNAL_CONFIG_TYPE);
        }

        return externalType;
    }

    private void validatePayload(String payload) {
        if (StrUtil.isBlank(payload)) {
            log.warn(AGENT_DEVICE_POST_EMPTY_DATA.getMsg());
            throw exception(AGENT_DEVICE_POST_EMPTY_DATA);
        }

        if (!JSON.isValid(payload)) {
            log.error(AGENT_DEVICE_POST_ILLEGAL_DATA.getMsg());
            log.error(payload);
            throw exception(AGENT_DEVICE_POST_ILLEGAL_DATA);
        }
    }
}
