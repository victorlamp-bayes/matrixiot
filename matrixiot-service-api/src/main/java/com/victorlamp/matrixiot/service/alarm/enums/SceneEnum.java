package com.victorlamp.matrixiot.service.alarm.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SceneEnum {

    DEFAULT("1", "默认场景");

    private final String id;
    private final String label;

    @JsonCreator
    public static SceneEnum getById(String code) {
        for (SceneEnum value : SceneEnum.values()) {
            if (value.getId().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
