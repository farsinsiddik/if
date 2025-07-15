package com.tag.biometric.ifService.config;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class RequestBodyWrapper {

    protected final ServerHttpRequest request;

    protected RequestBodyWrapper(ServerHttpRequest request) {
        this.request = request;
    }

    public abstract Mono<String> getRequestBody();

    public abstract Flux<DataBuffer> getBody();
}
