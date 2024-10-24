package com.victorlamp.matrixiot.service.rule.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.StringUtils;

/**
 * 规则的动作
 * @author: Dylan
 * @date: 2023/8/29
 */
@Getter
@AllArgsConstructor
public enum RuleActionType {

    /**
     * 设置设备属性
     */
    THING_SET_PROPERTY("设置设备属性", "action/thing/setProperty"),
    /**
     * 调用设备服务
     */
    THING_INVOKE_SERVICE("调用设备服务", "action/thing/invokeService"),
    /**
     * 触发其它场景联动规则
     */
    RULE_TRIGGER("触发其它场景联动规则", "action/rule/trigger"),
    /**
     * 启动其它场景联动规则
     */
    RULE_ENABLE("启动其它场景联动规则", "action/rule/enable"),
    /**
     * 停止其它场景联动规则
     */
    RULE_DISABLE("停止其它场景联动规则", "action/rule/disable"),
    /**
     * 发送告警
     */
    ALARM("发送告警", "action/alarm"),
    ;

    /**
     * 条件名称
     */
    private String name;

    /**
     * 条件 uri
     */
    private String uri;

    public static RuleActionType getByUri(String uri) {

        if (!StringUtils.hasText(uri)) {
            return null;
        }

        for (RuleActionType value : RuleActionType.values()) {

            if (value.getUri().equals(uri.trim())) {

                return value;
            }
        }

        return null;
    }
}
