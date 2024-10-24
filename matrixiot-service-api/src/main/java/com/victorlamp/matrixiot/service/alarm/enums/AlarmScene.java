package com.victorlamp.matrixiot.service.alarm.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 告警的场景
 * @author: Dylan
 * @date: 2023/8/22
 */
@AllArgsConstructor
public enum AlarmScene {

    DEFAULT("DEFAULT", "默认告警场景");

    private final String code;
    @Getter
    private final String name;

    @JsonValue
    public String getCode() {
        return code;
    }

    @JsonCreator
    public static AlarmScene getByCode(String code) {

        for (AlarmScene value : AlarmScene.values()) {

            if (value.getCode().equals(code)) {

                return value;
            }
        }

        return null;
    }
}
