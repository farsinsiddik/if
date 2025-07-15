package com.tag.biometric.ifService.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.tag.biometric.ifService.util.Utils.generateUniqueLogId;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApiReqResLogger {

    private final ApplicationEventPublisher eventPublisher;
    private final ApiLogService loggingService;
    private final Tracer tracer;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public Object logRequestAndResponse(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();

        String logId = generateUniqueLogId();
        String tracingId = tracer.currentSpan() != null ? tracer.currentSpan().context().traceId() : "no-trace";

        Mono<ServerHttpRequest> requestMono = ReactiveRequestContextHolder.getRequest();

        if (result instanceof Mono) {
            return requestMono.flatMap(request -> {
                Mono<Void> logRequest = loggingService.logRequestAsync(request, logId, tracingId);

                return logRequest.then(
                        ((Mono<?>) result).flatMap(response -> {
                            try {
                                String responseJson = objectMapper.writeValueAsString(response);
                                int statusCode = 200; // You can enhance to get real status if needed
                                return loggingService.logResponseAsync(responseJson, logId, statusCode)
                                        .thenReturn(response);
                            } catch (JsonProcessingException e) {
                                log.error("Failed to serialize Mono response: {}", e.getMessage());
                                return Mono.just(response);
                            }
                        }).doOnError(e -> log.error("Error in Mono response for logId {}: {}", logId, e.getMessage()))
                );
            });
        } else if (result instanceof Flux) {
            return requestMono.flatMapMany(request -> {
                Mono<Void> logRequest = loggingService.logRequestAsync(request, logId, tracingId);

                return logRequest.thenMany(
                        ((Flux<?>) result).flatMap(response -> {
                            try {
                                String responseJson = objectMapper.writeValueAsString(response);
                                int statusCode = 200;
                                return loggingService.logResponseAsync(responseJson, logId, statusCode)
                                        .then(Mono.just(response));
                            } catch (JsonProcessingException e) {
                                log.error("Failed to serialize Flux response: {}", e.getMessage());
                                return Mono.just(response);
                            }
                        }).doOnError(e -> log.error("Error in Flux response for logId {}: {}", logId, e.getMessage()))
                );
            });
        } else {
            return requestMono.flatMap(request -> {
                Mono<Void> logRequest = loggingService.logRequestAsync(request, logId, tracingId);
                try {
                    String responseJson = objectMapper.writeValueAsString(result);
                    int statusCode = 200;
                    Mono<Void> logResponse = loggingService.logResponseAsync(responseJson, logId, statusCode);

                    return logRequest.then(logResponse).thenReturn(result);
                } catch (JsonProcessingException e) {
                    log.error("Failed to serialize non-reactive response: {}", e.getMessage());
                    return Mono.just(result);
                }
            });
        }
    }
}
