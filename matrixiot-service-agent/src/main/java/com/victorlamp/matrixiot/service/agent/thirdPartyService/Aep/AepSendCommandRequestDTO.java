package com.victorlamp.matrixiot.service.agent.thirdPartyService.Aep;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AepSendCommandRequestDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 6604638069260861353L;

    private ContentDTO content;
    private String deviceId;
    private String operator = "system";
    private Integer productId;
    private Integer ttl = 86400;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ContentDTO implements Serializable {
        @Serial
        private static final long serialVersionUID = -5416877032245211905L;

        private String serviceId;
        private String method;
        private Map<String, Object> params;
    }
}
