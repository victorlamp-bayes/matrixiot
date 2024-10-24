package com.victorlamp.matrixiot.service.metric.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AggregationFreqEnum {
    MINUTE1("1", "1分钟"),
    MINUTE5("5", "5分钟"),
    MINUTE10("10", "10分钟"),
    MINUTE15("15", "15分钟"),
    MINUTE30("30", "30分钟"),
    HOUR1("60", "1小时"),
    HOUR6("360", "6小时"),
    HOUR12("720", "12小时"),
    DAY1("1440", "1天");

    private final String id;
    private final String label;
}