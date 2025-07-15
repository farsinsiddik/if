package com.tag.biometric.ifService.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {

    public static ResponseEntity<String> finalizeResponse(String responseBody) {
        ObjectNode wrapper = new ObjectMapper().createObjectNode();
        try {
            JsonNode parsedResponse = new ObjectMapper().readTree(responseBody);
            wrapper.set("ifData", parsedResponse);
        } catch (Exception e) {
            wrapper.put("ifData", responseBody);
        }
        return ResponseEntity.ok(wrapper.toString());
    }
}
