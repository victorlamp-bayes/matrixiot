package com.victorlamp.matrixiot.service.agent.constant;

import com.victorlamp.matrixiot.common.exception.ErrorCode;

/**
 * System 错误码枚举类
 * <p>
 * iot设备管理 系统，使用 1-002-000-000 段
 */
public interface ErrorCodeConstants {

    // ========== Agent模块 2_002_001_000 ========== //
    ErrorCode THING_AGENT_INVALID_DATA_FORMAT = new ErrorCode(2_002_001_000, "无效的数据格式");

    ErrorCode THING_AGENT_THING_MODEL_SCRIPT_NOT_EXISTS = new ErrorCode(2_002_001_000, "物模型脚本不存在");
    ErrorCode THING_AGENT_GET_PRESET_SCRIPT_FILE_FAILED = new ErrorCode(2_002_001_000, "读取预置物模型脚本文件失败");
    ErrorCode THING_AGENT_GET_PRESET_SCRIPT_IS_BLANK = new ErrorCode(2_002_001_000, "预置物模型脚本内容为空");
    ErrorCode THING_AGENT_ILLEGAL_THING_MODEL_SCRIPT = new ErrorCode(2_002_001_000, "物模型脚本语法错误");
    ErrorCode THING_AGENT_RAW_DATA_TO_PROTOCOL_NOT_EXISTS = new ErrorCode(2_002_001_000, "物模型脚本缺少rawDataToProtocol函数");
    ErrorCode THING_AGENT_RAW_DATA_TO_PROTOCOL_FAILED = new ErrorCode(2_002_001_000, "物模型脚本rawDataToProtocol函数执行失败");
    ErrorCode THING_AGENT_RAW_DATA_TO_PROTOCOL_RETURN_EMPTY = new ErrorCode(2_002_001_000, "物模型脚本rawDataToProtocol函数返回为空");
    ErrorCode THING_AGENT_RAW_DATA_TO_PROTOCOL_RETURN_ERROR = new ErrorCode(2_002_001_000, "物模型脚本rawDataToProtocol函数返回失败");
    ErrorCode THING_AGENT_PROTOCOL_TO_RAW_DATA_NOT_EXISTS = new ErrorCode(2_002_001_000, "物模型脚本缺少protocolToRawData函数");
    ErrorCode THING_AGENT_PROTOCOL_TO_RAW_DATA_FAILED = new ErrorCode(2_002_001_000, "物模型脚本protocolToRawData函数执行失败");

    ErrorCode THING_AGENT_DATA_ROUTE_SCRIPT_NOT_EXISTS = new ErrorCode(2_002_001_000, "数据路由脚本不存在");
    ErrorCode THING_AGENT_ILLEGAL_DATA_ROUTE_SCRIPT = new ErrorCode(2_002_001_000, "数据路由脚本语法错误");
    ErrorCode THING_AGENT_TRANSFORM_NOT_EXISTS = new ErrorCode(2_002_001_000, "数据路由脚本缺少transform函数");
    ErrorCode THING_AGENT_TRANSFORM_FAILED = new ErrorCode(2_002_001_000, "数据路由脚本transform函数执行失败");

    ErrorCode THING_AGENT_CONNECT_TO_EXTERNAL_SERVER_BY_CONFIG_ERROR = new ErrorCode(2_002_001_000, "产品配置错误，无法连接外部服务器");
    ErrorCode THING_AGENT_CONNECT_TO_EXTERNAL_SERVER_FAILED = new ErrorCode(2_002_001_000, "连接外部服务器失败");
    ErrorCode THING_AGENT_PULL_THING_FROM_EXTERNAL_SERVER_FAILED = new ErrorCode(2_002_001_000, "从外部服务器获取设备列表失败");
    ErrorCode THING_AGENT_CREATE_THING_FOR_EXTERNAL_DEVICE_FAILED = new ErrorCode(2_002_001_000, "为外部设备创建Thing失败");
    ErrorCode THING_AGENT_PULL_THING_DATA_FROM_EXTERNAL_SERVER_FAILED = new ErrorCode(2_002_001_000, "从外部服务器获取设备数据失败");
    ErrorCode THING_AGENT_PRODUCT_IS_NULL = new ErrorCode(2_002_001_000, "产品不能为空");
    ErrorCode THING_AGENT_PRODUCT_NOT_PUBLISHED = new ErrorCode(2_002_001_000, "产品未发布");
    ErrorCode THING_AGENT_PRODUCT_EXTERNAL_CONFIG_IS_NULL = new ErrorCode(2_002_001_000, "产品外部配置不能为空");
    ErrorCode THING_AGENT_PRODUCT_EXTERNAL_CONFIG_TYPE_MISMATCH = new ErrorCode(2_002_001_000, "产品外部配置类型不匹配");
    ErrorCode THING_AGENT_THING_IS_NULL = new ErrorCode(2_002_001_000, "设备不能为空");
    ErrorCode THING_AGENT_THING_IS_DISABLED = new ErrorCode(2_002_001_000, "设备未启用");
    ErrorCode THING_AGENT_THING_EXTERNAL_CONFIG_IS_NULL = new ErrorCode(2_002_001_000, "设备外部配置不能为空");
    ErrorCode THING_AGENT_THING_EXTERNAL_CONFIG_TYPE_MISMATCH = new ErrorCode(2_002_001_000, "设备外部配置类型不匹配");

    ErrorCode THING_AGENT_MQTT_SUB_TOPIC_FAILED = new ErrorCode(2_002_001_000, "订阅MQTT主题失败");

    ErrorCode THING_AGENT_INVALID_EXTERNAL_CONFIG_TYPE = new ErrorCode(2_002_001_000, "无效的外部类型");
    ErrorCode AGENT_UNKNOWN_DATA_SOURCE = new ErrorCode(2_003_000_000, "未知数据来源");
    ErrorCode AGENT_DEVICE_NOT_EXISTS = new ErrorCode(2_003_000_000, "设备不存在");
    ErrorCode AGENT_DEVICE_POST_EMPTY_DATA = new ErrorCode(2_001_001_001, "设备上报数据为空");
    ErrorCode AGENT_DEVICE_POST_ILLEGAL_DATA = new ErrorCode(2_001_001_001, "设备上报数据格式错误");
    ErrorCode AGENT_DEVICE_POST_UNKNOWN_DATA = new ErrorCode(2_001_001_001, "设备上报数据格式未知");
    ErrorCode AGENT_AEP_DEVICE_POST_INVALID_DATA = new ErrorCode(2_001_001_001, "AEP设备上报数据格式无效");
    ErrorCode AGENT_OC_DEVICE_POST_INVALID_DATA = new ErrorCode(2_001_001_001, "OC设备上报数据格式无效");
    ErrorCode AGENT_ONENET_DEVICE_POST_INVALID_DATA = new ErrorCode(2_001_001_001, "ONENET设备上报数据格式无效");
    ErrorCode AGENT_LORA_DEVICE_POST_INVALID_DATA = new ErrorCode(2_001_001_001, "LORA设备上报数据格式无效");
    ErrorCode AGENT_REGISTER_DEVICE_TO_AEP_FAILED_BY_REQ = new ErrorCode(2_001_001_001, "注册设备到AEP平台失败:请求失败");
    ErrorCode AGENT_SEND_COMMAND_TO_AEP_FAILED_BY_REQ = new ErrorCode(2_001_001_001, "发送指令到AEP平台失败:请求失败");
    ErrorCode AGENT_AUTH_OC_FAILED_BY_AT_REQ = new ErrorCode(2_001_001_001, "OC平台请求AT失败");
    ErrorCode AGENT_AUTH_OC_FAILED_BY_AT_EMPTY = new ErrorCode(2_001_001_001, "OC平台获取AT为空");
    ErrorCode AGENT_REGISTER_DEVICE_TO_OC_FAILED_BY_AT = new ErrorCode(2_001_001_001, "注册设备到OC平台失败:获取AT失败");
    ErrorCode AGENT_REGISTER_DEVICE_TO_OC_FAILED_BY_REQ = new ErrorCode(2_001_001_001, "注册设备到OC平台失败:请求失败");
    ErrorCode AGENT_REGISTER_DEVICE_TO_OC_FAILED_BY_RESP = new ErrorCode(2_001_001_001, "注册设备到OC平台失败:失败响应");
    ErrorCode AGENT_SEND_COMMAND_TO_OC_FAILED_BY_AT = new ErrorCode(2_001_001_001, "发送指令到OC平台失败:获取AT失败");
    ErrorCode AGENT_SEND_COMMAND_TO_OC_FAILED_BY_REQ = new ErrorCode(2_001_001_001, "发送指令到OC平台失败:请求失败");

    ErrorCode AGENT_ILLEGAL_PROTOCOL_DATA = new ErrorCode(2_001_001_001, "协议数据格式错误");
    ErrorCode AGENT_UNDEFINED_THING_MODEL_PROPERTY = new ErrorCode(2_001_001_001, "未定义物模型属性");
    ErrorCode AGENT_UNDEFINED_THING_MODEL_EVENT = new ErrorCode(2_001_001_001, "未定义物模型事件");
    ErrorCode AGENT_ILLEGAL_TIME_FORMAT = new ErrorCode(2_001_001_001, "上报时间格式错误");
}
