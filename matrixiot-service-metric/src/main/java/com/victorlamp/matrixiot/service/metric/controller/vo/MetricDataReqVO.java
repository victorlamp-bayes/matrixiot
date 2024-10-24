package com.victorlamp.matrixiot.service.metric.controller.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Schema(description = "实时监控 - 监控数据 Request VO")
@Data
public class MetricDataReqVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 2272393290707502629L;

    @Schema(description = "监控项标识符", requiredMode = Schema.RequiredMode.REQUIRED)
    private String identifier;

    @Schema(description = "开始时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long startTime;

    @Schema(description = "结束时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long endTime;
}
