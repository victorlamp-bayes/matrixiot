package com.victorlamp.matrixiot.service.system.service.permission;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import com.victorlamp.matrixiot.service.common.util.RpcFrameworkUtils;
import com.victorlamp.matrixiot.service.framework.tenant.core.context.TenantContextHolder;
import com.victorlamp.matrixiot.service.system.api.permission.PermissionService;
import com.victorlamp.matrixiot.service.system.authorization.AuthorizationService;
import com.victorlamp.matrixiot.service.system.authorization.LoginUser;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.Arrays;

/**
 * 默认的 {@link AuthorizationService} 实现类
 *
 * @author 芋道源码
 */
@AllArgsConstructor
@DubboService
@Slf4j
public class AuthorizationServiceImpl implements AuthorizationService {

    private final PermissionService permissionService;

    @Override
    @SneakyThrows
    public boolean hasAnyPermissions(String... permissions) {
        LoginUser loginUser = RpcFrameworkUtils.getLoginUser();
        if (loginUser == null) {
            return false;
        } else {
            TenantContextHolder.setTenantId(loginUser.getTenantId());
        }
        boolean hasAnyPermissions = permissionService.hasAnyPermissions(loginUser.getUserId(), permissions);
        log.info("检查Permissions:[{}]:[{}]", ArrayUtil.join(permissions, ","), hasAnyPermissions);
        return hasAnyPermissions;
    }

    @Override
    @SneakyThrows
    public boolean hasAnyRoles(String... roles) {
        LoginUser loginUser = RpcFrameworkUtils.getLoginUser();
        if (loginUser == null) {
            return false;
        } else {
            TenantContextHolder.setTenantId(loginUser.getTenantId());
        }
        boolean hasAnyRoles = permissionService.hasAnyRoles(loginUser.getUserId(), roles);
        log.info("检查Roles:[{}]:[{}]", ArrayUtil.join(roles, ","), hasAnyRoles);
        return hasAnyRoles;
    }

    @Override
    public boolean hasAnyScopes(String... scope) {
        LoginUser loginUser = RpcFrameworkUtils.getLoginUser();
        if (loginUser == null) {
            return false;
        } else {
            TenantContextHolder.setTenantId(loginUser.getTenantId());
        }
        boolean hasAnyScopes = CollUtil.containsAny(loginUser.getScopes(), Arrays.asList(scope));
        log.info("检查Scopes:[{}]:[{}]", ArrayUtil.join(scope, ","), hasAnyScopes);
        return hasAnyScopes;
    }
}
