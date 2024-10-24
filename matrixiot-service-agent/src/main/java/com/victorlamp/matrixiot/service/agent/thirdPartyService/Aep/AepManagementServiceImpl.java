package com.victorlamp.matrixiot.service.agent.thirdPartyService.Aep;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class AepManagementServiceImpl {
//    private final RocketMQTemplateProducer producer;
//    private final SendCommandReplyUtils commandReplyUtils;
//    private final DeviceOriginDataUtil deviceDataUtil;
//
//    @Override
//    public void registerDevice(ProductDTO product, ThingRequestDTO thingRequestDTO) {
//        // 获取aep配置
//        ThingThirdPlatformConfigDTO.Aep aepThingConfig = (ThingThirdPlatformConfigDTO.Aep) thingRequestDTO.getThirdPlatformConfig();
//        ProductThirdPlatformConfigDTO.Aep aepProductConfig = (ProductThirdPlatformConfigDTO.Aep) product.getProductAttributes().getThirdPlatformConfig();
//        String appId = aepProductConfig.getAppId();
//        String appSecret = aepProductConfig.getAppSecret();
//
//        // 构造请求
//        AepDeviceManagementClient client = AepDeviceManagementClient.newClient().appKey(appId).appSecret(appSecret).build();
//        CreateDeviceRequest device = new CreateDeviceRequest();
//        device.setPath("/device");
//        device.setMethod(RequestFormat.POST());
//        device.setParamMasterKey(aepProductConfig.getMasterKey());
//
//        AepRegisterDeviceRequestDTO aepDevice = new AepRegisterDeviceRequestDTO();
//        aepDevice.setDeviceName(thingRequestDTO.getThingName());
//        aepDevice.setImei(aepThingConfig.getImei());
//        aepDevice.setDeviceSn(aepThingConfig.getDeviceSn());
//        aepDevice.setOther(new AepRegisterDeviceRequestDTO.AepOther());
//        aepDevice.setProductId(aepProductConfig.getThirdProductId());
//
//        device.setBody(JSON.toJSONBytes(aepDevice));
//
//        // 发送请求
//        try {
//            log.info("发送注册设备请求：{}", aepDevice);
//            JSONObject jsonObject = JSON.parseObject(client.CreateDevice(device).getBody());
//            ThingRequestDTO dto = deviceDataUtil.parseThirdDevice(thingRequestDTO, jsonObject.toJSONString());
//            log.info("获取响应，发送到队列：{}", jsonObject);
//            producer.sendMessage(ThingTopic.THIRD_DEVICE_REGISTER_REPLY, dto);
//        } catch (Exception e) {
//            log.error("aep设备创建失败：" + e.getMessage());
//            throw new ServiceException(
//                    ServiceException.ExceptionType.INTERNAL_FAILURE,
//                    ExceptionInfoBuilder.build(ExceptionTemplate.INTERNAL_FAILURE_OTHER, "aep设备创建失败"));
//        }
//    }
//
//    @Override
//    public void sendCommand(ThingDTO thingDTO, ThingData thingData, String rawData) {
//        ProductThirdPlatformConfigDTO.Aep aepProductConfig = (ProductThirdPlatformConfigDTO.Aep) thingDTO.getThingProduct().getProductAttributes().getThirdPlatformConfig();
//        String appId = aepProductConfig.getAppId();
//        String appSecret = aepProductConfig.getAppSecret();
//
//        AepDeviceCommandClient client = AepDeviceCommandClient.newClient().appKey(appId).appSecret(appSecret).build();
//        CreateCommandRequest device = new CreateCommandRequest();
//        device.setPath("/command");
//        device.setMethod(RequestFormat.POST());
//        device.setParamMasterKey(aepProductConfig.getMasterKey());
//
//        AepSendCommandRequestDTO sendCommandDTO = new AepSendCommandRequestDTO();
//        ThingThirdPlatformConfigDTO.Aep aepThingConfig = (ThingThirdPlatformConfigDTO.Aep) thingDTO.getThirdPlatformConfig();
//        sendCommandDTO.setDeviceId(aepThingConfig.getThirdDeviceId());
//        sendCommandDTO.setProductId(aepProductConfig.getThirdProductId());
//        sendCommandDTO.setContent(JSONObject.parseObject(rawData, AepSendCommandRequestDTO.ContentDTO.class));
//
//        device.setBody(JSON.toJSONBytes(sendCommandDTO));
//
//        // 发送请求
//        JSONObject jsonObject;
//        try {
//            log.info("指令下发：{}", rawData);
//            jsonObject = JSON.parseObject(client.CreateCommand(device).getBody());
//            log.info("指令下发响应：{}", jsonObject);
//        } catch (Exception e) {
//            log.error("aep设备下发指令失败：" + e.getMessage());
//            throw new ServiceException(
//                    ServiceException.ExceptionType.INTERNAL_FAILURE,
//                    ExceptionInfoBuilder.build(ExceptionTemplate.INTERNAL_FAILURE_OTHER, "aep设备下发指令失败"));
//        }
//
//        ThingData.Command command = thingData.getCommand();
//        if (jsonObject.get("code").toString().equals("0")) {
//            SendCommandResponseDTO responseDTO = JSONObject.parseObject(jsonObject.get("result").toString(), SendCommandResponseDTO.class);
//            command.setCommandId(responseDTO.getCommandId().toString());
//            commandReplyUtils.setCachedStatus(thingData, command);
//        } else {
//            log.error("aep设备下发指令失败：" + jsonObject);
//            commandReplyUtils.setFailedStatus(thingData, command);
//        }
//    }
}
