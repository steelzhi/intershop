package ru.yandex.practicum.config;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.model.CartItem;
import ru.yandex.practicum.model.Image;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

@org.springframework.context.annotation.Configuration
public class Configuration {
    /**
     * С помощью бинов {@link RedisCacheManagerBuilderCustomizer} мы можем добавлять
     * кастомизации, которые будут использованы при построении бина {@link RedisCacheManager}.
     * В кастомайзерах можно добавлять общие конфигурации для всех кешей и отдельные по имени кеша.
     * Можно объявлять несколько кастомайзеров, они привязываются списком в автоконфигурации по созданию {@link RedisCacheManager}.
     * В примере ниже добавлен кастомайзер, добавляющий в билдер конфигурацию кеша `weather`.
     */
    @Bean
    public RedisCacheManagerBuilderCustomizer itemCacheCustomizer() {
        return builder -> builder.withCacheConfiguration(
                "item",                                         // Имя кеша
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.of(1, ChronoUnit.MINUTES))  // TTL
                        .serializeValuesWith(                          // Сериализация JSON
                                RedisSerializationContext.SerializationPair.fromSerializer(
                                        new Jackson2JsonRedisSerializer<>(ItemDto.class)
                                )
                        )
        );
    }

}
