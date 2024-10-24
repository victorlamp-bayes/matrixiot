package com.victorlamp.matrixiot.service.management.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommandStatus {
    IN_QUEUE(1, "等待中"),
    MQ_SEND_FAILED(2, "发送失败"),
    CACHED(3, "已缓存"),
    COMMAND_ISSUE_FAILED(4, "下发失败"),
    SENT(5, "已发送"),
    EXPIRED(6, "已过期"),
    SEND_SUCCESS(7, "发送成功"),
    TIMEOUT(8, "超时"),
    EXECUTION_SUCCESS(9, "执行成功"),
    EXECUTION_FAILED(10, "执行失败"),
    DELETED(11, "已删除");
    private final int id;
    private final String name;
}
