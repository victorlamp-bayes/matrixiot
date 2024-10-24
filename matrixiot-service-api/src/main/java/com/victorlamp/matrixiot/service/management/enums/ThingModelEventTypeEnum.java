package com.victorlamp.matrixiot.service.management.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ThingModelEventTypeEnum {
    INFO("info"), WARNING("warning"), ERROR("error");
    
    private final String label;
}
