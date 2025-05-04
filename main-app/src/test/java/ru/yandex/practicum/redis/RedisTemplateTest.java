/*
package ru.yandex.practicum.redis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import ru.yandex.practicum.config.EmbeddedRedisConfiguration;
import ru.yandex.practicum.constant.Constants;
import ru.yandex.practicum.dto.ItemDto;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Import(EmbeddedRedisConfiguration.class)
public class RedisTemplateTest {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    void testItemDtoTltInRedis() throws InterruptedException {
        ItemDto itemDto = new ItemDto("itemDto", "desc", null, 1, 1);
        itemDto.setId(1);
        redisTemplate.opsForValue().set("item:" + itemDto.getId(), itemDto, Constants.TTL, TimeUnit.SECONDS);

        // Проверяем наличие записи в Redis
        assertThat(redisTemplate.opsForValue().get("item:" + itemDto.getId()))
                .withFailMessage("Пока не прошёл TTL, запись должна быть доступна")
                .isNotNull()
                .isEqualTo(itemDto);

        // Ждём, пока TTL истечёт
        TimeUnit.SECONDS.sleep(Constants.TTL + 1);

        // Проверяем, что запись пропала
        assertThat(redisTemplate.opsForValue().get("item:" + itemDto.getId()))
                .withFailMessage("После истечения TTL запись должна пропасть")
                .isNull();
    }
}
*/
