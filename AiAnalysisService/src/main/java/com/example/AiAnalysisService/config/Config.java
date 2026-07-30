package com.example.AiAnalysisService.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class Config {

    @Bean
    @LoadBalanced
    public WebClient.Builder WebClientBuilder(){
        return WebClient.builder();
    }

    @Bean
    public WebClient uploadServiceWebClient(WebClient.Builder webClientBuilder){
        return WebClient.builder()
                .baseUrl("http://UPLOAD-SERVICE")
                .build();
    }

    @Bean
    public WebClient genericWebClient(){
        final int size = 100 * 1024 * 1024;

        final ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(codec -> codec.defaultCodecs().maxInMemorySize(size))
                .build();
        return WebClient.builder()
                .exchangeStrategies(exchangeStrategies)
                .build();
    }

    @Bean
    public CommonErrorHandler errorHandler() {
        return new DefaultErrorHandler(new FixedBackOff(0L, 0L));
    }
}
