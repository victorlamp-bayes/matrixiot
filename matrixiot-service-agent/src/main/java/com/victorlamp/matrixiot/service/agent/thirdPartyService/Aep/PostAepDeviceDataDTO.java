package com.victorlamp.matrixiot.service.agent.thirdPartyService.Aep;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostAepDeviceDataDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -2226526523020802744L;

    private String deviceId;
    private String serviceId;
    private Long timestamp;
    private Map<String, Object> payload;
    private String IMEI;
}
