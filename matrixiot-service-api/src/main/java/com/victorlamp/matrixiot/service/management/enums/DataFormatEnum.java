package com.victorlamp.matrixiot.service.management.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DataFormatEnum {

    HVIOT("HVIOT标准格式"), CUSTOM("自定义");

    private final String label;
}
