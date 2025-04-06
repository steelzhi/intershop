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
        Image image = savedImage.block();

        return new ItemDto(item.getName(), item.getDescription(), image != null ? image.getId() : 0, item.getPrice(), 0);
    }

    /*
    public static Mono<ItemDto> mapToItemDto(Mono<Item> itemMono, Mono<Image> savedImage) throws IOException {
        Mono<ItemDto> itemDtoMono = itemMono
                .map(item -> {
                    ItemDto itemDto = new ItemDto(item.getName(), item.getDescription(), 0, item.getPrice(), 0);
                    return itemDto;
                })
                .map(itemDto -> {
                    savedImage.map(image -> {
                        itemDto.setImageId(image.getId());
                        return image;
                    });
                    return itemDto;
                });
        itemDtoMono.subscribe();
        return itemDtoMono;
    }
    */
}
