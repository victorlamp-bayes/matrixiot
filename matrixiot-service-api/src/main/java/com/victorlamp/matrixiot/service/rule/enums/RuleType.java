package com.victorlamp.matrixiot.service.rule.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;

/**
 * 规则类型
 * @author: Dylan
 * @date: 2023/8/22
 */
@AllArgsConstructor
public enum RuleType {

    /**
     * 场景联动规则
     */
    SCENE_LINKAGE("IFTTT");

    private String code;

    @JsonValue
    public String getCode() {
        return code;
    }

    @JsonCreator
    public static RuleType getByCode(String code) {

        for (RuleType value : RuleType.values()) {

            if (value.getCode().equals(code)) {

                return value;
            }
        }

        return null;
    }
}
