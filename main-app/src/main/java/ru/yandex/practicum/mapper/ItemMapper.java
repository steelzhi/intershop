package ru.yandex.practicum.mapper;

import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.model.Image;
import ru.yandex.practicum.model.Item;

public class ItemMapper {
    private ItemMapper(){
    }

    public static Mono<ItemDto> mapToItemDto(Mono<Item> item, Mono<Image> savedImage) {
        Mono<ItemDto> itemDtoMono = item
                .map(item1 -> new ItemDto(item1.getName(), item1.getDescription(), null, item1.getPrice(), 0));
        Mono<ItemDto> itemDtoMono1 = itemDtoMono.zipWith(savedImage, (itemDto, image) -> {
            itemDto.setImageId(image.getId());
            return itemDto;
        });

        return itemDtoMono1;
    }
}
