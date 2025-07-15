package com.tag.biometric.ifService.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class RestTemplateResponseWrapper extends DefaultResponseErrorHandler {
    private final byte[] bodyBytes;
    private HttpHeaders headers;
    private HttpStatusCode status;

    public RestTemplateResponseWrapper(ClientHttpResponse response) throws IOException {
        this.status = response.getStatusCode();
        this.headers = response.getHeaders();

        // Read the response body only once and store it in bodyBytes
        InputStream responseBodyStream = response.getBody();
        this.bodyBytes = responseBodyStream != null ? readResponseBody(responseBodyStream) : new byte[0];

        // Handle the error if one exists
        if (hasError(response)) {
            handleError(response);
        }
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        // Convert bodyBytes to a String to log the error message correctly
        String errorMessage = new String(bodyBytes, StandardCharsets.UTF_8);
        System.err.println("Error occurred: " + errorMessage);
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public HttpStatusCode getStatusCode() {
        return status;
    }

    public InputStream getBody() {
        return new ByteArrayInputStream(bodyBytes);
    }

    public String getBodyAsString() {
        return new String(bodyBytes, StandardCharsets.UTF_8);
    }

    private byte[] readResponseBody(InputStream inputStream) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString().getBytes(StandardCharsets.UTF_8);
        }
    }
}
