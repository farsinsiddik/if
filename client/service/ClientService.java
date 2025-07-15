package com.tag.biometric.ifService.client.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

/**
 * Copyrights (c) 2022. Tag Biometric
 * All rights reserved with Tag Biometrics.
 *
 * @author midhu
 * @date 14-03-2025$
 */

public interface ClientService {

    Mono<ResponseEntity<String>> createClient(JsonNode jsonNode);

    Mono<ResponseEntity<String>> updateClient(String clientId, JsonNode jsonNode);

    Mono<ResponseEntity<String>> getClient(String clientId, String metadataInclude);

    Mono<ResponseEntity<String>> listClients(Integer page, Integer pageSize, String sortBy, String identityType, String fullName, String status, String clientNumber);
}
