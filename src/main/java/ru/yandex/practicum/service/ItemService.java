package ru.yandex.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemService {
    // Для теста
    private boolean isFirstLaunch = true;

    // Для снижения обращений к БД будем также хранить текущий список товаров в кэше
    private Map<Integer, ItemDto> existingItemsDtos = new HashMap<>();

    @Autowired
    private ItemRepository itemRepository;

    public List<List<ItemDto>> getItemsList() throws IOException {
        // Для теста
        if (isFirstLaunch) {
            byte[] imageBytes = Files.readAllBytes(Paths.get("src/main/resources/image-byte-array.txt"));

            for (int i = 0; i < 4; i++) {
                Image image = new Image(imageBytes);
                ItemDto itemDto = new ItemDto("1" + String.valueOf(+Math.random() * 100), "1" + String.valueOf(+Math.random() * 100), image, Math.random() * 100, i);
                ItemDto savedItemDto = itemRepository.save(itemDto);
                existingItemsDtos.put(savedItemDto.getId(), savedItemDto);
            }
            isFirstLaunch = false;
        }

        List<ItemDto> allItems = itemRepository.findAllByOrderById();
        List<List<ItemDto>> itemsDividedBy3 = ListDivider.getDividedListBy3(allItems);
        return itemsDividedBy3;
    }

    public ItemDto getItemDto(int id) {
        return existingItemsDtos.get(id);
    }

    public List<ItemDto> search(String key, SortingCategory sortingCategory) {
        List<ItemDto> itemDtos = null;

        switch (sortingCategory) {
            case NO -> itemDtos = itemRepository.findByNameContainingOrDescriptionContainingOrderById(key, key);
            case ALPHA -> itemDtos = itemRepository.findByNameContainingOrDescriptionOrderByName(key, key);
            case PRICE -> itemDtos = itemRepository.findByNameContainingOrDescriptionOrderByPrice(key, key);
        }

        return itemDtos;
    }

    public ItemDto addItem(Item item) throws IOException {
        ItemDto itemDto = ItemMapper.mapToItemDto(item);
        ItemDto savedItemDto = itemRepository.save(itemDto);
        existingItemsDtos.put(savedItemDto.getId(), savedItemDto);
        return savedItemDto;
    }

    public void decreaseItemAmount(@PathVariable int id) {
        ItemDto itemDto = existingItemsDtos.get(id);
        int currentAmount = itemDto.getAmount();
        if (currentAmount > 0) {
            itemDto.setAmount(--currentAmount);
        }
        itemRepository.save(itemDto);
    }

    public void increaseItemAmount(@PathVariable int id) {
        ItemDto itemDto = existingItemsDtos.get(id);
        int currentAmount = itemDto.getAmount();
        itemDto.setAmount(++currentAmount);
        itemRepository.save(itemDto);
    }
}
