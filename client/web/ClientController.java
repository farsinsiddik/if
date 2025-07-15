package com.tag.biometric.ifService.client.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.tag.biometric.ifService.config.RoleConstant;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/if/clients")
@Validated
public interface ClientController {

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('"+RoleConstant.BCASS_ADMIN +"','"+ RoleConstant.CLIENT_MANAGER +"')")
    Mono<ResponseEntity<String>> createClient(@RequestBody JsonNode jsonNode);

    @PatchMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('"+RoleConstant.BCASS_ADMIN +"','"+ RoleConstant.CLIENT_MANAGER +"')")
    Mono<ResponseEntity<String>> updateClient(@PathVariable("id") @NotNull String id, @RequestBody JsonNode jsonNode);

    @GetMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('"+RoleConstant.BCASS_ADMIN +"','"+ RoleConstant.CLIENT_MANAGER +"')")
    Mono<ResponseEntity<String>> getClient(
            @PathVariable("id") @NotNull String id,
            @RequestParam(name = "metadata.include", required = false) String metadataInclude
    );

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('"+RoleConstant.BCASS_ADMIN +"','"+ RoleConstant.CLIENT_MANAGER +"')")
    Mono<ResponseEntity<String>> listClients(
            @RequestParam(name = "metadata.page.number", required = false) Integer page,
            @RequestParam(name = "metadata.page.size", required = false) Integer pageSize,
            @RequestParam(name = "metadata.sort", required = false) String sortBy,
            @RequestParam(name = "data.client.identity.type", required = false) String identityType,
            @RequestParam(name = "data.client.fullName", required = false) String fullName,
            @RequestParam(name = "data.client.status", required = false) String status,
            @RequestParam(name = "data.client.clientNumber", required = false) String clientNumber
    );
}
