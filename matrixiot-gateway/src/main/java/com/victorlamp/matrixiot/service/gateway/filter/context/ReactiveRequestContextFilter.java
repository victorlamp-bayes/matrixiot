package com.victorlamp.matrixiot.service.gateway.filter.context;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class ReactiveRequestContextFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return chain.filter(exchange)
                .contextWrite(ctx -> ctx.put(ReactiveRequestContextHolder.CONTEXT_KEY, exchange.getRequest()))
                .contextWrite(ctx -> ctx.put(ReactiveRequestContextHolder.EXCHANGE_KEY, exchange));
    }
}
