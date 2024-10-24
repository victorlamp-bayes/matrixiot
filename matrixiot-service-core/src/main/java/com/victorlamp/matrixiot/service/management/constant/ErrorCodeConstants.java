package com.victorlamp.matrixiot.service.management.constant;

import com.victorlamp.matrixiot.common.exception.ErrorCode;

/**
 * System 错误码枚举类
 * <p>
 * iot设备管理 系统，使用 1-002-000-000 段
 */
public interface ErrorCodeConstants {

    // ========== 产品模块 2_001_001_000 ========== //
    ErrorCode PRODUCT_NAME_DUPLICATE = new ErrorCode(2_001_001_000, "已经存在该名字的产品");
    ErrorCode PRODUCT_NOT_EXISTS = new ErrorCode(2_001_001_001, "产品不存在");
    ErrorCode PRODUCT_NODE_TYPE_IS_NULL = new ErrorCode(2_001_001_002, "节点类型不能为空");
    ErrorCode PRODUCT_NODE_TYPE_INVALID = new ErrorCode(2_001_001_003, "无效的节点类型");
    ErrorCode PRODUCT_DIRECT_DEVICE_CANNOT_CONFIG_PROTOCOL_TYPE = new ErrorCode(2_001_001_004, "直连设备/网关设备不能配置子设备连接协议");
    ErrorCode PRODUCT_SUB_DEVICE_CANNOT_CONFIG_NET_TYPE = new ErrorCode(2_001_001_005, "子设备不能配置连网类型");
    ErrorCode PRODUCT_NET_TYPE_IS_NULL = new ErrorCode(2_001_001_006, "直连设备/网关设备的连网类型不能为空");
    ErrorCode PRODUCT_NET_TYPE_INVALID = new ErrorCode(2_001_001_007, "无效的连网类型");
    ErrorCode PRODUCT_PROTOCOL_TYPE_IS_NULL = new ErrorCode(2_001_001_008, "子设备连接网关协议不能为空");
    ErrorCode PRODUCT_PROTOCOL_TYPE_INVALID = new ErrorCode(2_001_001_009, "无效的子设备连接网关协议");
    ErrorCode PRODUCT_CATEGORY_IS_NULL = new ErrorCode(2_001_001_010, "产品分类不能为空");
    ErrorCode PRODUCT_CATEGORY_INVALID = new ErrorCode(2_001_001_011, "无效的产品品类");
    ErrorCode PRODUCT_NET_CONFIG_TYPE_INVALID = new ErrorCode(2_001_001_012, "无效的连网配置");
    ErrorCode PRODUCT_NET_CONFIG_IS_EMPTY = new ErrorCode(2_001_001_013, "连网配置不能为空");
    ErrorCode PRODUCT_CANNOT_UNPUBLISHED_BY_BIND_THING = new ErrorCode(2_001_001_014, "该产品下已有绑定设备，无法取消发布");
    ErrorCode PRODUCT_CANNOT_DELETE_BY_PUBLISHED = new ErrorCode(2_001_001_015, "已发布产品无法删除，请取消发布后再删除");
    ErrorCode PRODUCT_CANNOT_DELETE_BY_BIND_THING = new ErrorCode(2_001_001_016, "该产品下已有绑定设备，无法删除");
    ErrorCode PRODUCT_IMPORT_FILE_SIZE_LT_10M = new ErrorCode(2_001_001_001, "产品导入文件大小不能超过10M");

    // ========== 设备模块 2_001_002_000 ========== //
    ErrorCode THING_NAME_DUPLICATE = new ErrorCode(2_001_002_000, "已经存在该名字的设备");
    ErrorCode THING_NOT_EXISTS = new ErrorCode(2_001_002_001, "设备不存在");
    ErrorCode THING_PRODUCT_NOT_EXISTS = new ErrorCode(2_001_001_001, "产品不存在，无法绑定设备");
    ErrorCode THING_PRODUCT_NOT_PUBLISHED = new ErrorCode(2_001_001_001, "产品未发布，无法绑定设备");
    ErrorCode THING_CANNOT_DELETE_ENABLED = new ErrorCode(2_001_001_001, "设备已启用，无法删除");
    ErrorCode THING_CANNOT_UPDATE_DISABLED_THING = new ErrorCode(2_001_001_001, "设备已停用，无法修改");
    ErrorCode THING_CANNOT_UPDATE_THING_ONLINE_TO_NULL = new ErrorCode(2_001_001_001, "不能将设备在线状态设置为空");
    ErrorCode THING_CANNOT_DELETE_BIND_SUB_THING = new ErrorCode(2_001_001_001, "该网关已绑定子设备，无法删除");
    ErrorCode THING_CANNOT_ADD_SUB_THING_TO_NON_GATEWAY = new ErrorCode(2_001_001_001, "该设备不是网关，不能添加子设备");
    ErrorCode THING_CANNOT_ADD_NON_SUB_THING_TO_GATEWAY = new ErrorCode(2_001_001_001, "该设备不是子设备，不能添加为网关子设备");
    ErrorCode THING_CANNOT_REMOVE_NULL_SUB_THING = new ErrorCode(2_001_001_001, "子设备不存在，无法从网关移除");
    ErrorCode THING_CANNOT_REMOVE_FROM_NULL_GATEWAY = new ErrorCode(2_001_001_001, "网关设备不存在，无法移除子设备");
    ErrorCode THING_NOT_GATEWAY_AND_SUB_THING = new ErrorCode(2_001_001_001, "指定设备不是网关与子设备，无法移除子设备");
    ErrorCode THING_IMPORT_FILE_SIZE_LT_10M = new ErrorCode(2_001_001_001, "设备导入文件大小不能超过10M");
    ErrorCode THING_INVALID_EXTERNAL_TYPE = new ErrorCode(2_001_001_001, "无效的设备外部类型");
    ErrorCode THING_INVOKE_SERVICE_EMPTY_DATA = new ErrorCode(2_001_001_001, "调用服务的数据为空");
    ErrorCode THING_INVOKE_SERVICE_EMPTY_INPUT_PARAM = new ErrorCode(2_001_001_001, "调用服务输入参数为空");

    // ========== 物模型模块 2_001_003_000 ========== //
    ErrorCode THING_MODEL_PRODUCT_NOT_EXISTS = new ErrorCode(2_001_003_000, "产品不存在，无法创建/更新物模型");
    ErrorCode THING_MODEL_DUPLICATE = new ErrorCode(2_001_003_000, "已经存在该物模型");
    ErrorCode THING_MODEL_NOT_EXISTS = new ErrorCode(2_001_003_000, "物模型不存在");
    ErrorCode THING_MODEL_CANNOT_DELETE_BY_EXISTS_PRODUCT = new ErrorCode(2_001_003_000, "无法删除已有产品的物模型");
    ErrorCode THING_MODEL_MISMATCH_PRODUCT_ID_AND_THING_MODEL_ID = new ErrorCode(2_001_003_000, "请求参数中产品ID与物模型ID不一致");
    ErrorCode THING_MODEL_UNKNOWN_SCRIPT_TYPE = new ErrorCode(2_001_003_000, "未知的物模型脚本类型");
    ErrorCode THING_MODEL_SCRIPT_NOT_CONTAINS_REQUIRED_FUNC = new ErrorCode(2_001_003_000, "物模型脚本未包含必要函数");
    ErrorCode THING_MODEL_ILLEGAL_SCRIPT_CONTENT = new ErrorCode(2_001_003_000, "物模型脚本语法错误");
    ErrorCode THING_MODEL_CANNOT_MODIFY_PUBLISHED_PRODUCT = new ErrorCode(2_001_003_000, "产品已发布，无法更新物模型");
    ErrorCode THING_MODEL_PROPERTY_IDENTIFIER_DUPLICATE = new ErrorCode(2_001_001_000, "已经存在该标识符的属性");
    ErrorCode THING_MODEL_PROPERTY_NAME_DUPLICATE = new ErrorCode(2_001_001_000, "已经存在该名称的属性");
    ErrorCode THING_MODEL_PROPERTY_NOT_EXISTS = new ErrorCode(2_001_001_000, "物模型属性不存在");
    ErrorCode THING_MODEL_EVENT_IDENTIFIER_DUPLICATE = new ErrorCode(2_001_001_000, "已经存在该标识符的事件");
    ErrorCode THING_MODEL_EVENT_NAME_DUPLICATE = new ErrorCode(2_001_001_000, "已经存在该名称的事件");
    ErrorCode THING_MODEL_EVENT_NOT_EXISTS = new ErrorCode(2_001_001_000, "物模型事件不存在");
    ErrorCode THING_MODEL_SERVICE_IDENTIFIER_DUPLICATE = new ErrorCode(2_001_001_000, "已经存在该标识符的服务");
    ErrorCode THING_MODEL_SERVICE_NAME_DUPLICATE = new ErrorCode(2_001_001_000, "已经存在该名称的服务");
    ErrorCode THING_MODEL_SERVICE_NOT_EXISTS = new ErrorCode(2_001_001_000, "物模型服务不存在");
    ErrorCode THING_MODEL_SERVICE_PARAM_IDENTIFIER_DUPLICATE = new ErrorCode(2_001_001_000, "已经存在该标识符的服务参数");
    ErrorCode THING_MODEL_SERVICE_PARAM_NAME_DUPLICATE = new ErrorCode(2_001_001_000, "已经存在该名称的服务参数");
    ErrorCode THING_MODEL_SERVICE_PARAM_NOT_EXISTS = new ErrorCode(2_001_001_000, "物模型服务参数不存在");

    // ========== 设备数据模块 2_001_004_000 ========== //
    ErrorCode THING_DATA_THING_NOT_EXISTS = new ErrorCode(2_001_004_000, "设备不存在");
    ErrorCode THING_DATA_THING_DISABLED = new ErrorCode(2_001_004_000, "设备已停用");
    ErrorCode THING_DATA_PRODUCT_NOT_EXISTS = new ErrorCode(2_001_004_000, "产品不存在");
    ErrorCode THING_DATA_PRODUCT_UNAVAILABLE = new ErrorCode(2_001_004_000, "产品不可用");
    ErrorCode THING_DATA_CONSUMER_POST_DATA_FAILED = new ErrorCode(2_001_004_000, "处理上报数据失败");
    ErrorCode THING_DATA_SERVICE_DATA_NOT_EXISTS = new ErrorCode(2_001_004_000, "服务调用记录不存在");
    ErrorCode THING_DATA_CREATE_SERVICE_DATA_FAILED = new ErrorCode(2_001_004_000, "服务调用数据创建失败");

}
