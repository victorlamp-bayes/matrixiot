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
public class NBIoTSendCommandRequestDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 3869554816651106963L;
    private CommandDTO command;
    private String deviceId;
    private String callbackUrl;
    private Integer expireTime = 86400;
    private Integer maxRetransmit = 3;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CommandDTO implements Serializable {
        @Serial
        private static final long serialVersionUID = -1346739661790896784L;
        private String serviceId;
        private String method;
        private Map<String, Object> paras;
    }
}
