package com.victorlamp.matrixiot.service.system.entity.tenant;

import com.victorlamp.matrixiot.service.framework.mybatis.core.dataobject.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 拓展多租户的 BaseDO 基类
 *
 * @author bayes@victorlamp.com
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class TenantBaseDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = -8816599752239502837L;
    /**
     * 多租户编号
     */
    private Long tenantId;

}
