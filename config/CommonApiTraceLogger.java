package com.tag.biometric.ifService.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class CommonApiTraceLogger {

    public Object logApiTimestamp(ProceedingJoinPoint point) throws Throwable {
        long startTime = System.currentTimeMillis();
        log.info("Method: {} ,Started on : {}", point.getSignature(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("d::MMM::uuuu HH::mm::ss")));

        Object result = point.proceed();

        if (result instanceof Mono) {
            return ((Mono<?>) result)
                    .doOnTerminate(() -> logExecutionEnd(point, startTime));
        } else if (result instanceof Flux) {
            return ((Flux<?>) result)
                    .doOnTerminate(() -> logExecutionEnd(point, startTime));
        } else {
            logExecutionEnd(point, startTime);
            return result;
        }
    }

    private void logExecutionEnd(ProceedingJoinPoint point, long startTime) {
        long endTime = System.currentTimeMillis();
        log.info("Method: {} ,Completed on: {}", point.getSignature(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("d::MMM::uuuu HH::mm::ss")));
        log.info("Class Name: {} ,Method Name: {} ,Total Time taken for execution is: {} ms",
                point.getSignature().getDeclaringTypeName(),
                point.getSignature().getName(),
                endTime - startTime);
    }

}

