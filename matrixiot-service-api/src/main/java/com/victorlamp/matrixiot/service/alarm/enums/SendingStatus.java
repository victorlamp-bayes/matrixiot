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
public enum SendingStatus {

    TO_DO(0, "待发送"),
    DONE(1, "已发送"),
    FAILURE(2, "发送失败"),
    CANCELED(3, "取消发送"),
    ;

    private final Integer code;
    @Getter
    private final String name;

    @JsonValue
    public Integer getCode() {
        return code;
    }

    @JsonCreator
    public static SendingStatus getByCode(Integer code) {

        for (SendingStatus value : SendingStatus.values()) {

            if (value.getCode().equals(code)) {

                return value;
            }
        }

        return null;
    }
}
