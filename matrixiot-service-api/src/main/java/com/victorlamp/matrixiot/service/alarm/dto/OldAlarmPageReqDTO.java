package com.victorlamp.matrixiot.service.alarm.dto;

import com.victorlamp.matrixiot.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@Data
public class OldAlarmPageReqDTO extends PageParam {
    @Serial
    private static final long serialVersionUID = 5946309609486985504L;

    @Schema(description = "告警设备所属产品ID")
    private String productId;

    @Schema(description = "告警设备ID")
    private String thingId;

    @Schema(description = "告警状态: 0-待确认；1-已确认；")
    private Integer alarmStatus;

    @Schema(description = "告警等级: 1,2,3,4,5")
    private Integer alarmLevel;

    @Schema(description = "告警场景: 目前只支持 DEFAULT")
    private String alarmScene;

    @Schema(description = "开始时间: 值为13位时间戳")
    private Long startTime;

    @Schema(description = "结束时间: 值为13位时间戳")
    private Long endTime;
}
