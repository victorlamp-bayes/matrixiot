package com.victorlamp.matrixiot.service.agent.thirdPartyService.OC.dto;

import com.victorlamp.matrixiot.service.management.dto.product.ProductThirdPlatformConfigDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NBIoTRegisterDeviceRequestDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 7937183813108914282L;

    private ProductThirdPlatformConfigDTO.NBIoT.DeviceInfo deviceInfo;
    private String productId;
    private String deviceId;
    private Boolean isSecure;
    private String endUserId;
    private String imsi;
    private String nodeId;
    private String psk;  //测试时随意输入的8位
    private Integer timeOut;
    private String verifyCode; //测试时使用应用ID
    private String deviceName;

}
