package com.victorlamp.matrixiot.service.metric.dto;

import com.victorlamp.matrixiot.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricPageReqDTO extends PageParam {
    @Serial
    private static final long serialVersionUID = 8770815348365171081L;

    @Schema(description = "搜索关键字，包含字段：id, name, propertyIdentifier, description")
    private String keywords;

    @Schema(description = "度量运行状态")
    private String status;

    @Schema(description = "度量所属产品")
    private String productId;

    @Schema(description = "度量所属设备")
    private String thingId;
}
