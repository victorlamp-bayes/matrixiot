package com.victorlamp.matrixiot.service.alarm.constant;

import com.victorlamp.matrixiot.common.exception.ErrorCode;

public interface ErrorCodeConstants {
    // ========== 告警模块 2_001_001_000 ========== //
    ErrorCode ALARM_NOT_EXISTS = new ErrorCode(2_001_001_000, "告警不存在");
    ErrorCode ILLEGAL_UPDATE_VALUE = new ErrorCode(2_001_001_001, "更新值不合法");
    ErrorCode ALARM_SEND_CHANNEL_FORMAT_INVALID = new ErrorCode(2_001_001_002, "告警发送渠道格式无效");
    ErrorCode ALARM_CONFIG_NOT_EXISTS = new ErrorCode(2_001_001_003, "告警通知配置不存在");
    ErrorCode ALARM_CONFIG_ALREADY_EXISTS = new ErrorCode(2_001_001_004, "告警通知配置已存在");
    ErrorCode ALARM_PRODUCT_NOT_EXISTS = new ErrorCode(2_001_001_005, "产品不存在");

}
