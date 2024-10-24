package com.victorlamp.matrixiot.service.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 异常消息的模板
 * messageTemplate 说明：
 * - "{}"：占位符，在实际使用时替换。
 *
 * @author: Dylan
 * @date: 2023/10/25
 */
@Getter
@AllArgsConstructor
public enum ExceptionTemplate {

    // INVALID_REQUEST
    INVALID_REQUEST(400, "无效请求"),
    INVALID_REQUEST_COMMON(40000, "{}"),
    INVALID_REQUEST_NULL_VALUE(40001, "{}为空"),
    INVALID_REQUEST_REPETITION(40002, "{}重复"),
    INVALID_REQUEST_INVALID_FORMAT(40003, "{}格式无效"),
    INVALID_REQUEST_INVALID_FORMAT_2(40003, "{}无效，合法示例：{}"),
    INVALID_REQUEST_LESS_THEN_MIN(40004, "{}低于最小阈值"),
    INVALID_REQUEST_GREAT_THEN_MAX(40005, "{}超过最大阈值"),

    // RESOURCE_ALREADY_EXISTS
    RESOURCE_ALREADY_EXISTS_NAME(40901, "{}重复"),

    // RESOURCE_NOT_FOUND
    RESOURCE_NOT_FOUND(404, "资源不存在"),
    RESOURCE_NOT_FOUND_COMMON(40400, "{}不存在"),

    // INTERNAL_FAILURE
    INTERNAL_FAILURE(500, "内部服务异常"),
    INTERNAL_FAILURE_COMMON(50000, "{}"),
    INTERNAL_FAILURE_DATABASE(50001, "{}操作失败"),
    INTERNAL_FAILURE_DUBBOSERVICE(50002, "{}服务失败"),
    INTERNAL_FAILURE_OTHER(50003, "{}"),
    INTERNAL_FAILURE_OPERATION(50004, "{}失败");

    private final int errorCode;

    private final String messageTemplate;
}
