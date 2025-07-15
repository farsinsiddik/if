package com.tag.biometric.ifService.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tag.biometric.ifService.config.exception.InvalidClientException;
import com.tag.biometric.ifService.config.exception.InvalidPathParamException;
import com.tag.biometric.ifService.config.model.ExceptionResponse;
import io.micrometer.tracing.Tracer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.tag.biometric.ifService.config.IfConstants.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.readOnlyHttpHeaders;

@Slf4j
@Component
@AllArgsConstructor
public class FilterConfig implements WebFilter {

    private final Tracer tracer;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static final List<String> WRAP_VERBS = List.of("POST", "PUT", "GET", "PATCH", "DELETE");

    public static final String CONTEXT_CLIENT_ID_KEY = "clientId";
    public static final String CONTEXT_ROLE_KEY = "role";
    public static final String CONTEXT_TOKEN_KEY = "token";
    public static final String CLIENT_ID_HEADER = "client-id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        String requestURI = request.getURI().getPath();

        try {
            if (!requestURI.contains("invitations") && containsSpecialCharactersInPathParameters(request.getPath().value())) {
                throw new InvalidPathParamException("Invalid characters in path parameters");
            }
        } catch (InvalidPathParamException e) {
            return handleError(response, HttpStatus.BAD_REQUEST, e.getMessage());
        }

        if ((requestURI.contains(ACTUATOR) && requestURI.contains(IF_URL)) ||
                (requestURI.contains(SWAGGER_URL) && requestURI.contains(IF_URL))) {
            String newURI = requestURI.replace(IF_URL, "");
            ServerHttpRequest mutatedRequest = request.mutate().path(newURI).build();
            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        }

        if (WRAP_VERBS.contains(request.getMethod().name())) {
            return DataBufferUtils.join(request.getBody())
                    .defaultIfEmpty(exchange.getResponse().bufferFactory().wrap(new byte[0]))
                    .flatMap(dataBuffer -> {
                        byte[] bodyBytes = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(bodyBytes);
                        DataBufferUtils.release(dataBuffer);

                        ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(request) {
                            @Override
                            public Flux<DataBuffer> getBody() {
                                return Flux.just(exchange.getResponse().bufferFactory().wrap(bodyBytes));
                            }
                        };

                        return processAuthAndContinue(exchange.mutate().request(mutatedRequest).build(), chain);
                    });
        } else {
            return processAuthAndContinue(exchange, chain);
        }
    }

    private Mono<Void> processAuthAndContinue(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String method = request.getMethod().name();

        if (!WRAP_VERBS.contains(method)) {
            return chain.filter(exchange);
        }

        return extractClientInfoFromAuthHeader(request.getHeaders())
                .flatMap(context -> {
//                    if (RoleConstant.CLIENT_MANAGER.equals(context.role)) {
                        if (context.clientId == null) {
                            return Mono.error(new InvalidClientException("Link a client to the user to enable access to the IBAN solution."));
                        }
                        exchange.getAttributes().put(CONTEXT_CLIENT_ID_KEY, context.clientId);
                        exchange.getAttributes().put(CONTEXT_ROLE_KEY, context.role);
                        exchange.getAttributes().put(CONTEXT_TOKEN_KEY, context.token);

                        // Store into Reactor context
                        return chain.filter(exchange)
                                .contextWrite(ctx -> ctx
                                        .put(CONTEXT_CLIENT_ID_KEY, context.clientId)
                                        .put(CONTEXT_ROLE_KEY, context.role)
                                        .put(CONTEXT_TOKEN_KEY, context.token)
                                );
//                    }
//                    return chain.filter(exchange);
                })
                .onErrorResume(e -> {
                    if (e instanceof InvalidClientException) {
                        return handleError(exchange.getResponse(), HttpStatus.BAD_REQUEST, e.getMessage());
                    } else {
                        return handleError(exchange.getResponse(), HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error Occurred. Mail Sent.");
                    }
                });
    }

    private Mono<ClientContext> extractClientInfoFromAuthHeader(HttpHeaders headers) {

        String authHeader = headers.getFirst(AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Mono.just(new ClientContext(null, null, null));
        }

        String token = authHeader.substring(7);
        try {
            String[] chunks = token.split("\\.");
            if (chunks.length < 2) {
                return Mono.error(new InvalidClientException("Invalid JWT token."));
            }
            Base64.Decoder base64Decoder = Base64.getUrlDecoder();
            String payloadJson = new String(base64Decoder.decode(chunks[1]), StandardCharsets.UTF_8);
            JSONObject payload = (JSONObject) new JSONParser().parse(payloadJson);

            Object clientIdObj = payload.get(CLIENT_ID);
            List<String> roles = extractRoles(payload);
            boolean isClientManager = roles.contains(RoleConstant.CLIENT_MANAGER);

            if (isClientManager && clientIdObj == null) {
                return Mono.error(new InvalidClientException("Client Id not mapped with keycloak."));
            }
            if (isClientManager && !String.valueOf(payload.get(MODULES)).toLowerCase().contains("if")) {
                return Mono.error(new InvalidClientException("The user is not authorized to access the IBAN solution."));
            }

            if (!isClientManager && clientIdObj == null) {
                if(Objects.nonNull(headers.getFirst(CLIENT_ID_HEADER))) {
                    clientIdObj = headers.getFirst(CLIENT_ID_HEADER);
                } else {
                    return Mono.error(new InvalidClientException("Please add CLIENT_ID in header inorder to access IBAN data."));
                }
            }

            return Mono.just(new ClientContext(
                    clientIdObj != null ? clientIdObj.toString() : null,
                    isClientManager ? RoleConstant.CLIENT_MANAGER : RoleConstant.BCASS_ADMIN,
                    token
            ));
        } catch (ParseException | IllegalArgumentException e) {
            return Mono.error(new InvalidClientException("Failed to parse JWT token."));
        } catch (Exception e) {
            log.error("Unexpected error processing JWT token", e);
            return Mono.error(new InvalidClientException("Authentication processing error."));
        }
    }

    private List<String> extractRoles(JSONObject payload) {
        try {
            JSONObject realmAccess = (JSONObject) payload.get("realm_access");
            if (realmAccess != null) {
                return (List<String>) realmAccess.get("roles");
            }
            return Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private Mono<Void> handleError(ServerHttpResponse response, HttpStatus status, String message) {
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String tracingId = tracer.currentSpan() != null ? tracer.currentSpan().context().traceId() : "N/A";
        ExceptionResponse errorBody = new ExceptionResponse(status.value(), message, BCAAS, tracingId);

        try {
            byte[] bytes = OBJECT_MAPPER.writeValueAsBytes(errorBody);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (Exception e) {
            log.error("Error writing error response", e);
            return response.setComplete();
        }
    }

    private boolean containsSpecialCharactersInPathParameters(String input) {
        String[] pathSegments = input.split("/");
        for (String segment : pathSegments) {
            try {
                String encoded = URLEncoder.encode(segment, StandardCharsets.UTF_8);
                if (encoded.matches(IS_URL_VALID_REGEX) || segment.matches(IS_URL_VALID_REGEX)) {
                    return true;
                }
            } catch (Exception e) {
                return true;
            }
        }
        return false;
    }

    private static class ClientContext {
        final String clientId;
        final String role;
        final String token;

        ClientContext(String clientId, String role, String token) {
            this.clientId = clientId;
            this.role = role;
            this.token = token;
        }
    }
}
