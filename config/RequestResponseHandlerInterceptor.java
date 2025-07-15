package com.tag.biometric.ifService.config;

import com.tag.biometric.ifService.config.model.SessionInfo;
import io.micrometer.common.util.StringUtils;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.inject.Provider;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.tag.biometric.ifService.util.Utils.generateUniqueLogId;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@Slf4j
@AllArgsConstructor
@RequiredArgsConstructor
public class RequestResponseHandlerInterceptor implements ClientHttpRequestInterceptor {

    private Provider<SessionInfo> sessionInfoProvider;

    private RestTemplateLogService loggingService;

    private Tracer tracer;

    public RequestResponseHandlerInterceptor(Provider<SessionInfo> sessionInfoProvider) {
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        log.info("Interceptor..");

        String logId = generateUniqueLogId();
        Span currentSpan = tracer.currentSpan();
        if (currentSpan == null) {
            currentSpan = tracer.nextSpan().name("external-request").start();
        }
        String tracingId = currentSpan.context().traceId();

        try {
            RestTemplateRequestBodyWrapper requestWrapper = new RestTemplateRequestBodyWrapper(request, body);

            // Log the request
            loggingService.logRequestAsync(requestWrapper, logId, tracingId);
            ClientHttpResponse response = execution.execute(request, body);

            // Wrap and log the response
            RestTemplateResponseWrapper responseWrapper = new RestTemplateResponseWrapper(response);
            loggingService.logResponseAsync(responseWrapper, logId, response.getStatusCode().value());

            // If the response status is UNAUTHORIZED, retry with a new token
            if (HttpStatus.UNAUTHORIZED == response.getStatusCode()) {
                SessionInfo sessionInfo = sessionInfoProvider.get();
                String accessToken = "Bearer " + sessionInfo.getToken();
                if (!StringUtils.isEmpty(accessToken)) {
                    request.getHeaders().remove(AUTHORIZATION);
                    request.getHeaders().add(AUTHORIZATION, accessToken);
                    log.info("Bearer token set for jit funding gateway for client.");

                    // Log the retry request
                    requestWrapper = new RestTemplateRequestBodyWrapper(request, body);
                    loggingService.logRequestAsync(requestWrapper, logId, tracingId);
                    response = execution.execute(request, body);
                    // Wrap and log the response
                    responseWrapper = new RestTemplateResponseWrapper(response);
                    loggingService.logResponseAsync(responseWrapper, logId, response.getStatusCode().value());
                    return new BufferedClientHttpResponse(responseWrapper);
                }
            }

            return new BufferedClientHttpResponse(responseWrapper);
        } finally {
            currentSpan.end();
        }
    }
}
