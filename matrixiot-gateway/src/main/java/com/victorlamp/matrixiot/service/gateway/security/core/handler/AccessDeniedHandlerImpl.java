package com.victorlamp.matrixiot.service.gateway.security.core.handler;

import com.alibaba.fastjson2.JSON;
import com.victorlamp.matrixiot.common.exception.enums.GlobalErrorCodeConstants;
import com.victorlamp.matrixiot.common.pojo.CommonResult;
import com.victorlamp.matrixiot.service.gateway.util.SecurityFrameworkUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

import static com.victorlamp.matrixiot.common.exception.enums.GlobalErrorCodeConstants.FORBIDDEN;

/**
 * 访问一个需要认证的 URL 资源，已经认证（登录）但是没有权限的情况下，返回 {@link GlobalErrorCodeConstants#FORBIDDEN} 错误码。
 *
 * @author 芋道源码
 */
@Slf4j
public class AccessDeniedHandlerImpl implements ServerAccessDeniedHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException e) {
        log.warn("[commence][访问 URL({}) 时，用户({}) 权限不够]", exchange.getRequest().getURI(),
                SecurityFrameworkUtils.getLoginUserId(exchange), e);

        ServerHttpResponse response = exchange.getResponse();
        String body = JSON.toJSONString(CommonResult.error(FORBIDDEN));
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));

        // 返回 403
        return response.writeWith(Mono.just(buffer));
    }
}
