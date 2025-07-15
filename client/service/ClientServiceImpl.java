package com.tag.biometric.ifService.client.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.tag.biometric.ifService.config.webClient.WebClientManager;
import com.tag.biometric.ifService.util.ResponseUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Optional;

import static com.tag.biometric.ifService.others.constants.ClientUrlConstants.CREATE_CLIENT;
import static com.tag.biometric.ifService.util.Utils.addConnectAndMetaData;

/**
 * Copyrights (c) 2022. Tag Biometric
 * All rights reserved with Tag Biometrics.
 *
 * @author Farsin Siddik
 * @date 14-03-2025
 */

@Service
@AllArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final WebClientManager webClientManager;

    @Override
    public Mono<ResponseEntity<String>> createClient(JsonNode jsonNode) {
        JsonNode ifData = jsonNode.get("ifData"); // Extract only ifData

        if (ifData == null || ifData.isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body("ifData is missing"));
        }
        return Mono.deferContextual(ctx -> {
            if (!ctx.hasKey("clientId")) {
                return Mono.error(new IllegalStateException("Missing clientId in context"));
            }

            Long clientId = Long.valueOf(ctx.get("clientId"));

            return webClientManager.prepareRequest(clientId)
                    .flatMap(webClient -> webClient.post()
                            .uri(CREATE_CLIENT)
                            .bodyValue(addConnectAndMetaData(ifData))
                            .retrieve()
                            .bodyToMono(String.class)
                            .map(response -> ResponseEntity.created(URI.create(CREATE_CLIENT)).body(ResponseUtil.finalizeResponse(response).getBody()))
                            .onErrorResume(WebClientResponseException.class, WebClientManager::checkException));
        });
    }

    @Override
    public Mono<ResponseEntity<String>> updateClient(String id, JsonNode jsonNode) {
        JsonNode ifData = jsonNode.get("ifData"); // Extract only ifData

        if (ifData == null || ifData.isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body("ifData is missing"));
        }

        return Mono.deferContextual(ctx -> {
            if (!ctx.hasKey("clientId")) {
                return Mono.error(new IllegalStateException("Missing clientId in context"));
            }

            Long clientId = Long.valueOf(ctx.get("clientId"));

            return webClientManager.prepareRequest(clientId)
                    .flatMap(webClient -> webClient.patch()
                            .uri(uriBuilder -> uriBuilder
                                    .path(CREATE_CLIENT + "/{id}")
                                    .build(id))
                            .bodyValue(addConnectAndMetaData(ifData))
                            .retrieve()
                            .bodyToMono(String.class)
                            .map(response -> ResponseEntity.accepted().body(ResponseUtil.finalizeResponse(response).getBody()))
                            .onErrorResume(WebClientResponseException.class, WebClientManager::checkException));
        });

    }

    @Override
    public Mono<ResponseEntity<String>> listClients(Integer page, Integer pageSize, String sortBy, String identityType, String fullName, String status, String clientNumber) {

        return Mono.deferContextual(ctx -> {
            if (!ctx.hasKey("clientId")) {
                return Mono.error(new IllegalStateException("Missing clientId in context"));
            }

            Long clientId = Long.valueOf(ctx.get("clientId"));

            return webClientManager.prepareRequest(clientId)
                    .flatMap(webClient ->
                            webClient.get()
                                    .uri(uriBuilder -> {
                                        uriBuilder.path(CREATE_CLIENT);

                                        Optional.ofNullable(page).ifPresent(p -> uriBuilder.queryParam("metadata.page.number", p));
                                        Optional.ofNullable(pageSize).ifPresent(ps -> uriBuilder.queryParam("metadata.page.size", ps));
                                        Optional.ofNullable(sortBy).ifPresent(s -> uriBuilder.queryParam("metadata.sort", s));
                                        Optional.ofNullable(identityType).ifPresent(it -> uriBuilder.queryParam("data.client.identity.type", it));
                                        Optional.ofNullable(fullName).ifPresent(fn -> uriBuilder.queryParam("data.client.fullName", fn));
                                        Optional.ofNullable(status).ifPresent(st -> uriBuilder.queryParam("data.client.status", st));
                                        Optional.ofNullable(clientNumber).ifPresent(cn -> uriBuilder.queryParam("data.client.clientNumber", cn));

                                        return uriBuilder.build();
                                    })
                                    .retrieve()
                                    .bodyToMono(String.class)
                                    .map(ResponseUtil::finalizeResponse)
                                    .onErrorResume(WebClientResponseException.class, WebClientManager::checkException)
                    );
        });
    }


    @Override
    public Mono<ResponseEntity<String>> getClient(String id, String metadataInclude) {

        return Mono.deferContextual(ctx -> {
            if (!ctx.hasKey("clientId")) {
                return Mono.error(new IllegalStateException("Missing clientId in context"));
            }

            Long clientId = Long.valueOf(ctx.get("clientId"));

            return webClientManager.prepareRequest(clientId)
                    .flatMap(webClient -> webClient.get()
                            .uri(uriBuilder -> uriBuilder
                                    .path(CREATE_CLIENT + "/{id}")
                                    .queryParamIfPresent("metadata.include", Optional.ofNullable(metadataInclude))
                                    .build(id))
                            .retrieve()
                            .bodyToMono(String.class)
                            .map(ResponseUtil::finalizeResponse)
                            .onErrorResume(WebClientResponseException.class, WebClientManager::checkException));
        });
    }

}
