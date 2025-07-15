/*
 * Tag Biometric (c) 2022. Tag Biometric
 * All rights reserved with Tag Biometrics.
 */

package com.tag.biometric.ifService.config.exception;

/**
 * AccountNotFoundException.java
 *
 * @author: Accubits
 * Created On: 06-09-2022
 */
public class JsonProcessingException extends RuntimeException {
    public JsonProcessingException() {
        super("Something went wrong while conversation with object to json.");
    }
}
