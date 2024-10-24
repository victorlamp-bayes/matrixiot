package com.victorlamp.matrixiot.service.gateway.filter.context;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class ReactiveRequestContextHolder {
    public static final Class<ServerHttpRequest> CONTEXT_KEY = ServerHttpRequest.class;
    public static final Class<ServerWebExchange> EXCHANGE_KEY = ServerWebExchange.class;

    public static Mono<ServerHttpRequest> getRequest() {
//        return Mono.subscriberContext()
//                .map(ctx -> ctx.get(CONTEXT_KEY));
        return Mono.deferContextual(ctx -> Mono.just(ctx.get(CONTEXT_KEY)));
    }

    public static Mono<ServerWebExchange> getExchange() {
        return Mono.deferContextual(ctx -> Mono.just(ctx.get(EXCHANGE_KEY)));
    }
}
