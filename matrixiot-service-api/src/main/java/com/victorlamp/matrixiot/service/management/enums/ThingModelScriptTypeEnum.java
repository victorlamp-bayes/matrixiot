package com.victorlamp.matrixiot.service.management.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ThingModelScriptTypeEnum {
    PRESET(1, "预置脚本"), JS(2, "Javascript");

    private final Integer id;
    private final String label;
}
