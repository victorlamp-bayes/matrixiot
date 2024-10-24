package com.victorlamp.matrixiot.service.agent.thirdPartyService.Aep;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AepRegisterDeviceRequestDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 4102862937853190162L;
    private String deviceName;
    private String deviceSn;
    private String imei;
    private String operator = "system";
    private AepOther other;
    private Integer productId;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AepOther implements Serializable {
        @Serial
        private static final long serialVersionUID = -3945794463398567442L;
        private Integer autoObserver = 0;
    }
}
