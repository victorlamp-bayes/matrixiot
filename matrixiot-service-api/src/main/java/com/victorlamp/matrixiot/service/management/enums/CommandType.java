package com.victorlamp.matrixiot.service.management.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommandType {
    SERVICE_INVOKE(1, "服务"),
    PROPERTY_SET(2, "属性");
    private final int id;
    private final String name;
}
