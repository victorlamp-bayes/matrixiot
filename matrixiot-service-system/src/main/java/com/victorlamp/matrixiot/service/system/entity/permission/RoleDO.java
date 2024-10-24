package com.victorlamp.matrixiot.service.system.entity.permission;

import com.baomidou.mybatisplus.annotation.*;
import com.victorlamp.matrixiot.common.enums.CommonStatusEnum;
import com.victorlamp.matrixiot.service.framework.mybatis.core.type.JsonLongSetTypeHandler;
import com.victorlamp.matrixiot.service.system.entity.tenant.TenantBaseDO;
import com.victorlamp.matrixiot.service.system.enums.permission.DataScopeEnum;
import com.victorlamp.matrixiot.service.system.enums.permission.RoleTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * 角色 DO
 *
 * @author ruoyi
 */
@TableName(value = "system_role", autoResultMap = true)
@KeySequence("system_role_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class RoleDO extends TenantBaseDO {

    /**
     * 角色ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 角色名称
     */
    private String name;
    /**
     * 角色标识
     * <p>
     * 枚举
     */
    private String code;
    /**
     * 角色排序
     */
    private Integer sort;
    /**
     * 角色状态
     * <p>
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;
    /**
     * 角色类型
     * <p>
     * 枚举 {@link RoleTypeEnum}
     */
    private Integer type;
    /**
     * 备注
     */
    private String remark;

    /**
     * 数据范围
     * <p>
     * 枚举 {@link DataScopeEnum}
     */
    private Integer dataScope;
    /**
     * 数据范围(指定部门数组)
     * <p>
     * 适用于 {@link #dataScope} 的值为 {@link DataScopeEnum#DEPT_CUSTOM} 时
     */
    @TableField(typeHandler = JsonLongSetTypeHandler.class)
    private Set<Long> dataScopeDeptIds;

}
