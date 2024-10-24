package com.victorlamp.matrixiot.service.metric.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class MetricData implements Serializable {
    @Serial
    private static final long serialVersionUID = -6116314289946672546L;

    @Id
    private String id;
    private String productId;
    private String thingId;
    private String aggregationType;
    private Integer aggregationFreq;
    private String identifier;
    private String value;
    private Long createdAt;
}
