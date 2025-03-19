package ru.yandex.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.dao.ItemRepository;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.enums.SortingCategory;
import ru.yandex.practicum.mapper.ItemMapper;
import ru.yandex.practicum.model.Image;
import ru.yandex.practicum.model.Item;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    public List<ItemDto> getItemsList(int itemsOnPage, int pageNumber) throws IOException {
        // Для теста
        /*if (isFirstLaunch) {
            byte[] imageBytes1 = Files.readAllBytes(Paths.get("src/main/resources/images-bytes/pipe.txt"));
            Image image1 = new Image(imageBytes1);
            ItemDto itemDto = new ItemDto("Труба круглая", "Труба для водо- и газопроводов", image1, 72000, 0);
            ItemDto savedItemDto = itemRepository.save(itemDto);
            existingItemsDtos.put(savedItemDto.getId(), savedItemDto);

            byte[] imageBytes2 = Files.readAllBytes(Paths.get("src/main/resources/images-bytes/beam.txt"));
            Image image2 = new Image(imageBytes2);
            itemDto = new ItemDto("Балка", "Балка для перекрытий", image2, 104000, 0);
            savedItemDto = itemRepository.save(itemDto);
            existingItemsDtos.put(savedItemDto.getId(), savedItemDto);

            byte[] imageBytes3 = Files.readAllBytes(Paths.get("src/main/resources/images-bytes/armature.txt"));
            Image image3 = new Image(imageBytes3);
            itemDto = new ItemDto("Арматура", "Стальная арматура для строительства", image3, 77000, 0);
            savedItemDto = itemRepository.save(itemDto);
            existingItemsDtos.put(savedItemDto.getId(), savedItemDto);

            byte[] imageBytes4 = Files.readAllBytes(Paths.get("src/main/resources/images-bytes/sheet.txt"));
            Image image4 = new Image(imageBytes4);
            itemDto = new ItemDto("Лист гладкий", "Лист стальной для изготовления изделий", image4, 81000, 0);
            savedItemDto = itemRepository.save(itemDto);
            existingItemsDtos.put(savedItemDto.getId(), savedItemDto);

            isFirstLaunch = false;
        }*/

        PageRequest page = PageRequest.of(pageNumber - 1, itemsOnPage);

        Page<ItemDto> allItems = itemRepository.findAllByOrderById(page);
        return allItems.getContent();
    }

    public ItemDto getItemDto(int id) {
        return existingItemsDtos.get(id);
    }

    public List<ItemDto> search(String key, SortingCategory sortingCategory) {
        List<ItemDto> itemDtos = null;

        switch (sortingCategory) {
            case NO -> itemDtos = itemRepository.findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingOrderById(key, key);
            case ALPHA -> itemDtos = itemRepository.findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingOrderByName(key, key);
            case PRICE -> itemDtos = itemRepository.findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingOrderByPrice(key, key);
        }

        return itemDtos;
    }

    public ItemDto addItem(Item item) throws IOException {
        ItemDto itemDto = ItemMapper.mapToItemDto(item);
        ItemDto savedItemDto = itemRepository.save(itemDto);
        existingItemsDtos.put(savedItemDto.getId(), savedItemDto);
        return savedItemDto;
    }

    public ItemDto decreaseItemAmount(@PathVariable int id) {
        ItemDto itemDto = existingItemsDtos.get(id);
        int currentAmount = itemDto.getAmount();
        if (currentAmount > 0) {
            itemDto.setAmount(--currentAmount);
        }
        return itemRepository.save(itemDto);
    }

    public ItemDto increaseItemAmount(@PathVariable int id) {
        ItemDto itemDto = existingItemsDtos.get(id);
        int currentAmount = itemDto.getAmount();
        itemDto.setAmount(++currentAmount);
        return itemRepository.save(itemDto);
    }

    public int getItemListSize() {
        return itemRepository.getItemListSize();
    }

    public Map<Integer, ItemDto> getExistingItemsDtos() {
        return existingItemsDtos;
    }
}
