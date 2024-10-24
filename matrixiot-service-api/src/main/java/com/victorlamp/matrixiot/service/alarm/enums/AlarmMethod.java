package com.victorlamp.matrixiot.service.alarm.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 告警的通知渠道
 * @author: Dylan
 * @date: 2023/8/22
 */
@AllArgsConstructor
public enum AlarmMethod {

    /**
     * NULL：不发消息
     */
    NULL("NULL", "站内消息"),
    SMS("SMS", "短信"),
    EMAIL("EMAIL", "邮件"),
    WEBHOOK("WEBHOOK", "自定义回调"),
    ;

    private final String code;
    @Getter
    private final String name;

    @JsonValue
    public String getCode() {
        return code;
    }

    @JsonCreator
    public static AlarmMethod getByCode(String code) {

        for (AlarmMethod value : AlarmMethod.values()) {

            if (value.getCode().equals(code)) {

                return value;
            }
        }

        return null;
    }
}
