package ru.yandex.practicum.config;

import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Bean
    WebClient webClient() {
        return WebClient.create();
    }
}
