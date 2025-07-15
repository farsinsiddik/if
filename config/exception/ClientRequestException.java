package com.tag.biometric.ifService.config.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ClientRequestException extends RuntimeException {
    private final HttpStatus status;

    public ClientRequestException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

}
