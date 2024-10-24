package com.victorlamp.matrixiot.service.management.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ThingModelDataTypeEnum {
    NUMERIC("Numeric"), TEXT("Text"), ENUM("Enum");
    
    private final String label;
}
