package ru.yandex.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dao.ItemRepository;
import ru.yandex.practicum.dto.ItemDto;

/*
 * Пришлось разбить первоначальный сервис ItemService на 2 сервиса: ItemAddingGettingService и ItemAllOtherOpsService.
 * 1-й сервис содержит метод получения товара из кэша. Соответственно, чтобы не происходило @Cacheable self-invocation,
 * другие аннотированные методы были вынесены во 2-й сервис
 */
@Service
public class ItemGettingFromCacheService {

    @Autowired
    private ItemRepository itemRepository;

    @Cacheable(value = "item", key = "#id")
    public Mono<ItemDto> getItemDto(int id) {
        System.out.println("Cache doesn't contain item with id = " + id + ". Adding...");
        Mono<ItemDto> itemDtoMono = itemRepository.findById(id);
        return itemDtoMono;
    }
}
