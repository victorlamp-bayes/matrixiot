package com.victorlamp.matrixiot.service.rule.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.StringUtils;

/**
 * 规则的条件
 * @author: Dylan
 * @date: 2023/8/29
 */
@Getter
@AllArgsConstructor
public enum RuleConditionType {

    /**
     * 时间区间条件
     */
    TIME_RANGE("时间区间条件", "condition/timeRange"),
    /**
     * 设备属性条件
     */
    THING_PROPERTY("设备属性条件", "condition/thing/property"),
    /**
     * 设备状态条件
     */
    THING_STATUS("设备状态条件", "condition/thing/status"),
    /**
     * 监控指标条件
     */
    METRIC("监控指标条件", "condition/metric"),
    /**
     * 组合条件，内部支持多个条件进行逻辑与
     */
    LOGICAL_AND("逻辑与条件组合", "logical/and"),
    ;

    /**
     * 条件名称
     */
    private String name;

    /**
     * 条件 uri
     */
    private String uri;

    public static RuleConditionType getByUri(String uri) {

        if (!StringUtils.hasText(uri)) {
            return null;
        }

        for (RuleConditionType value : RuleConditionType.values()) {

            if (value.getUri().equals(uri.trim())) {

                return value;
            }
        }

        return null;
    }
}
