package com.example.booking.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    @Qualifier("userWebClient")
    public WebClient userWebClient(@Value("${user-service.url}") String baseUrl) {
        return WebClient.builder().baseUrl(baseUrl).build();
    }
    
    @Bean
    @Qualifier("availabilityWebClient")
    public WebClient availabilityWebClient(@Value("${availability-service.url}") String baseUrl) {
        return WebClient.builder().baseUrl(baseUrl).build();
    }
}