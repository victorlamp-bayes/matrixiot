package com.victorlamp.matrixiot.service.management.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ThingModelServiceParamDirectionEnum {
    INPUT("输入参数"), OUTPUT("输出参数");

    private final String label;
}
