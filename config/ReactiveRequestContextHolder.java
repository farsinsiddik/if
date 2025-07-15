package com.tag.biometric.ifService.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ReactiveRequestContextHolder {

    public static final String CONTEXT_KEY = "serverHttpRequest";

    public static Mono<ServerHttpRequest> getRequest() {
        return Mono.deferContextual(ctx -> {
            if (ctx.hasKey(CONTEXT_KEY)) {
                return Mono.just(ctx.get(CONTEXT_KEY));
            } else {
                return Mono.empty();
            }
        });
    }
}