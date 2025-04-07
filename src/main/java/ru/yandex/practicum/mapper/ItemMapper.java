package ru.yandex.practicum.mapper;

import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.model.Image;
import ru.yandex.practicum.model.Item;

import java.io.IOException;

public class ItemMapper {
    private ItemMapper(){
    }

    public static Mono<ItemDto> mapToItemDto(Mono<Item> item, Mono<Image> savedImage) throws IOException {
        Mono<ItemDto> itemDtoMono = item
                .map(item1 -> new ItemDto(item1.getName(), item1.getDescription(), null, item1.getPrice(), 0));
        itemDtoMono.subscribe();
        Mono<ItemDto> itemDtoMono1 = itemDtoMono.zipWith(savedImage, (itemDto, image) -> {
            itemDto.setImageId(image.getId());
            return itemDto;
        });
        itemDtoMono1.subscribe();

/*        Image image = savedImage.block();

        return new ItemDto(item.getName(), item.getDescription(), image != null ? image.getId() : 0, item.getPrice(), 0);*/
        return itemDtoMono1;
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
