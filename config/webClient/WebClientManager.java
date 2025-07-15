package com.tag.biometric.ifService.config.webClient;

import com.google.gson.Gson;
import com.tag.biometric.ifService.config.exception.ClientRequestException;
import com.tag.biometric.ifService.config.webClient.tokenGenerator.RedisTokenService;
import com.tag.biometric.ifService.others.exceptions.IfGenericException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Objects;

import static com.tag.biometric.ifService.others.constants.ProcessorErrorConstants.INTEGRATED_FINANCE;

/**
 * Copyrights (c) 2022. Tag Biometric
 * All rights reserved with Tag Biometrics.
 *
 * @author Farsin Siddik
 * @date 07-05-2025
 */

@Component
@Slf4j
public class WebClientManager {

    private final WebClient webClient;
    private final RedisTokenService redisTokenService;

    @Autowired
    public WebClientManager(WebClient.Builder builder,
                            RedisTokenService redisTokenService) {
        this.webClient = builder
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build(); // singleton WebClient
        this.redisTokenService = redisTokenService;
    }

    public Mono<WebClient> prepareRequest(Long clientId) {
        return Mono.zip(
                redisTokenService.getToken(clientId),
                redisTokenService.getInstanceUrl(clientId)
        ).flatMap(tuple -> {
            String token = tuple.getT1();
            String url = tuple.getT2();

            if (token == null || token.isBlank() || url == null || url.isBlank()) {
                return Mono.error(new ClientRequestException(HttpStatus.BAD_REQUEST,
                        "ClientId associated with the user is invalid, please contact administrator"));
            }

            WebClient preparedClient = webClient
                    .mutate()
                    .baseUrl(url)
                    .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .build();

            return Mono.just(preparedClient);
        }).onErrorResume(RedisConnectionFailureException.class, e -> {
            log.error("WebClientManager :: prepareRequest : {}", e.getMessage());
            return Mono.error(new ClientRequestException(HttpStatus.BAD_REQUEST,
                    "Server busy, please try again later or contact administrator"));
        }).onErrorResume(Exception.class, e -> {
            log.error("WebClientManager :: prepareRequest : {}", e.getMessage());
            return Mono.error(new ClientRequestException(HttpStatus.BAD_REQUEST,
                    "ClientId associated with the user is invalid, please contact administrator"));
        });
    }


    /*public WebClient prepareRequest(Long clientId) {

        try {
            Mono<String> clientToken = redisTokenService.getToken(clientId);
            if (clientToken == null || clientToken.block().isBlank()) {
                throw new ClientRequestException(HttpStatus.BAD_REQUEST,
                        "ClientId associated with the user is invalid, please contact administrator");
            }

            Mono<String> clientUrl = redisTokenService.getInstanceUrl(clientId);
            if (clientUrl == null || clientUrl.block().isBlank()) {
                throw new ClientRequestException(HttpStatus.BAD_REQUEST,
                        "ClientId associated with the user is invalid, please contact administrator");
            }

            return webClient
                    .mutate()
                    .baseUrl(clientUrl)
                    .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + clientToken)
                    .build();
        } catch (RedisConnectionFailureException e) {
            log.error("WebClientManager :: prepareRequest : {}",e.getMessage());
            throw new ClientRequestException(HttpStatus.BAD_REQUEST, "Server busy, please try again later or contact administrator");
        } catch (Exception e) {
            log.error("WebClientManager :: prepareRequest : {}",e.getMessage());
            throw new ClientRequestException(HttpStatus.BAD_REQUEST,
                    "ClientId associated with the user is invalid, please contact administrator");
        }

    }*/

    public static Mono<ResponseEntity<String>> checkException(WebClientResponseException exception) {
        HashMap<String, String> errorMap = new Gson().fromJson(exception.getResponseBodyAsString(), HashMap.class);
        String errorMessage = Objects.nonNull(errorMap) && errorMap.containsKey("message") ? errorMap.get("message") : exception.getMessage();
        HttpStatusCode statusCode = exception.getStatusCode();
        return Mono.just(ResponseEntity.status(statusCode).body(new IfGenericException((HttpStatus) statusCode, errorMessage, INTEGRATED_FINANCE).toString()));
    }
}
