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

import static com.tag.biometric.ifService.others.constants.ClientUrlConstants.CLIENT_RELATION;
import static com.tag.biometric.ifService.util.Utils.addConnectAndMetaData;

@Service
@AllArgsConstructor
public class ClientRelationServiceImpl implements ClientRelationService {

    private final WebClientManager webClientManager;

    @Override
    public Mono<ResponseEntity<String>> createClientRelation(JsonNode jsonNode, String clientId) {
        JsonNode ifData = jsonNode.get("ifData"); // Extract only ifData

        if (ifData == null || ifData.isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body("ifData is missing"));
        }

        return Mono.deferContextual(ctx -> {
            if (!ctx.hasKey("clientId")) {
                return Mono.error(new IllegalStateException("Missing clientId in context"));
            }

            Long tokenClientId = Long.valueOf(ctx.get("clientId"));

            return webClientManager.prepareRequest(tokenClientId)
                    .flatMap(webClient -> webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path(CLIENT_RELATION)
                            .build(clientId))
                    .bodyValue(addConnectAndMetaData(ifData))
                    .retrieve()
                    .bodyToMono(String.class)
                    .map(response -> ResponseEntity.created(URI.create(CLIENT_RELATION.replace("{clientId}", clientId)))
                            .body(ResponseUtil.finalizeResponse(response).getBody()))
                    .onErrorResume(WebClientResponseException.class, WebClientManager::checkException));
        });

    }

    @Override
    public Mono<ResponseEntity<String>> updateClientRelation(JsonNode jsonNode, String clientId, String clientRelationId) {
        JsonNode ifData = jsonNode.get("ifData"); // Extract only ifData

        if (ifData == null || ifData.isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body("ifData is missing"));
        }

        return Mono.deferContextual(ctx -> {
            if (!ctx.hasKey("clientId")) {
                return Mono.error(new IllegalStateException("Missing clientId in context"));
            }

            Long tokenClientId = Long.valueOf(ctx.get("clientId"));

            return webClientManager.prepareRequest(tokenClientId)
                    .flatMap(webClient -> webClient.patch()
                            .uri(uriBuilder -> uriBuilder
                                    .path(CLIENT_RELATION + "/{clientRelationId}") // URL : /client/{clientId
                                    // }/clientRelation/{id}
                                    .build(clientId, clientRelationId))
                            .bodyValue(addConnectAndMetaData(ifData))
                            .retrieve()
                            .bodyToMono(String.class)
                            .map(response -> ResponseEntity.accepted().body(ResponseUtil.finalizeResponse(response).getBody()))
                            .onErrorResume(WebClientResponseException.class, WebClientManager::checkException));
        });

    }

    @Override
    public Mono<ResponseEntity<String>> deleteClientRelation(String clientId, String clientRelationId) {
        return Mono.deferContextual(ctx -> {
            if (!ctx.hasKey("clientId")) {
                return Mono.error(new IllegalStateException("Missing clientId in context"));
            }

            Long tokenClientId = Long.valueOf(ctx.get("clientId"));

            return webClientManager.prepareRequest(tokenClientId)
                    .flatMap(webClient -> webClient.delete()
                    .uri(uriBuilder -> uriBuilder
                            .path(CLIENT_RELATION + "/{clientRelationId}") // URL : /client/{clientId}/clientRelation/{id}
                            .build(clientId, clientRelationId))
                    .retrieve()
                    .bodyToMono(String.class)
                    .map(response -> ResponseEntity.accepted().body(ResponseUtil.finalizeResponse(response).getBody()))
                    .onErrorResume(WebClientResponseException.class, WebClientManager::checkException));
        });

    }

    @Override
    public Mono<ResponseEntity<String>> getClientRelations(String clientId, String clientRelationId) {
        return Mono.deferContextual(ctx -> {
            if (!ctx.hasKey("clientId")) {
                return Mono.error(new IllegalStateException("Missing clientId in context"));
            }

            Long tokenClientId = Long.valueOf(ctx.get("clientId"));

            return webClientManager.prepareRequest(tokenClientId)
                    .flatMap(webClient -> webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(CLIENT_RELATION + "/{clientRelationId}")
                            .build(clientId, clientRelationId))
                    .retrieve()
                    .bodyToMono(String.class)
                    .map(ResponseUtil::finalizeResponse)
                    .onErrorResume(WebClientResponseException.class, WebClientManager::checkException));
        });

    }

    @Override
    public Mono<ResponseEntity<String>> listClientRelations(String clientId, Integer page, Integer pageSize, String sortBy, String identityType, String relation) {
        return Mono.deferContextual(ctx -> {
            if (!ctx.hasKey("clientId")) {
                return Mono.error(new IllegalStateException("Missing clientId in context"));
            }

            Long tokenClientId = Long.valueOf(ctx.get("clientId"));

            return webClientManager.prepareRequest(tokenClientId)
                    .flatMap(webClient -> webClient.get()
                    .uri(uriBuilder -> {
                        uriBuilder.path(CLIENT_RELATION);

                        Optional.ofNullable(page).ifPresent(p -> uriBuilder.queryParam("metadata.page.number", p));
                        Optional.ofNullable(pageSize).ifPresent(ps -> uriBuilder.queryParam("metadata.page.size", ps));
                        Optional.ofNullable(sortBy).ifPresent(s -> uriBuilder.queryParam("metadata.sort", s));
                        Optional.ofNullable(identityType).ifPresent(it -> uriBuilder.queryParam("data.clientRelation.identity.type", it));
                        Optional.ofNullable(relation).ifPresent(fn -> uriBuilder.queryParam("data.clientRelation.relation", fn));

                        return uriBuilder.build(clientId);
                    })
                    .retrieve()
                    .bodyToMono(String.class)
                    .map(ResponseUtil::finalizeResponse)
                    .onErrorResume(WebClientResponseException.class, WebClientManager::checkException));
        });

    }
}
