package com.victorlamp.matrixiot.service.gateway.util;

import com.victorlamp.matrixiot.common.util.json.JsonUtils;
import com.victorlamp.matrixiot.service.system.authorization.LoginUser;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

import static com.victorlamp.matrixiot.service.system.authorization.enums.LoginUserConstants.HEADER_LOGIN_USER;

/**
 * 安全服务工具类
 *
 * @author 芋道源码
 */
public class SecurityFrameworkUtils {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private static final String AUTHORIZATION_BEARER = "Bearer";

    private static final String LOGIN_USER_ATTR = HEADER_LOGIN_USER;

    /**
     * 从请求中，获得认证 Token
     *
     * @param exchange 请求
     * @return 认证 Token
     */
    public static String obtainAuthorization(ServerWebExchange exchange) {
        String authorization = exchange.getRequest().getHeaders().getFirst(AUTHORIZATION_HEADER);
        if (!StringUtils.hasText(authorization)) {
            return null;
        }
        int index = authorization.indexOf(AUTHORIZATION_BEARER + " ");
        if (index == -1) { // 未找到
            return null;
        }
        return authorization.substring(index + 7).trim();
    }

    /**
     * 将 user 设置到 login-user 的请求头，使用 json 存储值
     *
     * @param builder 请求
     * @param user    用户
     */
    public static void setLoginUserHeader(ServerHttpRequest.Builder builder, LoginUser user) {
        builder.header(HEADER_LOGIN_USER, JsonUtils.toJsonString(user));
    }

    /**
     * 移除请求头的用户
     *
     * @param exchange 请求
     * @return 请求
     */
    public static ServerWebExchange removeLoginUserHeader(ServerWebExchange exchange) {
        // 如果不包含，直接返回
        if (!exchange.getRequest().getHeaders().containsKey(HEADER_LOGIN_USER)) {
            return exchange;
        }
        // 如果包含，则移除。参考 RemoveRequestHeaderGatewayFilterFactory 实现
        ServerHttpRequest request = exchange.getRequest().mutate()
                .headers(httpHeaders -> httpHeaders.remove(HEADER_LOGIN_USER)).build();
        return exchange.mutate().request(request).build();
    }

    /**
     * 设置登录用户到ServerWebExchange Attributes
     *
     * @param exchange 请求
     * @param user     用户
     */
    public static void setLoginUser(ServerWebExchange exchange, LoginUser user) {
        WebfluxFrameworkUtils.setLoginUser(exchange, user);
    }

    /**
     * 获得登录用户
     *
     * @param exchange 请求
     * @return 用户
     */
    public static LoginUser getLoginUser(ServerWebExchange exchange) {
        return WebfluxFrameworkUtils.getLoginUser(exchange);
    }

    /**
     * 获得登录用户的编号
     *
     * @param exchange 请求
     * @return 用户编号
     */
    public static Long getLoginUserId(ServerWebExchange exchange) {
        LoginUser loginUser = getLoginUser(exchange);
        if (loginUser == null) {
            return null;
        }
        return loginUser.getUserId();
    }

    /**
     * 获得登录用户的类型
     *
     * @param exchange 请求
     * @return 用户类型
     */
    public static Integer getLoginUserType(ServerWebExchange exchange) {
        LoginUser loginUser = getLoginUser(exchange);
        if (loginUser == null) {
            return null;
        }
        return loginUser.getUserType();
    }
}
