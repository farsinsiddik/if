/*
 * Tag Biometric (c) 2022. Tag Biometric
 * All rights reserved with Tag Biometrics.
 */

package com.tag.biometric.ifService.config.exception;

/**
 * AddressNotFoundException.java
 *
 * @author: Dell
 * Created On: 25-08-2022
 */
public class StatusNotFoundException extends RuntimeException {
    public StatusNotFoundException() {
        super("Unable to find status in system. Please check if you have correct access.");
    }

    public StatusNotFoundException(String status) {
        super(String.format("Unable to find %s status in system. Please check if you have correct access.", status));
    }

    public StatusNotFoundException(String status, String field) {
        super(String.format("Unknown %s status found in %s.", status, field));
    }
}
