package com.victorlamp.matrixiot.service.agent.thirdPartyService.OC.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostNBIoTDeviceDataDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 529574084618965126L;

    private String deviceId;
    private String notifyType;
    private String gatewayId;
    private String requestId;
    private NBDeviceService service;

    @Data
    public static class NBDeviceService implements Serializable {
        @Serial
        private static final long serialVersionUID = -3452433637563540208L;

        private String serviceId;
        private String serviceType;
        private String eventTime;
        private Map<String, Object> data;
    }
}
