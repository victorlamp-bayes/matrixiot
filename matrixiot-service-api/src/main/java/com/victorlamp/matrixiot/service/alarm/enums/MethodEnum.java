package com.victorlamp.matrixiot.service.alarm.enums;

import cn.hutool.core.util.ArrayUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.victorlamp.matrixiot.common.core.StringArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MethodEnum implements StringArrayValuable {

    NULL("1", "站内消息"),
    SMS("2", "短信"),
    EMAIL("3", "邮件"),
    WEBHOOK("4", "自定义回调");

    public static final String[] ARRAYS = ArrayUtil.map(values(), String.class, MethodEnum::getId);

    private final String id;
    private final String label;

    @Override
    public String[] array() {
        return ARRAYS;
    }

    @JsonCreator
    public static MethodEnum getById(String code) {
        for (MethodEnum value : MethodEnum.values()) {
            if (value.getId().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
