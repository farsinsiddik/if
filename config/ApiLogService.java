package com.tag.biometric.ifService.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tag.biometric.ifService.institutionOnboarding.repository.ApiLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.tag.biometric.ifService.config.MaskApiReqResLog.maskSensitiveData;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApiLogService {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwtSetUri;

    private final ApiLogRepository apiLogRepository;

    public Mono<Void> logRequestAsync(ServerHttpRequest request, String logId, String tracingId) {
        if (request.getURI().getPath().contains("warmup")) {
            return Mono.empty();
        }

        log.info("Incoming request to {} : {} with tracingId {}", request.getURI(), request.getMethod(), tracingId);

        String ipAddress = request.getHeaders().getFirst("host");
        String userAgent = request.getHeaders().getFirst("forwarded");
        String endpoint = request.getURI().getPath();
        String method = request.getMethod().name();

        Mono<String> userIdMono = Mono.justOrEmpty(request.getHeaders().getFirst("Authorization"))
                .filter(auth -> auth != null && auth.startsWith("Bearer "))
                .map(auth -> auth.substring(7))
                .flatMap(this::decodeJwtAndGetUserId)
                .switchIfEmpty(Mono.just("anonymous"));

        Mono<String> requestPayloadMono = request.getBody()
                .collectList()
                .map(body -> {
                    try {
                        return new ObjectMapper().writeValueAsString(body);
                    } catch (JsonProcessingException e) {
                        log.error("Failed to serialize request payload for logId {}: {}", logId, e.getMessage());
                        return "";
                    }
                })
                .defaultIfEmpty("")
                .map(payload -> maskSensitiveData(payload, endpoint, logId));

        return Mono.zip(userIdMono, requestPayloadMono)
                .flatMap(tuple -> {
                    String userId = tuple.getT1();
                    String maskedRequestPayload = tuple.getT2();

                    ApiReqResLog apiLog = ApiReqResLog.builder()
                            .uniqueId(logId)
                            .endpoint(endpoint)
                            .requestMethod(method)
                            .userId(userId)
                            .externalRequest(false)
                            .requestPayload(maskedRequestPayload)
                            .responsePayload(null)
                            .tracingId(tracingId)
                            .ipAddress(ipAddress)
                            .userAgent(userAgent)
                            .timestamp(LocalDateTime.now())
                            .build();

                    return apiLogRepository.save(apiLog).then();
                })
                .doOnError(e -> log.error("Failed to log request for logId {}: {}", logId, e.getMessage()));
    }

    public Mono<Void> logResponseAsync(String response, String logId, int statusCode) {
        log.info("Request processing completed for uniqueId {}", logId);
        if (Objects.isNull(logId)) {
            return Mono.empty();
        }

        return apiLogRepository.findByUniqueId(logId)
                .flatMap(apiLog -> {
                    apiLog.setResponseCode(statusCode);
                    apiLog.setResponsePayload(maskSensitiveData(response, apiLog.getEndpoint(), logId));
                    return apiLogRepository.save(apiLog).then();
                })
                .doOnError(e -> log.error("Failed to log response for logId {}: {}", logId, e.getMessage()))
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("No log found for uniqueId {}", logId);
                    return Mono.empty();
                }));
    }

    private Mono<String> decodeJwtAndGetUserId(String token) {
        return Mono.fromCallable(() -> {
                    Jwt jwt = findTheJwtSetUriFromToken(listIssuers(jwtSetUri), token);
                    return jwt.getClaims().get("sub").toString();
                })
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorResume(JwtException.class, e -> {
                    log.error("Failed to decode JWT: {}", e.getMessage());
                    return Mono.just("invalid-jwt");
                });
    }

    private Jwt findTheJwtSetUriFromToken(List<String> jwkSetUris, String token) {
        for (String jwkSetUri : jwkSetUris) {
            try {
                NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
                return jwtDecoder.decode(token);
            } catch (JwtException e) {
                continue;
            }
        }
        throw new JwtException("Invalid JWT Token");
    }

    private List<String> listIssuers(String issuerUris) {
        return Arrays.stream(issuerUris.split(",")).toList();
    }
}