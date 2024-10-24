package com.victorlamp.matrixiot.service.alarm.dto.alarm;

import com.victorlamp.matrixiot.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class AlarmPageReqDTO extends PageParam {
    @Serial
    private static final long serialVersionUID = 5946309609486985504L;

    @Schema(description = "告警设备所属产品ID")
    private String productId;

    @Schema(description = "告警设备ID")
    private String thingId;

    @Schema(description = "告警状态")
    private Boolean status;

    @Schema(description = "告警等级")
    private String level;

    @Schema(description = "告警场景")
    private String scene;

    @Schema(description = "告警通知发送状态")
    private String sendStatus;

    @Schema(description = "告警时间段")
    private List<Long> timeRange;
}
