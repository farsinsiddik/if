package com.tag.biometric.ifService.config;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

public class DefaultRequestBodyWrapper {

    private final ServerHttpRequest delegate;
    private final DataBufferFactory dataBufferFactory;
    private Mono<String> cachedBodyMono;

    public DefaultRequestBodyWrapper(ServerHttpRequest delegate, DataBufferFactory dataBufferFactory) {
        this.delegate = delegate;
        this.dataBufferFactory = dataBufferFactory;
        cacheRequestBody();
    }

    private void cacheRequestBody() {
        // Cache the full request body as a String reactively
        cachedBodyMono = DataBufferUtils.join(delegate.getBody())
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return new String(bytes, StandardCharsets.UTF_8);
                })
                .cache();
    }

    public Mono<String> getRequestBody() {
        return cachedBodyMono;
    }

    public ServerHttpRequest getDelegate() {
        return delegate;
    }

    public Flux<DataBuffer> getBody() {
        return cachedBodyMono.flatMapMany(bodyString -> {
            byte[] bytes = bodyString.getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = dataBufferFactory.wrap(bytes);
            return Flux.just(buffer);
        });
    }
}
