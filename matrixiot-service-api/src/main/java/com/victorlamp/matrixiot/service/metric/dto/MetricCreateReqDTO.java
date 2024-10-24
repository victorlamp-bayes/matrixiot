package com.victorlamp.matrixiot.service.metric.dto;

import com.victorlamp.matrixiot.service.common.constant.ParamPattern;
import com.victorlamp.matrixiot.service.common.validation.annotation.IdHex24;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricCreateReqDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -6204186998247322074L;

    @Schema(description = "度量指标名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "度量指标名称不能为空")
    @Size(min = 2, max = 32, message = "监控指标名称长度为2-32个字符")
    @Pattern(regexp = ParamPattern.NAME, message = ParamPattern.NAME_MESSAGE)
    private String name;

    @Schema(description = "度量指标产品Id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "度量指标产品Id不能为空")
    @IdHex24
    private String productId;

    @Schema(description = "度量指标设备Id")
    private String thingId;

    @Schema(description = "度量指标物模型属性标识符")
    @NotBlank(message = "度量指标物模型属性标识符不能为空")
    private String propertyIdentifier;

    @NotBlank(message = "度量指标聚合类型不能为空")
    private String aggregationType;

    @NotNull(message = "度量指标聚合频率不能为空")
    private Integer aggregationFreq;

    @Size(max = 128, message = "度量指标描述长度不能超过128位")
    private String description;
}