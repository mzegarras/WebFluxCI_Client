package com.example.lab04client.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@AllArgsConstructor
public class AppConfig {

    private final ApiProperties apiProperties;

    @Bean
    public WebClient getWebClient(){
        return WebClient.create(apiProperties.getUrl());
    }

}
