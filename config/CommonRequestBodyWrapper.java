package com.tag.biometric.ifService.config;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class CommonRequestBodyWrapper implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        if (exchange.getRequest().getMethod() == null ||
                !exchange.getRequest().getMethod().matches("POST|PUT|PATCH")) {
            return chain.filter(exchange);
        }

        return DataBufferUtils.join(exchange.getRequest().getBody())
                .flatMap(dataBuffer -> {
                    byte[] bodyBytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bodyBytes);
                    DataBufferUtils.release(dataBuffer);

                    String requestBody = new String(bodyBytes, StandardCharsets.UTF_8);
                    exchange.getAttributes().put("cachedRequestBody", requestBody); // Save it for later

                    Flux<DataBuffer> cachedBody = Flux.defer(() ->
                            Mono.just(exchange.getResponse().bufferFactory().wrap(bodyBytes)));

                    ServerHttpRequestDecorator decoratedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
                        @Override
                        public Flux<DataBuffer> getBody() {
                            return cachedBody;
                        }
                    };

                    return chain.filter(exchange.mutate().request(decoratedRequest).build());
                });
    }
}
