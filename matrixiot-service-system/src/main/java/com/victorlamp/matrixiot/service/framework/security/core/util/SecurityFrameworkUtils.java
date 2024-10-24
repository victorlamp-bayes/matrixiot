package com.victorlamp.matrixiot.service.framework.security.core.util;

import cn.hutool.core.util.StrUtil;
import com.victorlamp.matrixiot.service.framework.web.core.util.WebFrameworkUtils;
import com.victorlamp.matrixiot.service.system.authorization.LoginUser;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 安全服务工具类
 *
 * @author 芋道源码
 */
public class SecurityFrameworkUtils {

    /**
     * HEADER 认证头 value 的前缀
     */
    public static final String AUTHORIZATION_BEARER = "Bearer";

    private SecurityFrameworkUtils() {
    }

    /**
     * 从请求中，获得认证 Token
     *
     * @param request       请求
     * @param headerName    认证 Token 对应的 Header 名字
     * @param parameterName 认证 Token 对应的 Parameter 名字
     * @return 认证 Token
     */
    public static String obtainAuthorization(HttpServletRequest request, String headerName, String parameterName) {
        // 1. 获得 Token。优先级：Header > Parameter
        String token = request.getHeader(headerName);
        if (StrUtil.isEmpty(token)) {
            token = request.getParameter(parameterName);
        }
        if (!StringUtils.hasText(token)) {
            return null;
        }
        // 2. 去除 Token 中带的 Bearer
        int index = token.indexOf(AUTHORIZATION_BEARER + " ");
        return index >= 0 ? token.substring(index + 7).trim() : token;
    }

    /**
     * 获取当前用户
     *
     * @return 当前用户
     */
    @Nullable
    public static LoginUser getLoginUser() {
        return WebFrameworkUtils.getLoginUser();
    }

    /**
     * 获得当前用户的编号，从上下文中
     *
     * @return 用户编号
     */
    @Nullable
    public static Long getLoginUserId() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null ? loginUser.getUserId() : null;
    }
}
