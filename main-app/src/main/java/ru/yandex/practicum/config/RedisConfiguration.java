package ru.yandex.practicum.config;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.web.reactive.function.client.WebClient;
import ru.yandex.practicum.constant.Constants;
import ru.yandex.practicum.dto.ItemDto;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@org.springframework.context.annotation.Configuration
public class RedisConfiguration {
    @Bean
    public RedisCacheManagerBuilderCustomizer itemCacheCustomizer() {
        return builder -> builder.withCacheConfiguration(
                "item",                                         // Имя кеша
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.of(Constants.TTL, ChronoUnit.SECONDS))  // TTL
                        .serializeValuesWith(                          // Сериализация JSON
                                RedisSerializationContext.SerializationPair.fromSerializer(
                                        new Jackson2JsonRedisSerializer<>(ItemDto.class)
                                )
                        )
        );
    }

    @Bean
    WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }
}
