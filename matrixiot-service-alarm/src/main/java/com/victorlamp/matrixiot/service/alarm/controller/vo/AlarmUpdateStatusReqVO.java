package com.victorlamp.matrixiot.service.alarm.controller.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AlarmUpdateStatusReqVO {

    @Schema(description = "告警状态")
    private Boolean status;
}
