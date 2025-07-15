package com.tag.biometric.ifService.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Slf4j
public class MaskApiReqResLog {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Add your sensitive keys here
    private static final List<String> SENSITIVE_FIELDS = List.of(
            "password", "token", "secret", "authorization", "ssn", "creditCardNumber"
    );

    public static String maskSensitiveData(String json, String endpoint, String logId) {
        try {
            JsonNode root = objectMapper.readTree(json);
            maskSensitiveFieldsRecursively(root);
            return objectMapper.writeValueAsString(root);
        } catch (Exception e) {
            log.error("Error while masking sensitive data - LogId: {}, Endpoint: {}, ExceptionMessage: {}",
                    logId, endpoint, e.getMessage());
            return json; // Return original if something goes wrong
        }
    }

    private static void maskSensitiveFieldsRecursively(JsonNode node) {
        if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node;
            Iterator<Map.Entry<String, JsonNode>> fields = objectNode.fields();

            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String fieldName = entry.getKey();
                JsonNode value = entry.getValue();

                if (isSensitiveField(fieldName)) {
                    objectNode.put(fieldName, "****");
                } else {
                    maskSensitiveFieldsRecursively(value);
                }
            }
        } else if (node.isArray()) {
            for (JsonNode item : node) {
                maskSensitiveFieldsRecursively(item);
            }
        }
    }

    private static boolean isSensitiveField(String fieldName) {
        return SENSITIVE_FIELDS.contains(fieldName.toLowerCase());
    }

}
