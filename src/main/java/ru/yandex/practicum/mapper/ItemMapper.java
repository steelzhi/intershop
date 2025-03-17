package ru.yandex.practicum.mapper;

import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.model.Image;
import ru.yandex.practicum.model.Item;

import java.io.IOException;

public class ItemMapper {
    private ItemMapper(){
    }

    public static ItemDto mapToItemDto(Item item) throws IOException {
        Image image = null;
        if (item.getImageFile() != null) {
            byte[] imageBytes = item.getImageFile().getBytes();
            image = new Image(imageBytes);
        }
        return new ItemDto(item.getName(), item.getDescription(), image, item.getPrice(), 0);
    }
}
