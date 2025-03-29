package ru.yandex.practicum.mapper;

import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.model.Image;
import ru.yandex.practicum.model.Item;

import java.io.IOException;

public class ItemMapper {
    private ItemMapper(){
    }

    public static ItemDto mapToItemDto(Item item, Mono<Image> savedImage) throws IOException {
        return new ItemDto(item.getName(), item.getDescription(), savedImage.block().getId(), item.getPrice(), 0);
    }
}
