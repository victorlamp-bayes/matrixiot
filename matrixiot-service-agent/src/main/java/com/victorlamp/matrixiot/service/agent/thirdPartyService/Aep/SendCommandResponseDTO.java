package com.victorlamp.matrixiot.service.agent.thirdPartyService.Aep;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendCommandResponseDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 8795475583475580648L;

    private Number commandId;
    private String commandStatus;
    private String deviceId;
    private Number productId;
    private String createBy;
    private Long createTime;
    private String command;
}
