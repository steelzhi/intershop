package ru.yandex.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
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
import java.nio.file.Files;
import java.nio.file.Paths;

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
    private ItemAddingGettingService itemService;

    public Flux<ItemDto> getItemsList(int itemsOnPage, int pageNumber) throws IOException {
        if (!wereTestItemsAdded) {
            addTestItems();
            wereTestItemsAdded = true;
        }

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

    @CachePut(value = "item", key = "#id")
    public Mono<ItemDto> decreaseItemAmount(int id) {
        Mono<ItemDto> itemDtoMono = itemService.getItemDto(id);
        itemDtoMono
                .doOnNext(itemDto -> itemDto.decreaseAmount())
                .flatMap(itemDto -> itemRepository.save(itemDto))
                .doOnNext(itemDto -> System.out.println("Item with id = " + itemDto.getId() + " was decreased: new amount = " + itemDto.getAmount() + ". Upserting cache"))
                .subscribe();
        return itemDtoMono;
    }

    @CachePut(value = "item", key = "#id")
    public Mono<ItemDto> increaseItemAmount(int id) {
        Mono<ItemDto> itemDtoMono = itemService.getItemDto(id);
        itemDtoMono
                .doOnNext(itemDto -> itemDto.increaseAmount())
                .flatMap(itemDto -> itemRepository.save(itemDto))
                .doOnNext(itemDto -> System.out.println("Item with id = " + itemDto.getId() + " was increased: new amount = " + itemDto.getAmount() + ". Upserting cache"))
                .subscribe();
        return itemDtoMono;
    }

    @CacheEvict(value = "item", allEntries = true)
    public void clearCache() {
        System.out.println("Cache was cleared");
    }

    private void addTestItems() throws IOException {
        byte[] imageBytes1 = Files.readAllBytes(Paths.get("main-app/src/main/resources/images-bytes/armature.txt"));
        Image image1 = new Image(imageBytes1);
        Mono<Image> imageMono1 = imageRepository.save(image1);
        Item item1 = new Item("Арматура", "Арматура для строительства", null, 65_000);
        Mono<ItemDto> itemDto1 = ItemMapper.mapToItemDto(Mono.just(item1), imageMono1)
                .doOnNext(itemDto -> itemDto.setAmount(1))
                .flatMap(itemDto -> itemRepository.save(itemDto));

        byte[] imageBytes2 = Files.readAllBytes(Paths.get("main-app/src/main/resources\\images-bytes\\beam.txt"));
        Image image2 = new Image(imageBytes2);
        Mono<Image> imageMono2 = imageRepository.save(image2);
        Item item2 = new Item("Балка", "Балка для перекрытий", null, 130_000);
        Mono<ItemDto> itemDto2 = ItemMapper.mapToItemDto(Mono.just(item2), imageMono2)
                .doOnNext(itemDto -> itemDto.setAmount(5))
                .flatMap(itemDto -> itemRepository.save(itemDto));

        byte[] imageBytes3 = Files.readAllBytes(Paths.get("main-app/src/main/resources\\images-bytes\\pipe.txt"));
        Image image3 = new Image(imageBytes3);
        Mono<Image> imageMono3 = imageRepository.save(image3);
        Item item3 = new Item("Труба стальная", "Труба для водо- и газопроводов", null, 70_000);
        Mono<ItemDto> itemDto3 = ItemMapper.mapToItemDto(Mono.just(item3), imageMono3)
                .doOnNext(itemDto -> itemDto.setAmount(3))
                .flatMap(itemDto -> itemRepository.save(itemDto));

        byte[] imageBytes4 = Files.readAllBytes(Paths.get("main-app/src/main/resources\\images-bytes\\sheet.txt"));
        Image image4 = new Image(imageBytes4);
        Mono<Image> imageMono4 = imageRepository.save(image4);
        Item item4 = new Item("Лист стальной", "Лист для изготовления конструкций", null, 68_000);
        Mono<ItemDto> itemDto4 = ItemMapper.mapToItemDto(Mono.just(item4), imageMono4)
                .doOnNext(itemDto -> itemDto.setAmount(12))
                .flatMap(itemDto -> itemRepository.save(itemDto));

        itemDto1
                .then(itemDto2)
                .then(itemDto3)
                .then(itemDto4)
                .subscribe();
    }

}
