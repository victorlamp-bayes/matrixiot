package com.victorlamp.matrixiot.service.alarm.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 告警的状态
 * @author: Dylan
 * @date: 2023/8/22
 */
@AllArgsConstructor
public enum AlarmStatus {

    TO_BE_CONFIRMED(0, "待确认"),
    HAS_CONFIRMED(1, "已确认"),
    ;

    private final Integer code;
    @Getter
    private final String name;

    @JsonValue
    public Integer getCode() {
        return code;
    }

    @JsonCreator
    public static AlarmStatus getByCode(Integer code) {

        for (AlarmStatus value : AlarmStatus.values()) {

            if (value.getCode().equals(code)) {

                return value;
            }
        }

        return null;
    }
}
