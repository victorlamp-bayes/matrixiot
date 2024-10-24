package com.victorlamp.matrixiot.service.gateway.security.core.handler;

import com.alibaba.fastjson2.JSON;
import com.victorlamp.matrixiot.common.exception.enums.GlobalErrorCodeConstants;
import com.victorlamp.matrixiot.common.pojo.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

import static com.victorlamp.matrixiot.common.exception.enums.GlobalErrorCodeConstants.UNAUTHORIZED;

/**
 * 访问一个需要认证的 URL 资源，但是此时自己尚未认证（登录）的情况下，返回 {@link GlobalErrorCodeConstants#UNAUTHORIZED} 错误码，从而使前端重定向到登录页
 *
 * @author ruoyi
 */
@Slf4j
public class AuthenticationEntryPointImpl implements ServerAuthenticationEntryPoint {

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException e) {
        log.debug("[commence][访问 URL({}) 时，没有登录]", exchange.getRequest().getURI(), e);

        ServerHttpResponse response = exchange.getResponse();
        String body = JSON.toJSONString(CommonResult.error(UNAUTHORIZED));
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));

        // 返回 401
        return response.writeWith(Mono.just(buffer));
    }
}
