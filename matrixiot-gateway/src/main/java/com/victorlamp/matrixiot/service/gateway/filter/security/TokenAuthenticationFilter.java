package com.victorlamp.matrixiot.service.gateway.filter.security;

import cn.hutool.core.util.StrUtil;
import com.nimbusds.jose.JWSObject;
import com.victorlamp.matrixiot.common.util.json.JsonUtils;
import com.victorlamp.matrixiot.service.gateway.util.SecurityFrameworkUtils;
import com.victorlamp.matrixiot.service.system.authorization.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.function.Function;

/**
 * Token 过滤器，验证 token 的有效性
 * 1. 验证通过时，将 userId、userType、tenantId 通过 Header 转发给服务
 * 2. 验证不通过，还是会转发给服务。因为，接口是否需要登录的校验，还是交给服务自身处理
 *
 * @author 芋道源码
 */
@Slf4j
public class TokenAuthenticationFilter implements WebFilter {

    private static final LoginUser LOGIN_USER_EMPTY = new LoginUser();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.info("认证(Authentication)过滤器");

        // 移除 login-user 的请求头，避免伪造模拟
        SecurityFrameworkUtils.removeLoginUserHeader(exchange);

        // 情况一，如果没有 Token 令牌，则直接继续 filter
        String token = SecurityFrameworkUtils.obtainAuthorization(exchange);
        if (StrUtil.isEmpty(token)) {
            return chain.filter(exchange);
        }

        // 情况二，如果有 Token 令牌，则解析对应 userId、userType、tenantId、scopes 等字段，并通过 Header 转发给服务
        // 重要说明：defaultIfEmpty 作用，保证 Mono.empty() 情况，可以继续执行 `flatMap 的 chain.filter(exchange)` 逻辑，避免返回给前端空的 Response！！
        return getLoginUser(token).defaultIfEmpty(LOGIN_USER_EMPTY).flatMap(user -> {
            // 1. 无用户，直接 filter 继续请求
            if (user == LOGIN_USER_EMPTY) {
                return chain.filter(exchange);
            }

            // 2.1 有用户，则设置登录用户
            SecurityFrameworkUtils.setLoginUser(exchange, user);
            // 2.2 将 user 设置到 login-user 的请求头，使用 json 存储值
            ServerWebExchange newExchange = exchange.mutate()
                    .request(builder -> SecurityFrameworkUtils.setLoginUserHeader(builder, user)).build();
            return chain.filter(newExchange);
        });
    }

    private Mono<LoginUser> getLoginUser(String token) {
        // 解析JWT，构造LoginUser
        return checkAccessToken(token).flatMap((Function<String, Mono<LoginUser>>) body -> {
            LoginUser loginUser = buildUser(body);
            if (loginUser != null) {
                return Mono.just(loginUser);
            }
            return Mono.empty();
        });
    }

    private Mono<String> checkAccessToken(String token) {
        try {
            JWSObject jwsObject = JWSObject.parse(token);
            LoginUser loginUser = buildUser(jwsObject.getPayload().toString());
            log.debug("login_user:{}", loginUser);
            return Mono.just(jwsObject.getPayload().toString());
        } catch (Exception e) {
            log.warn("解析AccessToken失败:{}", token, e);
        }

        return Mono.empty();
    }

    private LoginUser buildUser(String body) {
        try {
            return JsonUtils.parseObject(body, LoginUser.class);
        } catch (Exception e) {
            log.warn("构造LoginUser错误：{}", body, e);
        }

        return null;
    }

}
