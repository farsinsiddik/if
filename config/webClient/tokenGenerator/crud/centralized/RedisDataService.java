package com.tag.biometric.ifService.config.webClient.tokenGenerator.crud.centralized;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Copyrights (c) 2022. Tag Biometric
 * All rights reserved with Tag Biometrics.
 *
 * @author Farsin Siddik
 * @date 01-07-2025
 */

@Service
@AllArgsConstructor
public class RedisDataService {

    private final RedisTemplate<String, Object> redisTemplate;

    // ---------- PUT ----------
    public <T> void put(String key, String hashKey, T value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    // ---------- GET ----------
    @SuppressWarnings("unchecked")
    public <T> T get(String key, String hashKey, Class<T> type) {
        Object value = redisTemplate.opsForHash().get(key, hashKey);
        return value != null ? (T) value : null;
    }

    // ---------- DELETE ----------
    public void delete(String key, String hashKey) {
        redisTemplate.opsForHash().delete(key, hashKey);
    }

    // ---------- GET ALL ----------
    @SuppressWarnings("unchecked")
    public <T> Map<String, T> getAll(String key, Class<T> type) {
        Map<Object, Object> rawMap = redisTemplate.opsForHash().entries(key);
        return rawMap.entrySet().stream()
                .filter(e -> e.getKey() instanceof String && type.isInstance(e.getValue()))
                .collect(Collectors.toMap(
                        e -> (String) e.getKey(),
                        e -> type.cast(e.getValue())
                ));
    }

    // ---------- EXISTS ----------
    public boolean containsKey(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    // ---------- Optional TTL (Expire whole group if needed) ----------
    public void setTTL(String key, Duration ttl) {
        redisTemplate.expire(key, ttl);
    }
}
