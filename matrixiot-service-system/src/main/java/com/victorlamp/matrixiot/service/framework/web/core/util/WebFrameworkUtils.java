package com.victorlamp.matrixiot.service.framework.web.core.util;

import cn.hutool.core.util.NumberUtil;
import com.victorlamp.matrixiot.common.pojo.CommonResult;
import com.victorlamp.matrixiot.common.util.json.JsonUtils;
import com.victorlamp.matrixiot.service.system.authorization.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import static com.victorlamp.matrixiot.service.system.authorization.enums.LoginUserConstants.HEADER_LOGIN_USER;
import static com.victorlamp.matrixiot.service.system.authorization.enums.LoginUserConstants.HEADER_TENANT_ID;

/**
 * 专属于 web 包的工具类
 *
 * @author 芋道源码
 */
@Slf4j
public class WebFrameworkUtils {

    private static final String REQUEST_ATTRIBUTE_COMMON_RESULT = "common_result";

    public WebFrameworkUtils() {
    }

    public static Long getTenantId() {
        HttpServletRequest request = getRequest();
        return getTenantId(request);
    }

    /**
     * 获得租户编号，从 header 中
     * 考虑到其它 framework 组件也会使用到租户编号，所以不得不放在 WebFrameworkUtils 统一提供
     *
     * @param request 请求
     * @return 租户编号
     */
    public static Long getTenantId(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        // 已登录用户，从请求头的LoginUser中获取tenantId
        LoginUser loginUser = getLoginUser();
        if (loginUser != null) {
            return loginUser.getTenantId();
        }

        // 未登录用户，使用请求头中直接传递的tenantId
        String tenantId = request.getHeader(HEADER_TENANT_ID);
        return NumberUtil.isNumber(tenantId) ? Long.valueOf(tenantId) : null;
    }

    public static Long getLoginUserId() {
        HttpServletRequest request = getRequest();
        return getLoginUserId(request);
    }

    /**
     * 获得当前用户的编号，从请求中
     * 注意：该方法仅限于 framework 框架使用！！！
     *
     * @param request 请求
     * @return 用户编号
     */
    public static Long getLoginUserId(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        LoginUser loginUser = getLoginUser(request);
        if (loginUser == null) {
            return null;
        }

        return loginUser.getUserId();
    }

    public static Integer getLoginUserType() {
        HttpServletRequest request = getRequest();
        return getLoginUserType(request);
    }

    /**
     * 获得当前用户的类型
     * 注意：该方法仅限于 web 相关的 framework 组件使用！！！
     *
     * @param request 请求
     * @return 用户编号
     */
    public static Integer getLoginUserType(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        LoginUser loginUser = getLoginUser(request);
        if (loginUser == null) {
            return null;
        }

        return loginUser.getUserType();
    }

    public static LoginUser getLoginUser() {
        HttpServletRequest request = getRequest();
        return getLoginUser(request);
    }

    public static LoginUser getLoginUser(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        String loginUserStr = request.getHeader(HEADER_LOGIN_USER);
        try {
            return JsonUtils.parseObject(loginUserStr, LoginUser.class);
        } catch (Exception e) {
            log.info("构造LoginUser失败，非法格式:{}", loginUserStr);
        }

        return null;
    }

    public static void setCommonResult(ServletRequest request, CommonResult<?> result) {
        request.setAttribute(REQUEST_ATTRIBUTE_COMMON_RESULT, result);
    }

    public static CommonResult<?> getCommonResult(ServletRequest request) {
        return (CommonResult<?>) request.getAttribute(REQUEST_ATTRIBUTE_COMMON_RESULT);
    }

    public static HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (!(requestAttributes instanceof ServletRequestAttributes servletRequestAttributes)) {
            return null;
        }

        return servletRequestAttributes.getRequest();
    }
}
