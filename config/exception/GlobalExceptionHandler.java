package com.tag.biometric.ifService.config.exception;

import com.tag.biometric.ifService.others.exceptions.IfGenericException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.UnsupportedMediaTypeException;
import org.springframework.web.server.MethodNotAllowedException;
import reactor.core.publisher.Mono;

import static com.tag.biometric.ifService.config.IfConstants.BCAAS;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ClientRequestException.class)
    public Mono<ResponseEntity<String>> handleClientRequestException(ClientRequestException ex) {
        return Mono.just(ResponseEntity.status(ex.getStatus())
                        .body(new IfGenericException(ex.getStatus(), ex.getMessage(), BCAAS).toString()));
    }

    @ExceptionHandler(MethodNotAllowedException.class)
    public Mono<ResponseEntity<String>> handleMethodNotAllowedException(MethodNotAllowedException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new IfGenericException(
                        HttpStatus.METHOD_NOT_ALLOWED,
                        "Method not allowed: " + ex.getHttpMethod(),
                        BCAAS).toString()));
    }

    @ExceptionHandler(UnsupportedMediaTypeException.class)
    public Mono<ResponseEntity<String>> handleMediaTypeNotSupported(UnsupportedMediaTypeException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(new IfGenericException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                        String.format("Media type '%s' is not supported. Supported media types: %s",
                                ex.getContentType(), ex.getSupportedMediaTypes()),
                        BCAAS).toString()));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<String>> handleGenericException(Exception ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new IfGenericException(HttpStatus.BAD_REQUEST,
                                ex.getMessage(),
                                BCAAS).toString()));
    }
}