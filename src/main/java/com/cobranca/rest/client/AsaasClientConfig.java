package com.cobranca.rest.client;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AsaasClientConfig {

    @Value("${asaas.token}")
    private String token;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            template.header("access_token", token);
            template.header("accept", "application/json");
            template.header("content-type", "application/json");
        };
    }
}
