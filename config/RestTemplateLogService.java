package com.tag.biometric.ifService.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tag.biometric.ifService.institutionOnboarding.repository.ApiLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Objects;

import static com.tag.biometric.ifService.config.MaskApiReqResLog.maskSensitiveData;

@Service
@RequiredArgsConstructor
public class RestTemplateLogService {

    private ApiLogRepository apiLogRepository;

    @Async
    public void logRequestAsync(RestTemplateRequestBodyWrapper requestBodyWrapper, String logId, String tracingId) throws JsonProcessingException {
        if (requestBodyWrapper != null) {
            String endpoint = requestBodyWrapper.getRequestURI();
            String method = requestBodyWrapper.getMethod();
            String requestPayload = requestBodyWrapper.getRequestBody();
            String maskedRequestPayload = maskSensitiveData(requestPayload, endpoint, logId);
//            String ipAddress = httpRequest.getRemoteAddr();
            String userAgent = requestBodyWrapper.getHeader(HttpHeaders.USER_AGENT);

            ApiReqResLog apiLog = ApiReqResLog.builder()
                    .uniqueId(logId)
                    .endpoint(endpoint)
                    .requestMethod(method)
                    .externalRequest(true)
                    .requestPayload(maskedRequestPayload)
                    .responsePayload(null) // Initially set to null
                    .tracingId(tracingId)
//                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .timestamp(LocalDateTime.now())
                    .build();

            apiLogRepository.save(apiLog);
        }
    }

    @Async
    public void logResponseAsync(String response, String logId, int statusCode) {
        if (Objects.nonNull(logId)) {
            apiLogRepository.findByUniqueId(logId)
                    .flatMap(apiLog -> {
                        apiLog.setResponseCode(statusCode);
                        apiLog.setResponsePayload(maskSensitiveData(response, apiLog.getEndpoint(), logId));
                        return apiLogRepository.save(apiLog).then();
                    });
        }
    }

    @Async
    public void logResponseAsync(RestTemplateResponseWrapper response, String logId, int statusCode) {
        if (Objects.nonNull(logId)) {
            apiLogRepository.findByUniqueId(logId)
                    .flatMap(apiLog -> {
                        apiLog.setResponseCode(statusCode);
                        apiLog.setResponsePayload(maskSensitiveData(String.valueOf(response), apiLog.getEndpoint(), logId));
                        return apiLogRepository.save(apiLog).then();
                    });
        }
    }

    private String readResponseBody(RestTemplateResponseWrapper response) throws IOException {
        InputStream inputStream = response.getBody();
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }
        return stringBuilder.toString();
    }
}
