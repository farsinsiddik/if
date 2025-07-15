package com.tag.biometric.ifService.config.netty;

/**
 * Copyrights (c) 2022. Tag Biometric
 * All rights reserved with Tag Biometrics.
 *
 * @author Farsin Siddik
 * @date 09-01-2025
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.netty.resources.LoopResources;

@Configuration
public class NettyConfig {

    @Bean
    public LoopResources loopResources() {
        return LoopResources.create("my-event-loop", 100, true); // Min 16, Max 64 threads
    }
}
