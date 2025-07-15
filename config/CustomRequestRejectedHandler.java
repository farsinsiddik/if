package com.tag.biometric.ifService.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tag.biometric.ifService.config.exception.ReactiveRequestRejectedException;
import com.tag.biometric.ifService.others.classes.ExceptionResponse;
import com.tag.biometric.ifService.others.constants.ProcessorErrorConstants;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class CustomRequestRejectedHandler implements WebExceptionHandler {

    private final Tracer tracer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if (ex instanceof ReactiveRequestRejectedException) {
            HttpStatus status = HttpStatus.FORBIDDEN;
            String tracingId = tracer.currentSpan() != null ? tracer.currentSpan().context().traceId() : "N/A";

            ExceptionResponse exceptionResponse = new ExceptionResponse(
                    status.value(),
                    "The request contains invalid characters in the path parameters.",
                    ProcessorErrorConstants.BCAAS,
                    tracingId
            );

            byte[] bytes;
            try {
                bytes = objectMapper.writeValueAsBytes(exceptionResponse);
            } catch (Exception e) {
                bytes = ("{\"message\":\"Internal error\"}").getBytes(StandardCharsets.UTF_8);
            }

            exchange.getResponse().setStatusCode(status);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

            DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
            DataBuffer dataBuffer = bufferFactory.wrap(bytes);

            return exchange.getResponse().writeWith(Mono.just(dataBuffer));
        }

        return Mono.error(ex);
    }
}
