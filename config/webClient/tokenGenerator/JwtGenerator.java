package com.tag.biometric.ifService.config.webClient.tokenGenerator;

import com.fasterxml.jackson.databind.JsonNode;
import com.tag.biometric.ifService.config.IfConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.security.PrivateKey;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

/**
 * Copyrights (c) 2022. Tag Biometric
 * All rights reserved with Tag Biometrics.
 *
 * @author Farsin Siddik
 * @date 26-03-2025
 */

@Component
public class JwtGenerator {

    @Autowired
    private IfConstants ifConstants;
    @Autowired
    private WebClient.Builder webClientBuilder;
    private volatile String bearerToken; // Store latest Bearer Token

//    @PostConstruct
//    public void init() {
//        generateBearerToken().subscribe(); // Fetch token on startup reactively
//    }

//    @Scheduled(fixedRate = 180000) // Refresh token every 3 minutes
//    public void scheduleTokenRefresh() {
//        generateBearerToken().subscribe();
//    }

    public Mono<Void> generateBearerToken() {
        return generateJwt()
                .flatMap(jwt -> {
                    WebClient webClient = webClientBuilder.build();
                    return webClient.post()
                            .uri("https://sandbox-account.integrated.finance/auth/realms/ifp/protocol/openid-connect/token")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .body(BodyInserters.fromFormData("grant_type", "client_credentials")
                                    .with("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer")
                                    .with("client_assertion", jwt))
                            .retrieve()
                            .bodyToMono(JsonNode.class)
                            .doOnNext(jsonNode -> {
                                bearerToken = jsonNode.get("access_token").asText();
//                                System.out.println("New Bearer Token: " + bearerToken);
                            })
//                            .doOnNext(jsonNode -> cacheToken(jsonNode.get("access_token").asText())) //  Store in cache
                            .then(); // Return Mono<Void> to indicate completion
                });
    }

//    @Cacheable(value = "sandboxToken", unless = "#result == null") //  Fetch token from cache
//    public String getCachedToken() {
//        System.out.println("🔍 Fetching token from cache...");
//        return bearerToken;
//    }
//
//    @CachePut(value = "sandboxToken") //  Update token in cache
//    public void cacheToken(String token) {
//        this.bearerToken = token;
//        System.out.println(" Updating token in cache: " + token);
//    }

//    public String getBearerToken() {
//        return Optional.ofNullable(getCachedToken()).orElse(bearerToken);
//    }

    public String getBearerToken() {
        return bearerToken;
    }

    private Mono<String> generateJwt() {
        return Mono.fromCallable(() -> {
            // Load private key
            PrivateKey privateKey = KeyLoader.loadPrivateKey(ifConstants.getIF_PRIVATE_KEY_LOCATION());

            // Define claims
            String clientId = ifConstants.getIF_CLIENT_ID();
            String audience = "https://sandbox-account.integrated.finance/auth/realms/ifp";
            long now = System.currentTimeMillis();

            return Jwts.builder()
                    .setId(UUID.randomUUID().toString())
                    .setIssuer(clientId)
                    .setSubject(clientId)
                    .setAudience(audience)
                    .setIssuedAt(new Date(now))
                    .setExpiration(new Date(now + 600_000))
                    .signWith(privateKey, SignatureAlgorithm.RS256)
                    .compact();
        }).subscribeOn(Schedulers.boundedElastic()); // Offload to separate thread
    }

    private Mono<String> generateJwtWithClientId(String clientId) {
        return Mono.fromCallable(() -> {
            // Load private key
            PrivateKey privateKey = KeyLoader.loadPrivateKey(ifConstants.getIF_PRIVATE_KEY_LOCATION());

            String audience = "https://sandbox-account.integrated.finance/auth/realms/ifp";
            long now = System.currentTimeMillis();

            return Jwts.builder()
                    .setId(UUID.randomUUID().toString())
                    .setIssuer(clientId)
                    .setSubject(clientId)
                    .setAudience(audience)
                    .setIssuedAt(new Date(now))
                    .setExpiration(new Date(now + 600_000))
                    .signWith(privateKey, SignatureAlgorithm.RS256)
                    .compact();
        }).subscribeOn(Schedulers.boundedElastic()); // Offload to separate thread
    }
}
