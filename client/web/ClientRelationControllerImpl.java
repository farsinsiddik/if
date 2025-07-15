package com.tag.biometric.ifService.client.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.tag.biometric.ifService.client.service.ClientRelationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
public class ClientRelationControllerImpl implements ClientRelationController {

    private ClientRelationService clientRelationService;

    @Override
    public Mono<ResponseEntity<String>> createClientRelation(JsonNode jsonNode, String clientId) {
        return clientRelationService.createClientRelation(jsonNode, clientId);
    }

    @Override
    public Mono<ResponseEntity<String>> updateClientRelation(JsonNode jsonNode, String clientId, String id) {
        return clientRelationService.updateClientRelation(jsonNode, clientId, id);
    }

    @Override
    public Mono<ResponseEntity<String>> deleteClientRelation(String clientId, String id) {
        return clientRelationService.deleteClientRelation(clientId, id);
    }

    @Override
    public Mono<ResponseEntity<String>> getClientRelation(String clientId, String id) {
        return clientRelationService.getClientRelations(clientId, id);
    }

    @Override
    public Mono<ResponseEntity<String>> listClientRelations(String clientId, Integer page, Integer pageSize, String sortBy, String identityType, String relation) {
        return clientRelationService.listClientRelations(clientId, page, pageSize, sortBy, identityType, relation);
    }
}
