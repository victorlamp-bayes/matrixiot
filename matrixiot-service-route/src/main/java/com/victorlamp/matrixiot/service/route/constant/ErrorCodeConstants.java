package com.victorlamp.matrixiot.service.route.constant;

import com.victorlamp.matrixiot.common.exception.ErrorCode;

public interface ErrorCodeConstants {
    // ========== 数据流转模块 2_001_001_000 ========== //
    ErrorCode DATA_ROUTE_NAME_DUPLICATE = new ErrorCode(2_001_001_000, "已经存在该名字的数据路由");
    ErrorCode DATA_ROUTE_NOT_EXISTS = new ErrorCode(2_001_001_001, "数据路由不存在");
    ErrorCode DATA_ROUTE_STATUS_INVALID = new ErrorCode(2_001_001_002, "无效的状态类型");
    ErrorCode DATA_ROUTE_CANNOT_MODIFY_BY_STATUS = new ErrorCode(2_001_001_003, "数据路由正在使用，无法更新，请停止后再操作");
    ErrorCode DATA_ROUTE_DELETE_BY_STATUS = new ErrorCode(2_001_001_004, "数据路由正在使用，无法删除，请停止后再操作");
    ErrorCode DATA_DESTINATION_URL_IS_NULL = new ErrorCode(2_001_001_005, "数据目的请求地址不能为空");
    ErrorCode DATA_DESTINATION_METHOD_IS_NULL = new ErrorCode(2_001_001_006, "数据目的请求方式不能为空");
    ErrorCode DATA_DESTINATION_VERIFY_COD_IS_NULL = new ErrorCode(2_001_001_007, "数据目的验证码不能为空");
    ErrorCode DATA_DESTINATION_RABBITMQ_HOST_IS_NULL = new ErrorCode(2_001_001_008, "数据目的RabbitMQ主机号不能为空");
    ErrorCode DATA_DESTINATION_RABBITMQ_PORT_IS_NULL = new ErrorCode(2_001_001_009, "数据目的RabbitMQ端口号不能为空");
    ErrorCode DATA_DESTINATION_RABBITMQ_USERNAME_IS_NULL = new ErrorCode(2_001_001_010, "数据目的RabbitMQ用户名不能为空");
    ErrorCode DATA_DESTINATION_RABBITMQ_PASSWORD_IS_NULL = new ErrorCode(2_001_001_011, "数据目的RabbitMQ用户密码不能为空");
    ErrorCode DATA_DESTINATION_RABBITMQ_TOPIC_IS_NULL = new ErrorCode(2_001_001_012, "数据目的RabbitMQ消息主题不能为空");

    ErrorCode DATA_ROUTE_UNKNOWN_SCRIPT_TYPE = new ErrorCode(2_001_001_013, "未知的数据路由脚本类型");
    ErrorCode DATA_ROUTE_SCRIPT_NOT_EXISTS = new ErrorCode(2_002_001_014, "数据路由脚本不存在");
    ErrorCode ILLEGAL_DATA_ROUTE_SCRIPT = new ErrorCode(2_002_001_015, "数据路由脚本语法错误");
    ErrorCode DATA_ROUTE_TRANSFORM_NOT_EXISTS = new ErrorCode(2_002_001_016, "数据路由脚本缺少transform函数");
    ErrorCode DATA_ROUTE_TRANSFORM_FAILED = new ErrorCode(2_002_001_017, "数据路由脚本transform函数执行失败");

    ErrorCode DATA_FLOW_TRANSFORMATION_FAILED = new ErrorCode(2_002_001_018, "数据流转失败");
    ErrorCode DATA_ROUTE_CREATE_FAILED = new ErrorCode(2_002_001_019, "数据路由监听器创建失败");
    ErrorCode DATA_ROUTE_STATUS_IS_NOT_RUNNING = new ErrorCode(2_002_001_020, "数据路由状态未启用，停止流转");


    // ========== 产品/设备 2_002_001_000 ========== //
    ErrorCode DATA_ROUTE_PRODUCT_NOT_EXISTS = new ErrorCode(2_002_001_000, "产品不存在");
    ErrorCode DATA_ROUTE_THING_NOT_EXISTS = new ErrorCode(2_002_001_001, "设备不存在");
}
