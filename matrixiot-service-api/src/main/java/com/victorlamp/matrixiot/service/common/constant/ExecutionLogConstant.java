package com.victorlamp.matrixiot.service.common.constant;

import java.util.Arrays;
import java.util.List;

/**
 * 规则执行日志用的常量
 *
 * @author: Dylan
 * @date: 2023/9/12
 */
public class ExecutionLogConstant {

    /**
     * 逻辑删除：未删除
     */
    public static final int NOT_DELETED = 0;

    /**
     * 逻辑删除：已删除
     */
    public static final int DELETED = 1;

    /**
     * 日志执行状态：待执行
     */
    public static final int TODO_STATUS = 0;

    /**
     * 日志执行状态：执行中
     */
    public static final int EXECUTING_STATUS = 1;

    /**
     * 日志执行状态：执行成功
     */
    public static final int SUCCESS_STATUS = 2;

    /**
     * 日志执行状态：执行失败（首次）
     */
    public static final int FAILURE_STATUS = 9;

    /**
     * 日志执行状态：已重试
     */
    public static final int HAS_RETRIED_STATUS = 10;

    /**
     * 支持的日志执行状态
     */
    public static final List<Integer> ALLOW_STATUS = Arrays.asList(TODO_STATUS, EXECUTING_STATUS, SUCCESS_STATUS, FAILURE_STATUS, HAS_RETRIED_STATUS);

}
