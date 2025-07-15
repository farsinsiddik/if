package com.tag.biometric.ifService.config;

import org.springframework.http.HttpRequest;

import java.nio.charset.StandardCharsets;

public class RestTemplateRequestBodyWrapper {

    private String requestBody;
    private HttpRequest httpRequest;

    // Constructor for HttpRequest
    public RestTemplateRequestBodyWrapper(HttpRequest httpRequest, byte[] body) {
        this.httpRequest = httpRequest;
        this.requestBody = new String(body, StandardCharsets.UTF_8);
    }

    public String getRequestBody() {
        return requestBody;
    }

    public String getRequestURI() {
        return (httpRequest != null) ? httpRequest.getURI().toString() : null;
    }

    public String getMethod() {
        return (httpRequest != null) ? httpRequest.getMethod().name() : null;
    }

    public String getHeader(String headerName) {
        return (httpRequest != null) ? httpRequest.getHeaders().getFirst(headerName) : null;
    }

}
