package com.victorlamp.matrixiot.service.alarm.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum AlarmLevel {

    INFO(0, "Info"),
    ALARM(1, "Alarm"),
    ERROR(2,"Error");

    private final Integer code;
    @Getter
    private final String name;
    @JsonValue
    public Integer getCode() {
        return code;
    }

    @JsonCreator
    public static AlarmLevel getByCode(Integer code) {

        for (AlarmLevel value : AlarmLevel.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }

}
