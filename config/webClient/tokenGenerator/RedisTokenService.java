package com.tag.biometric.ifService.config.webClient.tokenGenerator;

import com.tag.biometric.ifService.config.webClient.tokenGenerator.crud.client.ClientCacheService;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;

/**
 * Copyrights (c) 2022. Tag Biometric
 * All rights reserved with Tag Biometrics.
 *
 * @author Farsin Siddik
 * @date 05-05-2025
 */

@Service
@AllArgsConstructor
public class RedisTokenService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String TOKEN_KEY = "iban_processor_tokens";
    private static final String INSTANCE_URL_KEY = "iban_processor_url";
    private static final String CLIENT_PROCESSOR_MAP = "client_processor_map";

    private final ClientCacheService clientCRUDService;

    public void storeToken(Long processorId, String token) {
        redisTemplate.opsForHash().put(TOKEN_KEY, processorId.toString(), token);
    }

    public void storeInstanceUrl(Long clientId, String instanceUrl) {
        redisTemplate.opsForHash().put(INSTANCE_URL_KEY, clientId.toString(), instanceUrl);
    }

    public Mono<String> getToken(Long clientId) {
        return clientCRUDService.getClientById(clientId) // returns Mono<CrudClient>
                .flatMap(client -> client.getProcessorIds().stream()
                        .map(processorId -> redisTemplate.opsForHash().get(TOKEN_KEY, processorId.toString()))
                        .filter(Objects::nonNull)
                        .map(Object::toString)
                        .findFirst() // returns Optional<String>
                        .map(Mono::just) // Optional<String> -> Mono<String>
                        .orElse(Mono.empty()));
    }

    public Mono<String> getInstanceUrl(Long clientId) {
        return clientCRUDService.getClientById(clientId) // returns Mono<CrudClient>
                .flatMap(client -> client.getProcessorIds().stream()
                        .map(processorId -> redisTemplate.opsForHash().get(INSTANCE_URL_KEY, processorId.toString()))
                        .filter(Objects::nonNull)
                        .map(Object::toString)
                        .findFirst() // returns Optional<String>
                        .map(Mono::just) // Optional<String> -> Mono<String>
                        .orElse(Mono.empty()));
    }

    public void removeToken(String clientId) {
        redisTemplate.opsForHash().delete(TOKEN_KEY, clientId);
    }

    public Map<Object, Object> getAllTokens() {
        return redisTemplate.opsForHash().entries(TOKEN_KEY);
    }

    public void removeInstanceUrl(String clientId) {
        redisTemplate.opsForHash().delete(INSTANCE_URL_KEY, clientId);
    }
}
