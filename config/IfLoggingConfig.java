package com.tag.biometric.ifService.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Aspect
public class IfLoggingConfig extends CommonApiTraceLogger {
    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object logApiTimestamp(ProceedingJoinPoint point) throws Throwable {
        return super.logApiTimestamp(point);
    }

}

