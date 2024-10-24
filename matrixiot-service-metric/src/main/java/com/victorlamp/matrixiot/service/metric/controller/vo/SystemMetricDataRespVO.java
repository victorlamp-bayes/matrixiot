package com.victorlamp.matrixiot.service.metric.controller.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Schema(description = "实时监控 - 监控数据 Response VO")
@Data
public class SystemMetricDataRespVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 6117750804821631246L;

    private Long timestamp;
    private Number value;
}
