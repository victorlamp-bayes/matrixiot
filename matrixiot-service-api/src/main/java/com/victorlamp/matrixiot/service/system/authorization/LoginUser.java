package com.victorlamp.matrixiot.service.system.authorization;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 登录用户信息
 * <p>
 * copy from yudao-spring-boot-starter-security 的 LoginUser 类
 *
 * @author 芋道源码
 */
@Data
@Accessors(chain = true)
public class LoginUser implements Serializable {

    @Serial private static final long serialVersionUID = 4596778118585130270L;
    /**
     * 用户编号
     */
    private Long userId;
    /**
     * 用户类型
     */
    private Integer userType;
    /**
     * 租户编号
     */
    private Long tenantId;
    /**
     * 授权范围
     */
    private List<String> scopes;

}
