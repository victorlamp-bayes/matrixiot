package com.victorlamp.matrixiot.service.route.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DataRouteStatusEnum {
    RUNNING("RUNNING"),
    STOP("STOP"),
    ABNORMAL("ABNORMAL");

    private final String label;
}
