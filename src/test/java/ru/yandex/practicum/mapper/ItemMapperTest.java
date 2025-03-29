/*
package ru.yandex.practicum.mapper;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.model.Item;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("unit")
public class ItemMapperTest {
    @Test
    void testCorrectMapping() throws IOException {
        Item item = new Item("Item", "Desc", null, 1.0);
        ItemDto itemDto = ItemMapper.mapToItemDto(item);
        assertEquals(itemDto.getId(), 0, "Id should be 0");
        assertEquals(itemDto.getName(), item.getName(), "Items names should be the same");
        assertEquals(itemDto.getImage(), null, "Item didn't have an image");
        assertEquals(itemDto.getDescription(), item.getDescription(), "Items descriptions should be the same");
    }
}
*/
