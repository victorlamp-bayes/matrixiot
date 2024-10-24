package com.victorlamp.matrixiot.service.management.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ThirdPartyUtils {
//    private static final String AEP = "AEP";
//    private static final String NBIoT = "NBIoT";
//    private static final String OneNET = "OneNET";
//    @DubboReference(timeout = 10000, async = true)
//    private final ThirdPartyService registerDeviceService;
//    @DubboReference(timeout = 10000)
//    private final NBIoTSubscribeService nbIoTSubscribeService;
//
//    public ThingDTO registerDevice(ThingRequestDTO requestDTO, ProductDTO product) {
//        String thirdPlatform = product.getProductAttributes().getThirdPlatform();
//        Set<String> allowedTypes = new HashSet<>(Arrays.asList(AEP, NBIoT, OneNET));
//
//        if (!StringUtils.equals(thirdPlatform, requestDTO.getThirdPlatform())) {
//            throw new ServiceException(
//                    ServiceException.ExceptionType.INVALID_REQUEST,
//                    ExceptionInfoBuilder.build(ExceptionTemplate.INVALID_REQUEST_COMMON, "产品、设备的三方类型不一致"));
//        }
//
//        // 目前AEP/NBIoT/OneNET需要调用三方注册设备接口，其余类型暂未提供
//        if (allowedTypes.contains(thirdPlatform)) {
//            try {
//                registerDeviceService.registerDevice(requestDTO, product);
//            } catch (Exception e) {
//                log.warn("调用三方设备注册服务失败：" + e.getMessage());
//                throw new ServiceException(
//                        ServiceException.ExceptionType.INTERNAL_FAILURE,
//                        ExceptionInfoBuilder.build(ExceptionTemplate.INTERNAL_FAILURE_OTHER, "调用三方设备注册服务失败：" + e.getMessage()));
//            }
//
//            ThingDTO dto = new ThingDTO();
//            dto.setMsg("注册请求已发送，等待处理");
//            return dto;
//        }
//
//        return null;
//    }
//
//    public void NBIoTSubscribe(Product product) {
//        if (product.getAttributes().getThirdPlatformConfig() instanceof ProductThirdPlatformConfig.NBIoT nbIoT && nbIoT.getIsSubscribe()) {
//            String response;
//            try {
//                response = nbIoTSubscribeService.NBIoTSubscribe(nbIoT);
//            } catch (Exception e) {
//                log.warn("调用NBIoT订阅服务失败：" + e.getMessage());
//                throw new ServiceException(
//                        ServiceException.ExceptionType.INTERNAL_FAILURE,
//                        ExceptionInfoBuilder.build(ExceptionTemplate.INTERNAL_FAILURE_OTHER, "调用NBIoT订阅服务失败：" + e.getMessage()));
//            }
//
//            JSONObject jsonObject = JSONObject.parseObject(response);
//            if (!jsonObject.containsKey("subscriptionId")) {
//                log.warn("NBIoT订阅服务失败：" + jsonObject);
//                throw new ServiceException(
//                        ServiceException.ExceptionType.INTERNAL_FAILURE,
//                        ExceptionInfoBuilder.build(ExceptionTemplate.INTERNAL_FAILURE_OTHER, "NBIoT订阅服务失败：" + jsonObject));
//            }
//        }
//    }
}
