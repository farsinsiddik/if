package com.tag.biometric.ifService.client.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.tag.biometric.ifService.client.service.ClientService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Copyrights (c) 2022. Tag Biometric
 * All rights reserved with Tag Biometrics.
 *
 * @author Farsin Siddik
 * @date 14-03-2025
 */

@RestController
@AllArgsConstructor
public class ClientControllerImpl implements ClientController {

    private ClientService clientService;

    @Override
    public Mono<ResponseEntity<String>> createClient(JsonNode jsonNode) {
        return clientService.createClient(jsonNode);
    }

    @Override
    public Mono<ResponseEntity<String>> updateClient(String clientId, JsonNode jsonNode) {
        return clientService.updateClient(clientId, jsonNode);
    }

    @Override
    public Mono<ResponseEntity<String>> getClient(String clientId, String metadataInclude) {
        return clientService.getClient(clientId, metadataInclude);
    }

    @Override
    public Mono<ResponseEntity<String>> listClients(Integer page, Integer pageSize, String sortBy, String identityType, String fullName, String status, String clientNumber) {
        return clientService.listClients(page, pageSize, sortBy, identityType, fullName, status, clientNumber);
    }
}
