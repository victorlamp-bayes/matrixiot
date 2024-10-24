package com.victorlamp.matrixiot.service.rule.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.StringUtils;

/**
 * 规则的触发器
 * @author: Dylan
 * @date: 2023/8/29
 */
@Getter
@AllArgsConstructor
public enum RuleTriggerType {

    /**
     * 定时器
     */
    TIMER("定时器", "trigger/timer"),
    /**
     * 设备属性上报
     */
    THING_PROPERTY("设备属性上报", "trigger/thing/property"),
    /**
     * 设备事件上报
     */
    THING_EVENT("设备事件上报", "trigger/thing/event"),
    /**
     * 设备状态变化
     */
    THING_STATUS_CHANGE("设备状态变化", "trigger/thing/statusChange"),
    /**
     * 产品下的任一设备属性上报
     */
    PRODUCT_PROPERTY("产品下的任一设备属性上报", "trigger/product/property"),
    /**
     * 产品下的任一设备事件上报
     */
    PRODUCT_EVENT("产品下的任一设备事件上报", "trigger/product/event"),
    /**
     * 产品下的任一设备状态变化
     */
    PRODUCT_STATUS_CHANGE("产品下的任一设备状态变化", "trigger/product/statusChange"),
    /**
     * 组合触发器，内部支持多个触发器进行逻辑或
     * 暂不支持组合触发器
     */
    @Deprecated
    LOGICAL_OR("逻辑或触发器组合", "logical/or"),
    ;

    /**
     * 条件名称
     */
    private String name;

    /**
     * 条件 uri
     */
    private String uri;

    public static RuleTriggerType getByUri(String uri) {

        if (!StringUtils.hasText(uri)) {
            return null;
        }

        for (RuleTriggerType value : RuleTriggerType.values()) {

            if (value.getUri().equals(uri.trim())) {

                return value;
            }
        }

        return null;
    }
}
