package com.tag.biometric.ifService.config;

import io.micrometer.tracing.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;


@Slf4j
@Aspect
@Component
public class ApiLoggingAspect extends ApiReqResLogger {

    @Value("${logging.internal.enabled}")
    private boolean isApiLoggingEnabled;

    public ApiLoggingAspect(ApplicationEventPublisher eventPublisher, ApiLogService loggingService, Tracer tracer) {
        super(eventPublisher, loggingService, tracer);
    }

    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    public Object logRequestAndResponse(ProceedingJoinPoint joinPoint) throws Throwable {
        if (isApiLoggingEnabled) {
            return super.logRequestAndResponse(joinPoint);
        } else {
            return joinPoint.proceed();
        }
    }

}
