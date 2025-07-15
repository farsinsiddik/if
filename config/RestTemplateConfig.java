package com.tag.biometric.ifService.config;

import com.tag.biometric.ifService.config.model.SessionInfo;
import io.micrometer.tracing.Tracer;
import jakarta.inject.Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.web.firewall.RequestRejectedHandler;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class RestTemplateConfig {

    private final Tracer tracer;
    private final RestTemplateLogService loggingService;

    @Bean(name = "restTemplate")
    @Primary
    public RestTemplate restTemplate(Provider<SessionInfo> sessionInfoProvider) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(
                new RequestResponseHandlerInterceptor(sessionInfoProvider, loggingService, tracer)
        ));
        return restTemplate;
    }

    @Bean(name = "loadBalanced")
    @LoadBalanced
    public RestTemplate getRestTemplate(Provider<SessionInfo> sessionInfoProvider) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(
                new RequestResponseHandlerInterceptor(sessionInfoProvider)
        ));
        return restTemplate;
    }
}
