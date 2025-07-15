package com.tag.biometric.ifService.client.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface ClientRelationService {

    Mono<ResponseEntity<String>> createClientRelation(JsonNode jsonNode, String clientId);

    Mono<ResponseEntity<String>> updateClientRelation(JsonNode jsonNode, String clientId, String clientRelationId);

    Mono<ResponseEntity<String>> deleteClientRelation(String clientId, String clientRelationId);

    Mono<ResponseEntity<String>> getClientRelations(String clientId, String clientRelationId);

    Mono<ResponseEntity<String>> listClientRelations(String clientId, Integer page, Integer pageSize, String sortBy, String identityType, String relation);
}
