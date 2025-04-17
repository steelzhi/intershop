package ru.yandex.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dao.ImageRepository;
import ru.yandex.practicum.dao.ItemRepository;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.enums.SortingCategory;
import ru.yandex.practicum.mapper.ItemMapper;
import ru.yandex.practicum.model.Image;
import ru.yandex.practicum.model.Item;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemsService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemService itemService;

    public Flux<ItemDto> getItemsList(int itemsOnPage, int pageNumber) {
        Flux<Integer> allItemsIdsOnPage = itemRepository.getAllItemIdsOnPage(pageNumber, itemsOnPage);

        // Вызываем метод с кэшированием из другого сервиса
        Flux<ItemDto> allItems = allItemsIdsOnPage.flatMap(id -> itemService.getItemDto(id));
        return allItems;
    }

    public Flux<ItemDto> search(String key, SortingCategory sortingCategory) {
        Flux<Integer> searchedItemsIds = switch (sortingCategory) {
            case NO ->
                    itemRepository.findIdsByNameOrDescriptionOrderById(key);
            case ALPHA ->
                    itemRepository.findIdsByNameOrDescriptionOrderByName(key);
            case PRICE ->
                    itemRepository.findIdsByNameOrDescriptionOrderByPrice(key);
        };

        Flux<ItemDto> searchedItems = searchedItemsIds.flatMap(id -> itemService.getItemDto(id));

        return searchedItems;
    }

    public Mono<Integer> getItemListSize() {
        return itemRepository.getItemListSize();
    }

}
