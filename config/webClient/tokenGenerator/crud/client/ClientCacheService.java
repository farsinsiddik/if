package com.tag.biometric.ifService.config.webClient.tokenGenerator.crud.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tag.biometric.ifService.config.webClient.tokenGenerator.crud.centralized.RedisDataService;
import com.tag.biometric.ifService.config.webClient.tokenGenerator.crud.dto.Client;
import com.tag.biometric.ifService.config.webClient.tokenGenerator.crud.dto.CrudClient;
import com.tag.biometric.ifService.institutionOnboarding.repository.ClientRepository;
import com.tag.biometric.ifService.others.exceptions.IfGenericException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Copyrights (c) 2022. Tag Biometric
 * All rights reserved with Tag Biometrics.
 *
 * @author Farsin Siddik
 * @date 01-07-2025
 */

@Service
@RequiredArgsConstructor
public class ClientCacheService {
    private static final String CLIENT_KEY = "clients";

    private final RedisDataService redisDataService;
    private final ClientRepository clientRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    public Mono<CrudClient> getClientById(Long clientId) {
        // Try to get from cache first
        CrudClient cached = redisDataService.get(CLIENT_KEY, clientId.toString(), CrudClient.class);
        if (cached != null) {
            return Mono.just(cached);
        }

        // Fallback to DB (reactive)
        return clientRepository.findById(clientId)
                .switchIfEmpty(Mono.error(new IfGenericException("Client not found with id " + clientId)))
                .map(this::mapToDto)
                .doOnNext(dto -> redisDataService.put(CLIENT_KEY, clientId.toString(), dto));
    }

    public void saveClient(Client client) {
        Client saved = clientRepository.save(client).block();
        CrudClient dto = mapToDto(saved);
        redisDataService.put(CLIENT_KEY, saved.getId().toString(), dto);
    }

    private CrudClient mapToDto(Client client) {
        try {
            return CrudClient.builder()
                    .id(client.getId())
                    .entityName(client.getEntityName())
                    .status(client.getStatus())
                    .secretKey(client.getSecretKey())
                    .processorIds(mapper.readValue(client.getProcessorIds(), new TypeReference<List<Integer>>() {}))
                    .build();

        } catch (Exception e) {
            throw new IfGenericException("Something went wrong, please try again after some time.");
        }
    }
}