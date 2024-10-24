package com.victorlamp.matrixiot.service.management.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import com.victorlamp.matrixiot.service.common.exception.ExceptionInfoBuilder;
import com.victorlamp.matrixiot.service.common.exception.ExceptionTemplate;
import com.victorlamp.matrixiot.service.common.exception.ServiceException;
import com.victorlamp.matrixiot.service.management.dto.thing.InvokeServiceReqDTO;
import com.victorlamp.matrixiot.service.management.entity.thing.Thing;
import com.victorlamp.matrixiot.service.management.entity.thingdata.ThingData;
import com.victorlamp.matrixiot.service.management.entity.thingdata.ThingServiceData;
import com.victorlamp.matrixiot.service.management.entity.thingmodel.ThingModel;
import com.victorlamp.matrixiot.service.management.entity.thingmodel.ThingModelService;
import com.victorlamp.matrixiot.service.management.entity.thingmodel.ThingModelServiceParam;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class ThingServiceDataUtils {

    private static Set<String> getServiceInputParamIdentifiers(String identifier, ThingModel thingModel) {
        return thingModel.getServices().stream()
                .filter(service -> service.getIdentifier().equals(identifier))
                .flatMap(service -> service.getInputParams().stream())
                .map(ThingModelServiceParam::getIdentifier)
                .collect(Collectors.toSet());
    }

    private static void validateServiceData(String identifier, List<ThingServiceData.Arg> args, ThingModel thingModel, Set<String> serviceIdentifiers) {
        // 校验是否符合物模型服务Identifier
        if (!serviceIdentifiers.contains(identifier)) {
            log.info("未定义的服务Identifier:{}", identifier);
            throw new ServiceException(
                    ServiceException.ExceptionType.INVALID_REQUEST,
                    ExceptionInfoBuilder.build(ExceptionTemplate.INVALID_REQUEST_COMMON, "未定义的服务Identifier:" + identifier));
        }

        // 获取符合当前物模型服务的InputParamIdentifier集合
        Set<String> serviceInputParamIdentifiers = getServiceInputParamIdentifiers(identifier, thingModel);

        // 校验是否符合对应物模型服务InputParam的Identifier
        for (ThingServiceData.Arg arg : args) {
            if (!serviceInputParamIdentifiers.contains(arg.getIdentifier())) {
                log.info("未定义{}服务的输入参数Identifier:{}", identifier, arg.getIdentifier());
                throw new ServiceException(
                        ServiceException.ExceptionType.INVALID_REQUEST,
                        ExceptionInfoBuilder.build(ExceptionTemplate.INVALID_REQUEST_COMMON, "未定义" + identifier + "服务的输入参数Identifier:" + arg.getIdentifier()));
            }
        }
    }

    public static void validateServiceData(ThingModel thingModel, ThingServiceData serviceData, InvokeServiceReqDTO reqDTO) {
        Set<String> serviceIdentifiers = new HashSet<>();

        // 获取物模型服务identifiers集合
        if (CollUtil.isNotEmpty(thingModel.getServices())) {
            serviceIdentifiers = thingModel.getServices().stream().map(ThingModelService::getIdentifier).collect(Collectors.toSet());
        }

        // 内部服务数据校验
        if (ObjUtil.isNotEmpty(serviceData)) {
            validateServiceData(serviceData.getIdentifier(), serviceData.getArgs(), thingModel, serviceIdentifiers);
        }

        // 外部服务数据校验
        if (ObjUtil.isNotNull(reqDTO)) {
            List<ThingServiceData.Arg> args = buildServiceArgs(reqDTO.getInputParams());
            validateServiceData(reqDTO.getIdentifier(), args, thingModel, serviceIdentifiers);
        }
    }

    public static ThingData buildThingData(Thing thing, InvokeServiceReqDTO reqDTO) {
        long timestamp = System.currentTimeMillis();

        ThingServiceData serviceData = ThingServiceData.builder()
                .identifier(reqDTO.getIdentifier())
                .status(true)
                .args(buildServiceArgs(reqDTO.getInputParams()))
                .timestamp(timestamp)
                .build();

        ThingData thingData = ThingData.builder()
                .productId(thing.getProduct().getId())
                .thingId(thing.getId())
                .timestamp(timestamp)
                .service(serviceData)
                .build();

        log.debug("服务数据:[{}]", thingData);
        return thingData;
    }

    public static List<ThingServiceData.Arg> buildServiceArgs(List<Map<String, Object>> inputParams) {
        List<ThingServiceData.Arg> args = new ArrayList<>();
        for (Map<String, Object> map : inputParams) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                args.add(ThingServiceData.Arg.builder()
                        .identifier(entry.getKey())
                        .value(entry.getValue().toString())
                        .build());
            }
        }
        return args;
    }
}
