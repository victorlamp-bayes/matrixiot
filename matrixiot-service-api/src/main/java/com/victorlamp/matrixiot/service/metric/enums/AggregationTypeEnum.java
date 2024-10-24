package com.victorlamp.matrixiot.service.metric.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AggregationTypeEnum {

    LATEST(1, "LATEST"),
    COUNT(2, "COUNT"),
    SUM(3, "SUM"),
    MIN(4, "MIN"),
    MAX(5, "MAX"),
    AVG(6, "AVG"),
    INC(7, "INC");

    private final int id;
    private final String label;
}
