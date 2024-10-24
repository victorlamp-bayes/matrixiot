package com.victorlamp.matrixiot.common.web;

import lombok.Getter;

/**
 * @Description <p> 全局错误码定义
 * 错误码由6位数字组成，3位模块编码+3位错误描述码
 * 模块编码如下：
 * 100 用户模块
 * 200 设备模块
 * 300 工单模块
 * 400 工作流模块
 * 500 系统模块
 * 900 其他
 */
@Getter
public enum ResultStatus {

    /**
     * 返回状态
     */
    OK(0, "成功"),

    ERROR_500(500, "系统错误"),
    //100 人员模块
    MEMBER_TELPHONE_ALREADY_EXISTS(100001, "手机号码已存在"),
    PERSON_TYPE_ALREADY_EXISTS(1000002, "该场景下的人员类型已存在"),
    PERSON_TYPE_NAME_ALREADY_EXISTS(100003, "该人员类型名称已存在"),

    //200 设备模块
    DEVICE_NO_EXISTS(200001, "该设备编号已存在，请修改"),
    DEVICE_NO_FORMAT_WRONG(200002, "请输入16位设备编号"),
    UNIT_FORMAT_WRONG(200003, "单元号限输入不超过4个字符"),
    LOCATION_FORMAT_WRONG(200004, "注册地址限输入不超过100个字符"),

    //300 验证码模块,机构管理模块
    SMS_CODE_COUNT(300001, "需进行随机码校验"),
    SMS_CODE_LIMIT(300002, "频繁获取验证码，请联系系统管理员"),
    ORG_HAS_USER(300003, "请先删除机构下的用户"),
    ORG_CHIRDTYPE_ERROR(300006, "下级机构存在公司，请先修改下级机构类型"),
    ORG_PARENT_ERROR(300007, "上级机构为部门，请先修改上级机构类型或更换上级机构"),
    ROLE_HAS_USER(300004, "请先解除用户与此角色绑定关系"),
    USERGROUP_HAS_USER(300005, "请先解除用户与此用户组绑定关系"),
    ROLE_HAS_DISABLE(300008, "该用户角色已经被禁用，无法登录"),
    ORG_INFO_ERROR(300009, "公司/部门信息错误"),
    APP_INFO_ERROR(300010, "appkey活appsecret错误"),
    DEVICESERIAL_INFO_ERROR(300011, "设备序列号错误"),

    //400 工作流模块
    PROCESS_DEPLOY_FAILED(400006, "流程部署失败"),
    TASK_IS_NOT_EXISTS(400001, "查找不到当前任务，任务可能已经被处理完成"),
    TASK_NO_AUTHENTICATION(400002, "不具备当前任务操作权限"),
    TASK_OPERATE_FAILED(400003, "任务操作失败"),
    CREATE_ALARM_FAILED(400004, "系统错误，无法创建工单"),
    NO_MATCH_WORKFLOW(400005, "系统配置错误，无对应工单流程"),
    PROCESS_ALREADY_EXISTS(400006, "该情况下的流程已存在"),
    PROCESS_KEY_ALREADY_EXISTS(400007, "该流程的processKey已经存在"),
    PROCESS_DEL_FAILED(400008, "删除流程失败"),
    PROCESS_NAME_ALREADY_EXISTS(400009, "该流程的名称已经存在"),

    //500 系统模块
    PHONE_NUMBER_ALREADY_EXISTS(500001, "该手机号已存在，请修改"),
    USERNAME_ALREADY_EXISTS(500002, "该用户名已存在，请修改"),
    UNABLE_ENABLE_USER(500003, "普通用户无法启用、禁用用户"),
    SITE_NAME_ALREADY_EXISTS(500004, "该站点名称已存在，请修改"),
    NAME_ALREADY_EXISTS(500005, "该名称已经存在，请修改"),
    ROLE_NAME_ALREADY_EXISTS(500006, "该角色名已存在，请修改"),
    ORGAN_NAME_ALREADY_EXISTS(500007, "该组织名称已存在，请修改"),
    PROGRAM_NAME_ALREADY_EXISTS(500008, "该权限名称已存在，请修改"),
    CUSTOMER_NAME_ALREADY_EXISTS(500009, "该客户名称已存在，请修改"),
    PROJECT_NAME_ALREADY_EXISTS(500010, "该项目已存在，请修改"),
    CHANNEL_NAME_ALREADY_EXISTS(500011, "该场景名称已存在，请修改"),
    PROJECT_DELETE_ERROR(500012, "项目使用中暂不可删除"),
    COMMUNITY_NAME_ALREADY_EXISTS(500013, "该小区已存在，请修改"),
    PRODUCT_CATEGORY_ALREADY_EXISTS(500014, "该产品类型已存在，请修改"),
    PRODUCT_CATEGORY_DELETE_ERROR(500015, "该产品类型已经有设备安装，无法删除"),
    BUILDING_NAME_ALREADY_EXISTS(500016, "该建筑已存在，请修改"),
    ALARM_NAME_ALREADY_EXISTS(500017, "该告警名称已存在，请修改"),
    PRODUCT_DELETE_ERROR(500018, "该产品已经有设备安装，无法删除"),
    CUSTOMER_DELETE_ERROR(500019, "该客户已经有设备安装，无法删除"),

    //600 运维模块
    CHILD_ORDER_ING(600001, "子单未完成，不能提交"),
    PARENT_ORDER_ING(600002, "父单未完成，不能提交"),

    //900 其他
    ERROR(900000, "失败"),
    APPLICATION_ERROR(900001, "应用异常"),
    SERVICE_ERROR(900002, "业务逻辑验证错误。"),
    ACCESS_DENIED_ERROR(900003, "权限不足，无法访问。"),
    USERNAME_NOT_FOUND_ERROR(900004, "账户未注册。"),
    PHONE_NUMBER_NOT_FOUND_ERROR(900005, "您的手机号尚未注册，请注册后进行登录。"),
    VALIDATE_CODE_EXPIRED_ERROR(900006, "您输入的验证码已过期，请刷新重试。"),
    VALIDATE_CODE_ERROR(900007, "您输入的验证码有误，请重新输入。"),
    UNABLE_SEND_ERROR(900008, "60秒内无法发送。"),

    PHONE_VALIDATE_CODE_EXPIRED_ERROR(900010, "您输入的手机验证码已过期，请重新输入。"),
    PHONE_VALIDATE_CODE_ERROR_ERROR(900011, "您输入的手机验证码有误，请重新输入。"),
    PHONE_VALIDATE_CODE_EMPTY_ERROR(900012, "请输入手机验证码。"),
    ACCESS_DISABLED_ERROR(900013, "您的账号已被禁用，如有问题请联系管理员。"),
    PHONE_NUMBER_NOT_FOUND_WECHAT_ERROR(900014, "您微信绑定的手机号尚未注册，请使用平台注册账号进行登录。"),

    TENANT_NAME_ALREADY_EXISTS(900015, "租户名称已存在"),

    EXSIT_IS_PARENT_ERROR(800001, "存在下级信息不能删除"),
    EXSIT_IS_DICTCODE_ERROR(800002, "存在相同字典编码不能新增"),
    EXSIT_IS_ACCOUNT_ERROR(800003, "登录用户不能删除当前登录用户信息"),
    DELETE_IS_ACCOUNT_ERROR(800003, "租户管理员不能删除"),
    SCENETYPE_NAME_ALREADY_EXISTS(100011, "所选场景类型已经被引用，不能被删除"),
    SYSTEM_ERROR(999999, "系统错误"),
    PARAM_ERROR(700001, "参数缺失"),
    SCENE_TYPE_NAME_ALREADY_EXISTS(900015, "场景类型名称重复"),
    SCENE_NAME_ALREADY_EXISTS(900015, "场景名称重复"),
    SCENE_PARENT_ALREADY_EXISTS(900016, "上级场景不能选择当前编辑场景"),
    DEVICE_PARENT_ALREADY_EXISTS(900017, "未选择任何设备或标签信息"),
    USERNAME_OR_PASSWORD_INCORRECT(900018, "用户名或密码错误"),
    ;

    private final int status;

    private final String message;

    ResultStatus(int status, String message) {
        this.status = status;
        this.message = message;
    }
}