package com.tag.biometric.ifService.client.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.tag.biometric.ifService.config.RoleConstant;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/if/clients/{clientId}/relations")
@Validated
public interface ClientRelationController {

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('"+ RoleConstant.BCASS_ADMIN +"','"+ RoleConstant.CLIENT_MANAGER +"')")
    Mono<ResponseEntity<String>> createClientRelation(@RequestBody JsonNode jsonNode,
                                                      @PathVariable("clientId") String clientId);

    @PatchMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('"+RoleConstant.BCASS_ADMIN +"','"+ RoleConstant.CLIENT_MANAGER +"')")
    Mono<ResponseEntity<String>> updateClientRelation(@RequestBody JsonNode jsonNode,
                                                      @PathVariable("clientId") String clientId,
                                                      @PathVariable("id") @NotNull String id);

    @DeleteMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('"+RoleConstant.BCASS_ADMIN +"','"+ RoleConstant.CLIENT_MANAGER +"')")
    Mono<ResponseEntity<String>> deleteClientRelation(@PathVariable("clientId") String clientId,
                                                      @PathVariable("id") @NotNull String id);

    @GetMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('"+RoleConstant.BCASS_ADMIN +"','"+ RoleConstant.CLIENT_MANAGER +"')")
    Mono<ResponseEntity<String>> getClientRelation(@PathVariable("clientId") String clientId,
                                                   @PathVariable("id") @NotNull String id
    );

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('"+RoleConstant.BCASS_ADMIN +"','"+ RoleConstant.CLIENT_MANAGER +"')")
    Mono<ResponseEntity<String>> listClientRelations(@PathVariable("clientId") String clientId,
                                                     @RequestParam(name = "metadata.page.number", required = false) Integer page,
                                                     @RequestParam(name = "metadata.page.size", required = false) Integer pageSize,
                                                     @RequestParam(name = "metadata.sort", required = false) String sortBy,
                                                     @RequestParam(name = "data.clientRelation.identity.type", required = false) String identityType,
                                                     @RequestParam(name = "data.clientRelation.relation", required = false) String relation
                                                     );
}
