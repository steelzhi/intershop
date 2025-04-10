package ru.yandex.practicum.mapper;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.model.Image;
import ru.yandex.practicum.model.Item;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@Tag("unit")
public class ItemMapperTest {
    @Test
    void testCorrectMapping() throws IOException {
        byte[] imageBytes1 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\armature.txt"));
        Image image1 = new Image(imageBytes1);
        image1.setId(1);
        Item item1 = new Item("itemDto1", "abcdesc1", null, 1.0);
        Mono<ItemDto> itemDtoMono1 = ItemMapper.mapToItemDto(Mono.just(item1), Mono.just(image1))
                .doOnNext(itemDto -> itemDto.setId(1));

        assertEquals(itemDtoMono1.block().getId(), 1, "Id should be 0");
        assertEquals(itemDtoMono1.block().getName(), item1.getName(), "Items names should be the same");
        assertEquals(itemDtoMono1.block().getImageId(), 1, "Image should have id = 1");
        assertEquals(itemDtoMono1.block().getDescription(), item1.getDescription(), "Items descriptions should be the same");
    }
}
