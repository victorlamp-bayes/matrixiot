package com.victorlamp.matrixiot.service.metric.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class SystemMetricData implements Serializable {
    @Serial
    private static final long serialVersionUID = -4862862001189041127L;

    private Long timestamp;
    private Number value;
}
