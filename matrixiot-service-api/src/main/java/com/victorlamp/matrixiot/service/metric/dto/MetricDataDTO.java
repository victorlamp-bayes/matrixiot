package com.victorlamp.matrixiot.service.metric.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricDataDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -8695474933356368976L;
    private String metricDataId;
    private String productId;
    private String thingId;
    private String aggregationType;
    private Integer aggregationFreq;
    private String identifier;
    private String value;
    private Long createdAt;
}
