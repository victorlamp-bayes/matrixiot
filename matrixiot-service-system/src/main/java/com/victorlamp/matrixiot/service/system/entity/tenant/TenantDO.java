package com.victorlamp.matrixiot.service.system.entity.tenant;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.victorlamp.matrixiot.common.enums.CommonStatusEnum;
import com.victorlamp.matrixiot.service.framework.mybatis.core.dataobject.BaseDO;
import com.victorlamp.matrixiot.service.system.entity.user.AdminUserDO;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 租户 DO
 *
 * @author 芋道源码
 */
@TableName(value = "system_tenant", autoResultMap = true)
//@KeySequence("system_tenant_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class TenantDO extends BaseDO {

    /**
     * 套餐编号 - 系统
     */
    public static final Long PACKAGE_ID_SYSTEM = 0L;

    /**
     * 租户编号，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 租户名，唯一
     */
    private String name;
    /**
     * 联系人的用户编号
     * <p>
     * 关联 {@link AdminUserDO#getId()}
     */
    private Long contactUserId;
    /**
     * 联系人
     */
    private String contactName;
    /**
     * 联系手机
     */
    private String contactMobile;
    /**
     * 租户状态
     * <p>
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;
    /**
     * 绑定域名
     */
    private String website;
    /**
     * 租户套餐编号
     * <p>
     * 关联 {@link TenantPackageDO#getId()}
     * 特殊逻辑：系统内置租户，不使用套餐，暂时使用 {@link #PACKAGE_ID_SYSTEM} 标识
     */
    private Long packageId;
    /**
     * 过期时间
     */
    private LocalDateTime expireTime;
    /**
     * 账号数量
     */
    private Integer accountCount;

}
