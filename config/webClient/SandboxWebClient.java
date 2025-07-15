package com.tag.biometric.ifService.config.webClient;

import com.google.gson.Gson;
import com.tag.biometric.ifService.config.webClient.tokenGenerator.JwtGenerator;
import com.tag.biometric.ifService.others.exceptions.IfGenericException;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
//import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.Objects;

import static com.tag.biometric.ifService.others.constants.ProcessorErrorConstants.INTEGRATED_FINANCE;

/**
 * Copyrights (c) 2022. Tag Biometric
 * All rights reserved with Tag Biometrics.
 *
 * @author Farsin Siddik
 * @date 14-03-2025
 */

@Component
public class SandboxWebClient {

    @Value("${sandbox.url}")
    private String sandboxUrl;
    @Value("${sandbox.private_key_location}")
    private String sandboxPrivateKeyLocation;
    @Value("${sandbox.client_id}")
    private String sandboxClientId;
    private WebClient webClient;
    private boolean hasCredentials;
    @Autowired
    private JwtGenerator jwtGenerator;

    public WebClient getWebClient() {
        return WebClient.builder()
                .baseUrl(sandboxUrl)
                .defaultHeader("Authorization", "Bearer " + jwtGenerator.getBearerToken())
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

//    private final JwtGenerator jwtGenerator;
//
//    @Autowired
//    public SandboxWebClient(JwtGenerator jwtGenerator, @Value("${sandbox.url}") String sandboxUrl) {
//        this.jwtGenerator = jwtGenerator;
//
//        this.webClient = WebClient.builder()
//                .baseUrl(sandboxUrl)
//                .defaultHeader("Authorization", "Bearer " + jwtGenerator.getBearerToken())
//                .defaultHeader("Content-Type", "application/json")
//                .build();
//    }

//    @Autowired
//    private JwtGenerator jwtGenerator;
//
//    public SandboxWebClient() {
//        this.webClient = WebClient.builder()
//                .baseUrl(sandboxUrl)
//                .defaultHeader("Content-Type", "application/json")
////                .filter(this::authHeaderFilter)  // Attach dynamic token logic
//                .build();
//    }
//
//    private Mono<ClientResponse> authHeaderFilter(ClientRequest request, ExchangeFunction next) {
//        return Mono.defer(() -> {
//            String token = jwtGenerator.getBearerToken(); //  Get latest token at request time
//            ClientRequest newRequest = ClientRequest.from(request)
//                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
//                    .build();
//            return next.exchange(newRequest);
//        });
//    }


//    //  Expose WebClient for reusability
//    public WebClient getWebClient() {
//        return this.webClient;
//    }

    public boolean hasCredentials() {
        return !(sandboxPrivateKeyLocation == null || sandboxClientId == null);
    }

    public static Mono<ResponseEntity<String>> checkException(WebClientResponseException exception) {
        HashMap<String, String> errorMap = new Gson().fromJson(exception.getResponseBodyAsString(), HashMap.class);
        String errorMessage = Objects.nonNull(errorMap) && errorMap.containsKey("message") ? errorMap.get("message") : exception.getMessage();
        HttpStatusCode statusCode = exception.getStatusCode();
        return Mono.just(ResponseEntity.status(statusCode).body(new IfGenericException((HttpStatus) statusCode, errorMessage, INTEGRATED_FINANCE).toString()));
    }

}
