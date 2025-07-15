package com.tag.biometric.ifService.config.exception;

public class InvalidFieldDataException extends RuntimeException {

    public InvalidFieldDataException(String message) {
        super(String.format("%s field data is either empty or invalid please check.", message));
    }

    public InvalidFieldDataException(String errorCode, String message) {
        super(String.format("Error:%s - %s field data cannot be modified",errorCode, message));
    }

    public InvalidFieldDataException() {
        super("Invalid data for field");
    }
}
