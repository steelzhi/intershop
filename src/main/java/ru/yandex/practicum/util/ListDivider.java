package ru.yandex.practicum.util;

import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.model.Item;

import java.util.ArrayList;
import java.util.List;

public class ListDivider {
    private ListDivider() {
    }

    public static List<List<ItemDto>> getDividedListBy3(List<ItemDto> allItems) {
        List<List<ItemDto>> dividedList = new ArrayList<>();
        for (int i = 0; i < allItems.size(); i += 3) {
            List<ItemDto> listOf3Items = new ArrayList<>();
            for (int j = 0; j < (3 < allItems.size() - i ? 3 : allItems.size() - i); j++) {
                listOf3Items.add(allItems.get(j + i));
            }
            dividedList.add(listOf3Items);
        }

        return dividedList;
    }
}
