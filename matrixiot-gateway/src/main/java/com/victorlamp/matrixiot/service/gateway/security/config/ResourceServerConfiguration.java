package com.victorlamp.matrixiot.service.gateway.security.config;

import cn.hutool.core.collection.CollUtil;
import com.victorlamp.matrixiot.common.util.collection.ArrayUtils;
import com.victorlamp.matrixiot.service.gateway.filter.security.TokenAuthenticationFilter;
import lombok.SneakyThrows;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.util.FileCopyUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;

/**
 * 自定义的 Spring Security 配置
 * 该资源服务器同时配置了认证（Authentication）和鉴权（Authorization）管理器
 * 认证（Authentication）用于验证身份
 * 鉴权（Authorization）用于验证访问系统资源的权限
 *
 * @author Bayes
 */
@AutoConfiguration
@EnableWebFluxSecurity
@EnableConfigurationProperties(SecurityProperties.class)
public class ResourceServerConfiguration {

    /**
     * 授权管理器（验证访问权限）
     */
    @Resource
    public ReactiveAuthorizationManager<AuthorizationContext> authorizationManager;

    /**
     * 安全配置
     */
    @Resource
    private SecurityProperties securityProperties;

    /**
     * 认证失败处理类 Bean
     */
    @Resource
    private ServerAuthenticationEntryPoint authenticationEntryPoint;

    /**
     * 权限不够处理器 Bean
     */
    @Resource
    private ServerAccessDeniedHandler accessDeniedHandler;

    /**
     * 配置 URL 的安全配置
     * <p>
     * anyRequest          |   匹配所有请求路径
     * access              |   SpringEl表达式结果为true时可以访问
     * anonymous           |   匿名可以访问
     * denyAll             |   用户不能访问
     * fullyAuthenticated  |   用户完全认证可以访问（非remember-me下自动登录）
     * hasAnyAuthority     |   如果有参数，参数表示权限，则其中任何一个权限可以访问
     * hasAnyRole          |   如果有参数，参数表示角色，则其中任何一个角色可以访问
     * hasAuthority        |   如果有参数，参数表示权限，则其权限可以访问
     * hasIpAddress        |   如果有参数，参数表示IP地址，如果用户IP和参数匹配，则可以访问
     * hasRole             |   如果有参数，参数表示角色，则其角色可以访问
     * permitAll           |   用户可以任意访问
     * rememberMe          |   允许通过remember-me登录的用户访问
     * authenticated       |   用户登录后可访问
     */
    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity httpSecurity) throws Exception {
        // 设置认证的jwt解码器
        httpSecurity.oauth2ResourceServer()
                .jwt().jwtDecoder(jwtDecoder())
                .and()
                .authenticationEntryPoint(authenticationEntryPoint);

        // 登出
        httpSecurity
                // 开启跨域
                .cors(Customizer.withDefaults())
                // CSRF 禁用，因为不使用 Session
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .headers(headers -> headers.frameOptions(ServerHttpSecurity.HeaderSpec.FrameOptionsSpec::disable))
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler);

        // 登录、登录暂时不使用 Spring Security 的拓展点，主要考虑一方面拓展多用户、多种登录方式相对复杂，一方面用户的学习成本较高

        // 设置所有请求的权限验证规则
        // 1：全局共享规则
        httpSecurity.authorizeExchange()
                // 1.1 静态资源，可匿名访问
                .pathMatchers(HttpMethod.GET, "/*.html", "/*.css", "/*.js").permitAll()
                // 1.2 预检请求放行
                .pathMatchers(HttpMethod.OPTIONS).permitAll();

        // 2. 基于 matrixiot.security.permit-all-urls 无需认证
        List<String> permitAllUrls = securityProperties.getPermitAllUrls();
        if (!CollUtil.isEmpty(permitAllUrls)) {
            httpSecurity.authorizeExchange()
                    .pathMatchers(ArrayUtils.toArray(securityProperties.getPermitAllUrls())).permitAll();
        }

        // 3：自定义授权管理器
        httpSecurity.authorizeExchange().anyExchange().access(authorizationManager);

        httpSecurity.addFilterAt(new TokenAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION);

        return httpSecurity.build();
    }

    /**
     * 本地加载JWT验签公钥
     */
    @SneakyThrows
    public RSAPublicKey rsaPublicKey() {
        org.springframework.core.io.Resource resource = new ClassPathResource("public.key");

        File tempFile = File.createTempFile("temp", ".key");
        FileCopyUtils.copy(resource.getInputStream(), new FileOutputStream(tempFile));
        String publicKeyStr = String.join("", Files.readAllLines(tempFile.toPath()));
        tempFile.delete();

        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    @SneakyThrows
    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        return NimbusReactiveJwtDecoder
                .withPublicKey(rsaPublicKey())
                .signatureAlgorithm(SignatureAlgorithm.RS256)
                .build();
    }
}