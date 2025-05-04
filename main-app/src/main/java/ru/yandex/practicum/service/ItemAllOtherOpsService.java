package ru.yandex.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
 * Пришлось разбить первоначальный сервис ItemService на 2 сервиса: ItemAddingGettingService и ItemAllOtherOpsService.
 * 1-й сервис содержит метод получения товара из кэша. Соответственно, чтобы не происходило @Cacheable self-invocation,
 * другие аннотированные методы были вынесены во 2-й сервис
 */
@Service
public class ItemAllOtherOpsService {
    private boolean wereTestItemsAdded;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ItemGettingFromCacheService itemGettingFromCacheService;

    public Flux<ItemDto> getItemsList(int itemsOnPage, int pageNumber) throws IOException {
        Flux<Integer> allItemsIdsOnPage = itemRepository.getAllItemIdsOnPage(pageNumber, itemsOnPage);

        // Вызываем метод с кэшированием из другого сервиса
        Flux<ItemDto> allItems = allItemsIdsOnPage.flatMap(id -> itemGettingFromCacheService.getItemDto(id));
        return allItems;
    }

    public Flux<ItemDto> search(String key, SortingCategory sortingCategory) {
        Flux<Integer> searchedItemsIds = switch (sortingCategory) {
            case NO -> itemRepository.findIdsByNameOrDescriptionOrderById(key);
            case ALPHA -> itemRepository.findIdsByNameOrDescriptionOrderByName(key);
            case PRICE -> itemRepository.findIdsByNameOrDescriptionOrderByPrice(key);
        };

        Flux<ItemDto> searchedItems = searchedItemsIds.flatMap(id -> itemGettingFromCacheService.getItemDto(id));

        return searchedItems;
    }

    public Mono<Integer> getItemListSize() {
        return itemRepository.getItemListSize();
    }

    @CachePut(value = "item", key = "#id")
    public Mono<ItemDto> decreaseItemAmount(int id) {
        Mono<ItemDto> itemDtoMono = itemGettingFromCacheService.getItemDto(id);
        itemDtoMono
                .doOnNext(itemDto -> itemDto.decreaseAmount())
                .flatMap(itemDto -> itemRepository.save(itemDto))
                .doOnNext(itemDto -> System.out.println("Item with id = " + itemDto.getId()
                                                        + " was decreased: new amount = " + itemDto.getAmount()
                                                        + ". Upserting cache"))
                .subscribe();
        return itemDtoMono;
    }

    @CachePut(value = "item", key = "#id")
    public Mono<ItemDto> increaseItemAmount(int id) {
        Mono<ItemDto> itemDtoMono = itemGettingFromCacheService.getItemDto(id);
        itemDtoMono
                .doOnNext(itemDto -> itemDto.increaseAmount())
                .flatMap(itemDto -> itemRepository.save(itemDto))
                .doOnNext(itemDto -> System.out.println("Item with id = " + itemDto.getId()
                                                        + " was increased: new amount = " + itemDto.getAmount()
                                                        + ". Upserting cache"))
                .subscribe();
        return itemDtoMono;
    }

    /*
     * Если напрямую обращаться к методу ItemAddingGettingService#getItemDto, сначала возвращаются данные из кэша
     * (если он пуст, соответственно, возвращается null), а затем уже производится обновление кэша
     */
    public Mono<ItemDto> getItemDto(int id) {
        Mono<ItemDto> itemDtoMono = Mono.just(id)
                .flatMap(id1 -> itemGettingFromCacheService.getItemDto(id1));
        return itemDtoMono;
    }

    @CachePut(value = "item", key = "#item.id")
    public Mono<ItemDto> addItem(Item item) {
        Mono<Item> itemMono = Mono.just(item);
        Mono<Image> savedImageMono = addImageToDbAndGetMono(item.getImageFile());
        Mono<ItemDto> itemDtoMono = ItemMapper.mapToItemDto(itemMono, savedImageMono);
        Mono<ItemDto> savedItemDto = itemDtoMono.flatMap(itemDto -> itemRepository.save(itemDto));

        return savedItemDto.doOnNext(itemDto -> item.setId(itemDto.getId()));
    }

    private Mono<Image> addImageToDbAndGetMono(FilePart filePart) {
        if (filePart == null) {
            return Mono.empty();
        }

        List<byte[]> bytesList = new ArrayList<>();

        Mono<Boolean> booleanFlux = DataBufferUtils.join(filePart.content())
                .map(content ->
                        bytesList.add(content.asByteBuffer().array()));

        Mono<Image> imageMono = booleanFlux
                .flatMap(hasImage -> getSavedImage(hasImage, bytesList));

        return imageMono;
    }

    private Mono<Image> getSavedImage(boolean hasImage, List<byte[]> bytesList) {
        if (hasImage) {
            Image image = new Image(bytesList.get(0));
            Mono<Image> savedImage = imageRepository.save(image);
            return savedImage;
        } else {
            return Mono.empty();
        }
    }

    @CacheEvict(value = "item", allEntries = true)
    public void clearCache() {
        System.out.println("Cache was cleared");
    }
}
