package com.victorlamp.matrixiot.service.metric.constant;

import com.victorlamp.matrixiot.common.exception.ErrorCode;

/**
 * System 错误码枚举类
 * <p>
 * iot设备管理 系统，使用 1-002-000-000 段
 */
public interface ErrorCodeConstants {

    // ========== 度量模块 2_004_001_000 ========== //
    ErrorCode METRIC_NAME_DUPLICATE = new ErrorCode(2_004_001_000, "已经存在该名字的度量指标");

    ErrorCode METRIC_IDENTIFIER_DUPLICATE = new ErrorCode(2_004_001_000, "已经存在该标识符的度量指标");
    ErrorCode METRIC_NOT_EXISTS = new ErrorCode(2_004_001_000, "度量指标不存在");
    ErrorCode METRIC_PRODUCT_NOT_EXISTS = new ErrorCode(2_004_001_000, "度量指标指定产品不存在");

    ErrorCode METRIC_PROPERTY_IDENTIFIER_DUPLICATE = new ErrorCode(2_004_001_000, "度量指标物模型属性不存在");
    ErrorCode METRIC_THING_NOT_EXISTS = new ErrorCode(2_004_001_000, "度量指标指定设备不存在");
    ErrorCode METRIC_UNKNOWN_AGGREGATION_TYPE = new ErrorCode(2_004_001_000, "未知的聚合类型");
    ErrorCode METRIC_UNKNOWN_AGGREGATION_FREQ = new ErrorCode(2_004_001_000, "未知的聚合频率");
}
