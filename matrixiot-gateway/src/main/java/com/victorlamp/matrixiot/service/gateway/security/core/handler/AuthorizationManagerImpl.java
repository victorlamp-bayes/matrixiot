package com.victorlamp.matrixiot.service.gateway.security.core.handler;

import cn.hutool.core.collection.CollUtil;
import com.victorlamp.matrixiot.service.common.util.RpcFrameworkUtils;
import com.victorlamp.matrixiot.service.gateway.security.config.SecurityProperties;
import com.victorlamp.matrixiot.service.gateway.util.SecurityFrameworkUtils;
import com.victorlamp.matrixiot.service.system.authorization.AuthorizationService;
import com.victorlamp.matrixiot.service.system.authorization.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
public class AuthorizationManagerImpl implements ReactiveAuthorizationManager<AuthorizationContext> {

    private static final PathMatcher MATCHER = new AntPathMatcher();
    /**
     * 安全配置
     */
    @Resource
    private SecurityProperties securityProperties;
    @DubboReference
    @Resource
    private AuthorizationService authorizationService;

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext authorizationContext) {
        log.info("授权(Authorization)管理器");

        // 当前登录用户信息写入RpcContext
        ServerHttpRequest request = authorizationContext.getExchange().getRequest();
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser(authorizationContext.getExchange());
        RpcFrameworkUtils.setLoginUser(loginUser);

        return authentication
                .filter(Authentication::isAuthenticated)
                .flatMap(auth -> checkPermissions(auth, request))
                .map(AuthorizationDecision::new)
                .defaultIfEmpty(new AuthorizationDecision(false));
    }

    private Mono<Boolean> checkPermissions(Authentication authentication, ServerHttpRequest request) {
        log.debug("请求鉴权时，是否已登录:[{}]", authentication.isAuthenticated());
        // 需登录且忽略授权的URL
        List<String> ignorePermissionUrls = securityProperties.getIgnorePermissionUrls();
        String path = request.getURI().getPath();
        log.debug("请求路径:[{}]", path);
        if (!CollUtil.isEmpty(ignorePermissionUrls)
                && ignorePermissionUrls.stream().anyMatch(url -> MATCHER.match(url, path))) {
            return Mono.just(true);
        }

        // TODO: 需要授权的URL
        String permission = getPermission(request);
        log.info("请求鉴权[{}]", permission);
        boolean result = authorizationService.hasAnyPermissions(permission);
        return Mono.just(result);
    }

    private String getPermission(ServerHttpRequest request) {
        String originPath = request.getURI().getPath();
        String path = originPath.replaceFirst("/api/v\\d/", "");
        return path.replaceAll("/", ":");
    }
}