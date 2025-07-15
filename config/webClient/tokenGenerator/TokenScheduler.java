package com.tag.biometric.ifService.config.webClient.tokenGenerator;

import com.fasterxml.jackson.databind.JsonNode;
import com.tag.biometric.ifService.config.IfConstants;
import com.tag.biometric.ifService.institutionOnboarding.dto.Processor;
import com.tag.biometric.ifService.institutionOnboarding.repository.ProcessorRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.security.PrivateKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * Copyrights (c) 2022. Tag Biometric
 * All rights reserved with Tag Biometrics.
 *
 * @author Farsin Siddik
 * @date 02-05-2025
 */

@Component
public class TokenScheduler {

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);
    private final Map<String, String> clientTokenMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
//    @Autowired
//    private JwtGenerator jwtGenerator;
    @Autowired
    private IfConstants ifConstants;
    @Autowired
    private WebClient.Builder webClientBuilder;
    @Autowired
    private ProcessorRepository processorRepository;
    @Autowired
    private RedisTokenService redisTokenService;

    @PostConstruct
    public void initialize() {
        processorRepository.findAllIfProcessor()
                .flatMap(this::generateBearerToken) // schedules token for each client
                .subscribe(); // trigger the stream
    }

//    private void scheduleTokenRefresh(IbanClients client) {
//        TokenResponse tokenResponse = generateBearerToken(client.getInstanceClientId()); // Your API call
//        Instant expiryTime = Instant.now().plusSeconds(tokenResponse.getExpiresIn() - 30);
//        clientTokenMap.put(client.getId(), new TokenHolder(tokenResponse.getAccessToken(), expiryTime));
//
//        long delay = Duration.between(Instant.now(), expiryTime).toMillis();
//
//        executor.schedule(() -> scheduleTokenRefresh(client), delay, TimeUnit.MILLISECONDS);
//    }

//    public String getToken(String clientId) {
//        return clientTokenMap.get(clientId).getToken();
//    }

    public Mono<Void> generateBearerToken(Processor processor) {
        return generateJwtWithClientId(processor.getInstanceClientId())
                .flatMap(jwt -> {
                    WebClient webClient = webClientBuilder.build();
                    System.out.println("Before return line-------------- ");
                    return webClient.post()
                            .uri("https://sandbox-account.integrated.finance/auth/realms/ifp/protocol/openid-connect/token")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .body(BodyInserters.fromFormData("grant_type", "client_credentials")
                                    .with("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer")
                                    .with("client_assertion", jwt))
                            .retrieve()
                            .bodyToMono(JsonNode.class)
                            .doOnNext(jsonNode -> {
                                Long processorId = processor.getId();
                                redisTokenService.storeToken(processorId, jsonNode.get("access_token").asText());
                                redisTokenService.storeInstanceUrl(processorId, processor.getInstanceUrl());
                                System.out.println("Token stored to redis-------------- ");
                                long delay = Duration.between(Instant.now(), Instant.now().plusSeconds(jsonNode.get("expires_in").asLong() - 30)).toMillis();

                                ScheduledFuture<?> future = executor.schedule(() -> generateBearerToken(processor).subscribe(), delay, TimeUnit.MILLISECONDS);
                                scheduledTasks.put(processorId.toString(), future);
                                System.out.println("scheduler added-------------- ");
                            })
//                            .doOnNext(jsonNode -> cacheToken(jsonNode.get("access_token").asText())) //  Store in cache
                            .then(); // Return Mono<Void> to indicate completion
                });
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

    public void disableProcessor(Long processorId) {
        // Cancel the scheduled task
        ScheduledFuture<?> future = scheduledTasks.remove(String.valueOf(processorId));
        if (future != null) {
            future.cancel(false); // Cancel without interrupting if running
            System.out.println("Scheduled task cancelled for client: " + processorId);
        }

        // Remove token and instance URL from Redis
        redisTokenService.removeToken(String.valueOf(processorId));
        redisTokenService.removeInstanceUrl(String.valueOf(processorId));
        System.out.println("Token and instance URL removed from Redis for processor: " + processorId);
    }
}
