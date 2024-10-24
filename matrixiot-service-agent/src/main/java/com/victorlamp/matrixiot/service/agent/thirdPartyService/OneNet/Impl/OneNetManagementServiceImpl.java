package com.victorlamp.matrixiot.service.agent.thirdPartyService.OneNet.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service("oneNetManagementService")
@Slf4j
@RequiredArgsConstructor
public class OneNetManagementServiceImpl {
//    private final HttpSendCenter httpSendCenter;
//    private final SignatureGenerator signatureGenerator;
//    private final SendCommandReplyUtils commandReplyUtils;
//    private final RocketMQTemplateProducer producer;
//    private final DeviceOriginDataUtil deviceDataUtil;
//
//    @Override
//    public void registerDevice(ProductDTO product, ThingRequestDTO requestDTO) {
//        ThingThirdPlatformConfigDTO.OneNET oneNetThingConfig = (ThingThirdPlatformConfigDTO.OneNET) requestDTO.getThirdPlatformConfig();
//        ProductThirdPlatformConfigDTO.OneNET oneNetProductConfig = (ProductThirdPlatformConfigDTO.OneNET) product.getProductAttributes().getThirdPlatformConfig();
//
//        String appId = oneNetProductConfig.getAppId();
//        String res = oneNetProductConfig.getRes();
//        String registerUrl = oneNetProductConfig.getRegisterUrl();
//
//        // 构造请求参数
//        OneNetRegisterDeviceRequestDTO deviceRequestDTO = new OneNetRegisterDeviceRequestDTO();
//        deviceRequestDTO.setDevice_name(requestDTO.getThingName());
//        deviceRequestDTO.setProduct_id(oneNetProductConfig.getThirdProductId());
//        deviceRequestDTO.setImei(oneNetThingConfig.getImei());
//        deviceRequestDTO.setImsi(oneNetThingConfig.getImsi());
//
//        String jsonObject = JSON.toJSONString(deviceRequestDTO);
//
//        String token = getToken(res, appId);
//        log.info("获取token: {}", token);
//
//        // 发送请求
//        String response;
//        try {
//            log.info("设备注册请求发送：{}", jsonObject);
//            response = httpSendCenter.post(registerUrl, jsonObject, token);
//            ThingRequestDTO dto = deviceDataUtil.parseThirdDevice(requestDTO, response);
//            log.info("获取响应，发送到队列：{}", response);
//            producer.sendMessage(ThingTopic.THIRD_DEVICE_REGISTER_REPLY, dto);
//        } catch (Exception e) {
//            log.error("oneNet设备创建失败：" + e.getMessage());
//            throw new ServiceException(
//                    ServiceException.ExceptionType.INTERNAL_FAILURE,
//                    ExceptionInfoBuilder.build(ExceptionTemplate.INTERNAL_FAILURE_OTHER, "oneNet设备创建失败"));
//        }
//    }
//
//    @Override
//    public void sendCommand(ThingDTO thingDTO, ThingData thingData, String rawData) {
//        ProductThirdPlatformConfigDTO.OneNET oneNetProductConfig = (ProductThirdPlatformConfigDTO.OneNET) thingDTO.getThingProduct().getProductAttributes().getThirdPlatformConfig();
//        ThingThirdPlatformConfigDTO.OneNET oneNetThingConfig = (ThingThirdPlatformConfigDTO.OneNET) thingDTO.getThirdPlatformConfig();
//
//        String appId = oneNetProductConfig.getAppId();
//        String res = oneNetProductConfig.getRes();
//        String commandUrl = oneNetProductConfig.getCommandUrl();
//        String imei = oneNetThingConfig.getImei();
//
//        int objId = 3200;
//        int objInstId = 0;
//        int executeResId = 5501;
//        String url = commandUrl + "?imei=" + imei +
//                "&obj_id=" + objId +
//                "&obj_inst_id=" + objInstId +
//                "&res_id=" + executeResId +
//                "&valid_time=" + LocalDateTime.now().plusSeconds(20) +
//                "&expired_time=" + LocalDateTime.now().plusDays(2) +
//                "&trigger_msg=" + 4 +
//                "&retry=" + 0 +
//                "&timeout=" + 25;
//
//        String token = getToken(res, appId);
//        log.info("获取token: {}", token);
//
//        ThingData.Command command = thingData.getCommand();
//        String response;
//        try {
//            log.info("指令下发：{}", rawData);
//            response = httpSendCenter.post(url, rawData, token);
//            log.info("指令下发响应：{}", response);
//        } catch (Exception e) {
//            log.error("oneNet指令下发失败：" + e.getMessage());
//            throw new ServiceException(
//                    ServiceException.ExceptionType.INTERNAL_FAILURE,
//                    ExceptionInfoBuilder.build(ExceptionTemplate.INTERNAL_FAILURE_OTHER, "oneNet指令下发失败"));
//        }
//
//        JSONObject json = JSONObject.parseObject(response);
//        if (json.get("msg").equals("succ")) {
//            JSONObject data = JSONObject.parseObject(json.getString("data"));
//            command.setCommandId(data.getString("uuid"));
//            commandReplyUtils.setCachedStatus(thingData, command);
//        } else {
//            log.error("oneNet指令下发失败：" + json);
//            commandReplyUtils.setFailedStatus(thingData, command);
//        }
//    }
//
//    private String getToken(String res, String appId) {
//        try {
//            return signatureGenerator.assembleToken(res, appId);
//        } catch (Exception e) {
//            log.error("oneNet处理签名加密失败，原因：" + e.getMessage());
//            throw new ServiceException(
//                    ServiceException.ExceptionType.INTERNAL_FAILURE,
//                    ExceptionInfoBuilder.build(ExceptionTemplate.INTERNAL_FAILURE_OTHER, "oneNet处理签名加密失败"));
//        }
//    }
//
//    @PostConstruct
//    public void afterPropertiesSet() {
//        httpSendCenter.init();
//    }
}
