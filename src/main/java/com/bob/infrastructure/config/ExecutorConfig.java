package com.bob.infrastructure.config;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExecutorConfig {

    @Bean(name = "sseHeartbeatScheduler")
    public ScheduledExecutorService sseHeartbeatScheduler() {
        return Executors.newSingleThreadScheduledExecutor();
    }
}
