package com.victorlamp.matrixiot.service.alarm.dto.alarm;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AlarmUpdateStatusReqDTO {
    @Schema(description = "告警状态")
    private Boolean status;

    @Schema(description = "消息发送状态")
    private String sendStatus;
}
