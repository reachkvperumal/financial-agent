package com.kv.llm.financial.agent.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class Initialize {
    @Value("${fmp.hostName:null}")
    private String hostname;


    @Bean
    public RestClient fmpClient() {
        return RestClient.builder().baseUrl(hostname).defaultHeader("Content-Type", "application/json").build();
    }

}
