package com.victorlamp.matrixiot.service.common.constant;

import java.util.Arrays;
import java.util.List;

/**
 * 场景规则相关常量
 *
 * @author: Dylan
 * @date: 2023/9/12
 */
public class SceneRuleConstant {

    /**
     * 场景规则状态：启用
     */
    public static final int ENABLED = 0;

    /**
     * 场景规则状态：停用
     */
    public static final int DISABLED = 1;

    /**
     * 场景规则逻辑删除：未删除
     */
    public static final int NOT_DELETED = 0;

    /**
     * 场景规则逻辑删除：已删除
     */
    public static final int DELETED = 1;

    /**
     * 默认的时区
     */
    public static final String DEFAULT_TIME_ZONE = "Asia/Shanghai";

    /**
     * 默认的 cron 表达式格式，quartz 支持的格式；
     * 目前仅支持该格式的 cron 字符串。
     */
    public static final String DEFAULT_CRON_TYPE = "quartz";

    /**
     * 默认循环周期：每天
     */
    public static final String DEFAULT_REPEAT = "1,2,3,4,5,6,7";

    /**
     * 允许的比较符
     */
    public static final List<String> ALLOWED_COMPARE_TYPES = Arrays.asList(">", ">=", "==", "!=", "<", "<=", "in");

    /**
     * 设备在线/离线状态：启用
     */
    public static final int THING_ONLINE = 1;

    /**
     * 设备在线/离线状态：启用
     */
    public static final int THING_OFFLINE = 3;

    /**
     * 是否允许组合触发器，目前暂时为不允许，虽然逻辑已经写了，但是组合触发器逻辑会不清晰
     */
    public static final boolean ALLOW_LOGIC_OR_TRIGGER = false;

    /**
     * 场景联动中条件和动作中，表示触发设备的标识符
     * 以条件为例，如果是判断设备属性，当取值为触发设备时，则仅查询触发事件的这个设备的属性。
     */
    public static final String TRIGGERED_DEVICE = "TRIGGERED_DEVICE";

    /**
     * drl 文件中，表示 global 变量名：触发设备
     */
    public static final String TRIGGERED_DEVICE_GLOBAL_NAME = "currentTriggerDevice";

    /**
     * drl 文件中，表示 when 条件的变量名：[触发设备]
     */
    public static final String TRIGGERED_DEVICE_VAR_NAME = "triggerDevice";
}
