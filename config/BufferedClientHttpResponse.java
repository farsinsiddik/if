package com.tag.biometric.ifService.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.io.InputStream;

public class BufferedClientHttpResponse implements ClientHttpResponse {
    private final RestTemplateResponseWrapper responseWrapper;

    public BufferedClientHttpResponse(RestTemplateResponseWrapper responseWrapper) {
        this.responseWrapper = responseWrapper;
    }

    @Override
    public InputStream getBody() {
        return responseWrapper.getBody();
    }

    @Override
    public HttpHeaders getHeaders() {
        // Return a mock or real HttpHeaders if needed
        return responseWrapper.getHeaders();
    }

    @Override
    public HttpStatus getStatusCode() throws IOException {
        // Return a mock or real HttpStatus if needed
        return (HttpStatus) responseWrapper.getStatusCode();
    }

    @Override
    public String getStatusText() throws IOException {
        // Return a mock or real status text if needed
        return HttpStatus.OK.getReasonPhrase();
    }

    @Override
    public void close() {
        // Close resources if needed
    }
}
