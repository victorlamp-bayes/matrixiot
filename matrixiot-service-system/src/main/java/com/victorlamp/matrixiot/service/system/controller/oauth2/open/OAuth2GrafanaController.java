package com.victorlamp.matrixiot.service.system.controller.oauth2.open;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.victorlamp.matrixiot.common.enums.UserTypeEnum;
import com.victorlamp.matrixiot.common.pojo.CommonResult;
import com.victorlamp.matrixiot.common.util.http.HttpUtils;
import com.victorlamp.matrixiot.common.util.json.JsonUtils;
import com.victorlamp.matrixiot.service.framework.operatelog.core.annotation.OperateLog;
import com.victorlamp.matrixiot.service.framework.security.core.util.OAuth2Utils;
import com.victorlamp.matrixiot.service.system.api.oauth2.OAuth2ApproveService;
import com.victorlamp.matrixiot.service.system.api.oauth2.OAuth2ClientService;
import com.victorlamp.matrixiot.service.system.api.oauth2.OAuth2GrantService;
import com.victorlamp.matrixiot.service.system.api.oauth2.OAuth2TokenService;
import com.victorlamp.matrixiot.service.system.api.user.AdminUserService;
import com.victorlamp.matrixiot.service.system.authorization.LoginUser;
import com.victorlamp.matrixiot.service.system.controller.oauth2.vo.open.OAuth2OpenAccessTokenRespVO;
import com.victorlamp.matrixiot.service.system.controller.oauth2.vo.open.OAuth2OpenCheckTokenRespVO;
import com.victorlamp.matrixiot.service.system.convert.oauth2.OAuth2OpenConvert;
import com.victorlamp.matrixiot.service.system.entity.oauth2.OAuth2AccessTokenDO;
import com.victorlamp.matrixiot.service.system.entity.oauth2.OAuth2ClientDO;
import com.victorlamp.matrixiot.service.system.entity.user.AdminUserDO;
import com.victorlamp.matrixiot.service.system.enums.oauth2.OAuth2GrantTypeEnum;
import com.victorlamp.matrixiot.service.system.service.tenant.TenantUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.victorlamp.matrixiot.common.exception.enums.GlobalErrorCodeConstants.BAD_REQUEST;
import static com.victorlamp.matrixiot.common.exception.enums.GlobalErrorCodeConstants.UNAUTHORIZED;
import static com.victorlamp.matrixiot.common.exception.util.ServiceExceptionUtil.exception;
import static com.victorlamp.matrixiot.common.exception.util.ServiceExceptionUtil.exception0;
import static com.victorlamp.matrixiot.common.pojo.CommonResult.success;
import static com.victorlamp.matrixiot.common.util.collection.CollectionUtils.convertList;
import static com.victorlamp.matrixiot.service.framework.security.core.util.SecurityFrameworkUtils.getLoginUser;
import static com.victorlamp.matrixiot.service.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

/**
 * 适配Grafana
 */
@Tag(name = "管理后台 - OAuth2.0 授权给 Grafana")
@RestController
@RequestMapping(value = "/api/v1/system/oauth2/grafana")
@Validated
@Slf4j
public class OAuth2GrafanaController {

    private final Cache<String, LoginUser> loginUserCache = Caffeine.newBuilder().build();
    @Resource
    private OAuth2GrantService oauth2GrantService;
    @Resource
    private OAuth2ClientService oauth2ClientService;
    @Resource
    private OAuth2ApproveService oauth2ApproveService;
    @Resource
    private AdminUserService userService;
    @Resource
    private OAuth2TokenService oauth2TokenService;

    private static OAuth2GrantTypeEnum getGrantTypeEnum(String responseType) {
        if (StrUtil.equals(responseType, "code")) {
            return OAuth2GrantTypeEnum.AUTHORIZATION_CODE;
        }
        if (StrUtil.equalsAny(responseType, "token")) {
            return OAuth2GrantTypeEnum.IMPLICIT;
        }
        throw exception0(BAD_REQUEST.getCode(), "response_type 参数值只允许 code 和 token");
    }

    @GetMapping("/userinfo")
    @Operation(summary = "获得用户信息")
    @OperateLog(enable = false) // 避免 Post 请求被记录操作日志
    public Map<String, String> getUserInfo(HttpServletRequest request) {
        OAuth2AccessTokenDO oAuth2AccessTokenDO = oauth2TokenService.getAccessToken(StrUtil.removePrefixIgnoreCase(request.getHeader("Authorization"), "Bearer "));

        if (oAuth2AccessTokenDO == null) {
            throw exception(UNAUTHORIZED);
        }

        AdminUserDO user = userService.getUser(oAuth2AccessTokenDO.getUserId());
        if (user == null) {
            throw exception(UNAUTHORIZED);
        }

        Map<String, String> map = new LinkedHashMap<>();
        String prefix = "matrixiot_net_" + user.getTenantId() + "_";
        map.put("username", prefix + user.getUsername()); // login or username
        map.put("name", prefix + user.getNickname()); // name or display_name
        map.put("email", prefix + user.getUsername() + "@victorlamp.com");
        return map;
    }

    @PostMapping("/token")
    @Operation(summary = "获得访问令牌", description = "适合 code 授权码模式")
    @Parameters({
            @Parameter(name = "grant_type", required = true, description = "授权类型", example = "authorization_code"),
            @Parameter(name = "code", description = "授权范围"),
            @Parameter(name = "redirect_uri", description = "重定向 URI"),
            @Parameter(name = "state", description = "状态", example = "1"),
            @Parameter(name = "scope", example = "user_info"),
            @Parameter(name = "refresh_token", example = "123424233"),
    })
    @OperateLog(enable = false) // 避免 Post 请求被记录操作日志
    public OAuth2OpenAccessTokenRespVO postAccessToken(HttpServletRequest request,
                                                       @RequestParam("grant_type") String grantType,
                                                       @RequestParam(value = "code", required = false) String code, // 授权码模式
                                                       @RequestParam(value = "redirect_uri", required = false) String redirectUri, // 授权码模式
                                                       @RequestParam(value = "state", required = false) String state, // 授权码模式
                                                       @RequestParam(value = "scope", required = false) String scope, // 授权码模式
                                                       @RequestParam(value = "refresh_token", required = false) String refreshToken) { // 刷新模式

        List<String> scopes = OAuth2Utils.buildScopes(scope);
        // 1.1 校验授权类型
        OAuth2GrantTypeEnum grantTypeEnum = OAuth2GrantTypeEnum.getByGranType(grantType);
        if (grantTypeEnum == null) {
            throw exception0(BAD_REQUEST.getCode(), StrUtil.format("未知授权类型({})", grantType));
        }
        if (grantTypeEnum == OAuth2GrantTypeEnum.IMPLICIT) {
            throw exception0(BAD_REQUEST.getCode(), "Token 接口不支持 implicit 授权模式");
        }

        // 1.2 校验客户端
        String[] clientIdAndSecret = obtainBasicAuthorization(request);
        OAuth2ClientDO client = oauth2ClientService.validOAuthClientFromCache(clientIdAndSecret[0], clientIdAndSecret[1], grantType, scopes, redirectUri);

        AtomicReference<OAuth2AccessTokenDO> accessTokenDO = new AtomicReference<>();
        LoginUser loginUser = loginUserCache.getIfPresent(code);
        assert loginUser != null;
        // 2. 根据授权模式，获取访问令牌
        TenantUtils.execute(loginUser.getTenantId(), () -> {
            accessTokenDO.set(switch (grantTypeEnum) {
                case AUTHORIZATION_CODE -> oauth2GrantService.grantAuthorizationCodeForAccessToken(client.getClientId(), code, redirectUri, state);
                case REFRESH_TOKEN -> oauth2GrantService.grantRefreshToken(refreshToken, client.getClientId());
                default -> throw new IllegalArgumentException("未知授权类型：" + grantType);
            });
        });

        Assert.notNull(accessTokenDO, "访问令牌不能为空"); // 防御性检查
        return OAuth2OpenConvert.INSTANCE.convert(accessTokenDO.get());
    }


    /**
     * 对应 Spring Security OAuth 的 CheckTokenEndpoint 类的 checkToken 方法
     */
    @PostMapping("/check-token")
    @Operation(summary = "校验访问令牌")
    @Parameter(name = "token", required = true, description = "访问令牌", example = "biu")
    @OperateLog(enable = false) // 避免 Post 请求被记录操作日志
    public CommonResult<OAuth2OpenCheckTokenRespVO> checkToken(HttpServletRequest request,
                                                               @RequestParam("token") String token) {
        // 校验客户端
        String[] clientIdAndSecret = obtainBasicAuthorization(request);
        oauth2ClientService.validOAuthClientFromCache(clientIdAndSecret[0], clientIdAndSecret[1],
                null, null, null);

        // 校验令牌
        OAuth2AccessTokenDO accessTokenDO = oauth2TokenService.checkAccessToken(token);
        Assert.notNull(accessTokenDO, "访问令牌不能为空"); // 防御性检查
        return success(OAuth2OpenConvert.INSTANCE.convert2(accessTokenDO));
    }

    /**
     * 对应 Spring Security OAuth 的 AuthorizationEndpoint 类的 approveOrDeny 方法
     * <p>
     * 因为前后端分离，Axios 无法很好的处理 302 重定向，所以和 Spring Security OAuth 略有不同，返回结果是重定向的 URL，剩余交给前端处理
     */
    @GetMapping("/authorize")
    @Operation(summary = "申请授权", description = "适合 code 授权码模式，或者 implicit 简化模式；在 sso.vue 单点登录界面被【提交】调用")
    @Parameters({
            @Parameter(name = "response_type", required = true, description = "响应类型", example = "code"),
            @Parameter(name = "client_id", required = true, description = "客户端编号"),
            @Parameter(name = "scope", description = "授权范围", example = "userinfo.read"), // 使用 Map<String, Boolean> 格式，Spring MVC 暂时不支持这么接收参数
            @Parameter(name = "redirect_uri", required = true, description = "重定向 URI"),
            @Parameter(name = "state", example = "1")
    })
    @OperateLog(enable = false) // 避免 Post 请求被记录操作日志
    public CommonResult<String> approveOrDeny(HttpServletRequest request,
                                              HttpServletResponse response,
                                              @RequestParam("response_type") String responseType,
                                              @RequestParam("client_id") String clientId,
                                              @RequestParam(value = "scope", required = false) String scope,
                                              @RequestParam("redirect_uri") String redirectUri,
                                              @RequestParam(value = "state", required = false) String state) throws IOException {
        @SuppressWarnings("unchecked")
        Map<String, Boolean> scopes = JsonUtils.parseObject(scope, Map.class);
        scopes = ObjectUtil.defaultIfNull(scopes, Collections.emptyMap());
        // 0. 校验用户已经登录。通过 Spring Security 实现

        // 1.1 校验 responseType 是否满足 code 或者 token 值
        OAuth2GrantTypeEnum grantTypeEnum = getGrantTypeEnum(responseType);
        // 1.2 校验 redirectUri 重定向域名是否合法 + 校验 scope 是否在 Client 授权范围内
        OAuth2ClientDO client = oauth2ClientService.validOAuthClientFromCache(clientId, null,
                grantTypeEnum.getGrantType(), scopes.keySet(), redirectUri);

        // 2.1 如果是 code 授权码模式，则发放 code 授权码，并重定向
        List<String> approveScopes = convertList(scopes.entrySet(), Map.Entry::getKey, Map.Entry::getValue);
        if (grantTypeEnum == OAuth2GrantTypeEnum.AUTHORIZATION_CODE) {
            return success(getAuthorizationCodeRedirect(getLoginUserId(), client, approveScopes, redirectUri, state));
        }
        // 2.2 如果是 token 则是 implicit 简化模式，则发送 accessToken 访问令牌，并重定向
        return success(getImplicitGrantRedirect(getLoginUserId(), client, approveScopes, redirectUri, state));
    }

    private String getImplicitGrantRedirect(Long userId, OAuth2ClientDO client,
                                            List<String> scopes, String redirectUri, String state) {
        // 1. 创建 access token 访问令牌
        OAuth2AccessTokenDO accessTokenDO = oauth2GrantService.grantImplicit(userId, getUserType(), client.getClientId(), scopes);
        Assert.notNull(accessTokenDO, "访问令牌不能为空"); // 防御性检查
        // 2. 拼接重定向的 URL
        // noinspection unchecked
        return OAuth2Utils.buildImplicitRedirectUri(redirectUri, accessTokenDO.getAccessToken(), state, accessTokenDO.getExpiresTime(),
                scopes, JsonUtils.parseObject(client.getAdditionalInformation(), Map.class));
    }

    private String getAuthorizationCodeRedirect(Long userId, OAuth2ClientDO client, List<String> scopes, String redirectUri, String state) {
        // 1. 创建 code 授权码
        String code = oauth2GrantService.grantAuthorizationCodeForCode(userId, getUserType(), client.getClientId(), scopes, redirectUri, state);

        loginUserCache.put(code, getLoginUser());

        // 2. 拼接重定向的 URL
        return OAuth2Utils.buildAuthorizationCodeRedirectUri(redirectUri, code, state);
    }

    private Integer getUserType() {
        return UserTypeEnum.ADMIN.getValue();
    }

    private String[] obtainBasicAuthorization(HttpServletRequest request) {
        String[] clientIdAndSecret = HttpUtils.obtainBasicAuthorization(request);
        if (ArrayUtil.isEmpty(clientIdAndSecret) || clientIdAndSecret.length != 2) {
            throw exception0(BAD_REQUEST.getCode(), "client_id 或 client_secret 未正确传递");
        }
        return clientIdAndSecret;
    }

}
