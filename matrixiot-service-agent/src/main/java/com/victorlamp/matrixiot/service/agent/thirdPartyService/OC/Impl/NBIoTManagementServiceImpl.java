package com.victorlamp.matrixiot.service.agent.thirdPartyService.OC.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class NBIoTManagementServiceImpl {
//    public static final String HEADER_APP_KEY = "app_key";
//    public static final String HEADER_APP_AUTH = "Authorization";
//    private final HttpsUtil httpsUtil;
//    private final AuthenticationService authenticationService;
//    private final SendCommandReplyUtils commandReplyUtils;
//    private final RocketMQTemplateProducer producer;
//    private final DeviceOriginDataUtil deviceDataUtil;
//
//    private Map<String, String> buildGeneralHeader(String appId, String accessToken) {
//        Map<String, String> header = new HashMap<>();
//        header.put(HEADER_APP_KEY, appId);
//        header.put(HEADER_APP_AUTH, "Bearer" + " " + accessToken);
//        return header;
//    }
//
//    @Override
//    public void registerDevice(ProductDTO product, ThingRequestDTO thingRequestDTO) {
//        // 获取NBIoT配置
//        ThingThirdPlatformConfigDTO.NBIoT nbThingConfig = (ThingThirdPlatformConfigDTO.NBIoT) thingRequestDTO.getThirdPlatformConfig();
//        ProductThirdPlatformConfigDTO.NBIoT nbProductConfig = (ProductThirdPlatformConfigDTO.NBIoT) product.getProductAttributes().getThirdPlatformConfig();
//
//        String appId = nbProductConfig.getAppId();
//        String appSecret = nbProductConfig.getAppSecret();
//        String appAuthUrl = nbProductConfig.getAppAuthUrl();
//        String registerUrl = nbProductConfig.getRegisterUrl();
//
//        // 获取accessToken
//        String accessToken = authenticationService.getAccessToken(appId, appSecret, appAuthUrl);
//        // 构造请求
//        NBIoTRegisterDeviceRequestDTO requestDTO = buildDTO(nbProductConfig, nbThingConfig);
//        requestDTO.setDeviceName(thingRequestDTO.getThingName());
//        String jsonRequest = JSON.toJSONString(requestDTO);
//        Map<String, String> header = buildGeneralHeader(appId, accessToken);
//
//        // 发送请求
//        StreamClosedHttpResponse response;
//        try {
//            log.info("发送注册设备请求：{}", jsonRequest);
//            response = httpsUtil.doPostJsonGetStatusLine(registerUrl, header, jsonRequest);
//            ThingRequestDTO dto = deviceDataUtil.parseThirdDevice(thingRequestDTO, response.getContent());
//            log.info("获取响应，发送到队列：{}", response);
//            producer.sendMessage(ThingTopic.THIRD_DEVICE_REGISTER_REPLY, dto);
//        } catch (Exception e) {
//            log.error("NBIoT设备创建失败：" + e.getMessage());
//            throw new ServiceException(
//                    ServiceException.ExceptionType.INTERNAL_FAILURE,
//                    ExceptionInfoBuilder.build(ExceptionTemplate.INTERNAL_FAILURE_OTHER, "NBIoT设备创建失败"));
//        }
//    }
//
//    @Override
//    public String nbIoTSubscribe(ProductThirdPlatformConfig.NBIoT nbIoT) {
//        String appId = nbIoT.getAppId();
//        String appSecret = nbIoT.getAppSecret();
//        String appAuthUrl = nbIoT.getAppAuthUrl();
//        String subscribeUrl = nbIoT.getSubscribeUrl();
//
//        // 获取accessToken
//        String accessToken = authenticationService.getAccessToken(appId, appSecret, appAuthUrl);
//        // 构造请求
//        SubscriptionRequestDTO requestDTO = new SubscriptionRequestDTO();
//        requestDTO.setAppId(appId);
//        requestDTO.setNotifyType(nbIoT.getNotifyType());
//        requestDTO.setCallbackUrl(nbIoT.getSubscribeCallbackUrl());
//
//        String jsonRequest = JSON.toJSONString(requestDTO);
//        Map<String, String> header = buildGeneralHeader(appId, accessToken);
//
//        // 发送请求
//        StreamClosedHttpResponse response;
//        try {
//            log.info("发送订阅请求：{}", jsonRequest);
//            response = httpsUtil.doPostJsonGetStatusLine(subscribeUrl, header, jsonRequest);
//            log.info("指令下发响应：{}", response);
//            return response.getContent();
//        } catch (Exception e) {
//            log.error("NBIoT订阅服务失败：" + e.getMessage());
//            throw new ServiceException(
//                    ServiceException.ExceptionType.INTERNAL_FAILURE,
//                    ExceptionInfoBuilder.build(ExceptionTemplate.INTERNAL_FAILURE_OTHER, "NBIoT订阅服务失败"));
//        }
//    }
//
//    @Override
//    public void sendCommand(ThingDTO thingDTO, ThingData thingData, String rawData) {
//        ProductThirdPlatformConfigDTO.NBIoT nbIoTProductConfig = (ProductThirdPlatformConfigDTO.NBIoT) thingDTO.getThingProduct().getProductAttributes().getThirdPlatformConfig();
//        ThingThirdPlatformConfigDTO.NBIoT nbThingConfig = (ThingThirdPlatformConfigDTO.NBIoT) thingDTO.getThirdPlatformConfig();
//
//        String appId = nbIoTProductConfig.getAppId();
//        String appSecret = nbIoTProductConfig.getAppSecret();
//        String appAuthUrl = nbIoTProductConfig.getAppAuthUrl();
//        String commandUrl = nbIoTProductConfig.getCommandUrl();
//
//        // 获取accessToken
//        String accessToken = authenticationService.getAccessToken(appId, appSecret, appAuthUrl);
//        Map<String, String> header = buildGeneralHeader(appId, accessToken);
//
//        // 构造请求
//        NBIoTSendCommandRequestDTO sendCommandDTO = new NBIoTSendCommandRequestDTO();
//        sendCommandDTO.setCallbackUrl(nbIoTProductConfig.getCommandCallbackUrl());
//        sendCommandDTO.setDeviceId(nbThingConfig.getThirdDeviceId());
//        sendCommandDTO.setCommand(JSONObject.parseObject(rawData, NBIoTSendCommandRequestDTO.CommandDTO.class));
//        String jsonRequest = JSON.toJSONString(sendCommandDTO);
//
//        StreamClosedHttpResponse response;
//        try {
//            log.info("指令下发：{}", rawData);
//            response = httpsUtil.doPostJsonGetStatusLine(commandUrl, header, jsonRequest);
//            log.info("获取响应：{}", response);
//        } catch (Exception e) {
//            log.error("NBIoT指令下发失败：" + e.getMessage());
//            throw new ServiceException(
//                    ServiceException.ExceptionType.INTERNAL_FAILURE,
//                    ExceptionInfoBuilder.build(ExceptionTemplate.INTERNAL_FAILURE_OTHER, "NBIoT指令下发失败"));
//        }
//
//        JSONObject jsonObject = JSON.parseObject(response.getContent());
//        int code = response.getStatusLine().getStatusCode();
//
//        ThingData.Command command = thingData.getCommand();
//        if (code == 201 && jsonObject != null) {
//            command.setCommandId(jsonObject.getString("commandId"));
//            commandReplyUtils.setCachedStatus(thingData, command);
//        } else {
//            log.error("指令下发失败：" + response.getStatusLine().getReasonPhrase());
//            commandReplyUtils.setFailedStatus(thingData, command);
//        }
//    }
//
//    private NBIoTRegisterDeviceRequestDTO buildDTO(ProductThirdPlatformConfigDTO.NBIoT nbProductConfig, ThingThirdPlatformConfigDTO.NBIoT nbThingConfig) {
//        NBIoTRegisterDeviceRequestDTO nbRequestDTO = new NBIoTRegisterDeviceRequestDTO();
//        nbRequestDTO.setDeviceInfo(nbProductConfig.getDeviceInfo());
//        nbRequestDTO.setProductId(nbProductConfig.getThirdProductId());
//        nbRequestDTO.setIsSecure(nbProductConfig.getIsSecure());
//        nbRequestDTO.setImsi(nbThingConfig.getImsi());
//        nbRequestDTO.setPsk(nbThingConfig.getPsk());
//        nbRequestDTO.setNodeId(nbThingConfig.getNodeId());
//        nbRequestDTO.setTimeOut(nbThingConfig.getTimeOut());
//        nbRequestDTO.setVerifyCode(nbThingConfig.getVerifyCode());
//        nbRequestDTO.setEndUserId(nbThingConfig.getEndUserId());
//
//        return nbRequestDTO;
//    }
}
