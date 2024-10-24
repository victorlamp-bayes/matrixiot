package com.victorlamp.matrixiot.service.agent.thirdPartyService.OneNet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OneNetRegisterDeviceRequestDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 8644510528091080529L;

    private String product_id;
    private String device_name;
    private String imei;
    private String imsi;

//    private String title;
//    private String imei;
//    private String imsi;
//
//    private String protocol = "LWM2M";
//    private AuthInfo auth_info;
//
//    @Data
//    public static class AuthInfo implements Serializable {
//        @Serial
//        private static final long serialVersionUID = -4331324942364160476L;
//        private String imsi;
//        private String imei;
//    }

}
