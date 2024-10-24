package com.victorlamp.matrixiot.service.metric.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemMetricDataReqDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -8134395037289245464L;

    private String identifier;
    private Long startTime;
    private Long endTime;
}
