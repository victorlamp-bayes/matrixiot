package com.victorlamp.matrixiot.service.gateway.security.config;

import com.victorlamp.matrixiot.service.gateway.security.core.handler.AccessDeniedHandlerImpl;
import com.victorlamp.matrixiot.service.gateway.security.core.handler.AuthenticationEntryPointImpl;
import com.victorlamp.matrixiot.service.gateway.security.core.handler.AuthorizationManagerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;

import javax.annotation.Resource;

/**
 * Spring Security 自动配置类，主要用于相关组件的配置
 *
 * @author 芋道源码
 */
//@AutoConfiguration
public class SecurityAutoConfiguration {

    /**
     * 安全配置
     */
    @Resource
    private SecurityProperties securityProperties;

    /**
     * 认证失败处理类 Bean
     */
    @Bean
    public ServerAuthenticationEntryPoint authenticationEntryPoint() {
        return new AuthenticationEntryPointImpl();
    }

    /**
     * 授权失败处理类 Bean
     */
    @Bean
    public ServerAccessDeniedHandler accessDeniedHandler() {
        return new AccessDeniedHandlerImpl();
    }

    /**
     * Spring Security 加密器
     * 考虑到安全性，这里采用 BCryptPasswordEncoder 加密器
     *
     * @see <a href="http://stackabuse.com/password-encoding-with-spring-security/">Password Encoding with Spring Security</a>
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(securityProperties.getPasswordEncoderLength());
    }

    /**
     * 授权管理器（验证访问权限）
     */
    @Bean
    public AuthorizationManagerImpl authorizationManager() {
        return new AuthorizationManagerImpl();
    }
}
