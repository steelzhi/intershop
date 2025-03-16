package ru.yandex.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dao.ItemRepository;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.enums.SortingCategory;
import ru.yandex.practicum.mapper.ItemMapper;
import ru.yandex.practicum.model.Image;
import ru.yandex.practicum.model.Item;
import ru.yandex.practicum.util.ListDivider;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Service
public class ItemService {
    // Для теста
    private boolean isFirstLaunch = true;

    @Autowired
    private ItemRepository itemRepository;

    public List<List<ItemDto>> getItemsList() throws IOException {
        // Для теста
        if (isFirstLaunch) {
            byte[] imageBytes = Files.readAllBytes(Paths.get("src/main/resources/image-byte-array.txt"));

            for (int i = 0; i < 4; i++) {
                Image image = new Image(imageBytes);
                itemRepository.save(new ItemDto("1" + String.valueOf(+Math.random() * 100), String.valueOf(i), image, Math.random() * 100));
            }
            isFirstLaunch = false;
        }

        List<ItemDto> allItems = itemRepository.findAll();
        List<List<ItemDto>> itemsDividedBy3 = ListDivider.getDividedListBy3(allItems);
        return itemsDividedBy3;
    }

    public ItemDto getItemDto(int id) {
        return itemRepository.findById(id).get();
    }

    public List<ItemDto> search(String key, SortingCategory sortingCategory) {
        List<ItemDto> itemDtos = null;

        switch (sortingCategory) {
            case NO -> itemDtos = itemRepository.findByNameContainingOrderById(key);
            case ALPHA -> itemDtos = itemRepository.findByNameContainingOrderByName(key);
            case PRICE -> itemDtos = itemRepository.findByNameContainingOrderByPrice(key);
        }
        ;

        //List<ItemDto> itemDtos = itemRepository.findByKeyLikeAndSort(key, sort);

        return itemDtos;
    }

    public ItemDto addItem(Item item) throws IOException {
        ItemDto itemDto = ItemMapper.mapToItemDto(item);
        return itemRepository.save(itemDto);
    }
}
