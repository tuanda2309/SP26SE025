package com.example.SP26SE025.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Cấu hình cho AI-Service (FastAPI) integration
 * Cung cấp RestTemplate bean để giao tiếp HTTP với AI microservice
 */
@Configuration
public class AiServiceConfig {

    @Value("${ai.service.url:http://localhost:8000}")
    private String aiServiceUrl;

    @Value("${ai.service.timeout:60}")
    private int timeout;

    @Value("${ai.service.enabled:true}")
    private boolean enabled;

    /**
     * Tạo RestTemplate bean cấu hình cho gọi AI-Service
     * Bao gồm timeout và cài đặt kết nối
     */
    @Bean
    public RestTemplate aiServiceRestTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(timeout))
                .setReadTimeout(Duration.ofSeconds(timeout))
                .build();
    }

    public String getAiServiceUrl() {
        return aiServiceUrl;
    }

    public int getTimeout() {
        return timeout;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
