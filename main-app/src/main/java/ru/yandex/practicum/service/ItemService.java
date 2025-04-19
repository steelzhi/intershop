package ru.yandex.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dao.ImageRepository;
import ru.yandex.practicum.dao.ItemRepository;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.mapper.ItemMapper;
import ru.yandex.practicum.model.Image;
import ru.yandex.practicum.model.Item;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ImageRepository imageRepository;

/*    public Flux<ItemDto> getItemsList(int itemsOnPage, int pageNumber) {
        PageRequest page = PageRequest.of(pageNumber - 1, itemsOnPage);

        Flux<ItemDto> allItems = itemRepository.findAllByOrderById(page);
        return allItems;
    }*/

/*    public Flux<ItemDto> getItemsList(int itemsOnPage, int pageNumber) {
        Flux<Integer> allItemsIdsOnPage = itemRepository.getAllItemIdsOnPage(pageNumber, itemsOnPage);

        Flux<ItemDto> allItems = allItemsIdsOnPage.flatMap(id -> getItemDto(id));
        return allItems;
    }*/

    // сначала берет данные их кэша, а потом уже вычисляет новое значение
    @Cacheable(value = "item", key = "#id")
    public Mono<ItemDto> getItemDto(int id) {
        System.out.println("Cache doesn't contain item with id = " + id + ". Adding...");
        Mono<ItemDto> itemDtoMono = itemRepository.findById(id);
        return itemDtoMono;
    }

/*    public Flux<ItemDto> search(String key, SortingCategory sortingCategory) {
        Flux<ItemDto> itemDtos = switch (sortingCategory) {
            case NO ->
                    itemRepository.findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingOrderById(key, key);
            case ALPHA ->
                    itemRepository.findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingOrderByName(key, key);
            case PRICE ->
                    itemRepository.findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingOrderByPrice(key, key);
        };

        return itemDtos;
    }*/

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

    @CachePut(value = "item", key = "#id")
    public Mono<ItemDto> decreaseItemAmount(int id) {
        Mono<ItemDto> itemDtoMono = getItemDto(id);
        itemDtoMono
                .doOnNext(itemDto -> itemDto.decreaseAmount())
                .flatMap(itemDto -> itemRepository.save(itemDto))
                .doOnNext(itemDto -> System.out.println("Item with id = " + itemDto.getId() + " was decreased: new amount = " + itemDto.getAmount() + ". Upserting cache"))
                .subscribe();
        return itemDtoMono;
    }

    @CachePut(value = "item", key = "#id")
    public Mono<ItemDto> increaseItemAmount(int id) {
        Mono<ItemDto> itemDtoMono = getItemDto(id);
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

/*    public Mono<Integer> getItemListSize() {
        return itemRepository.getItemListSize();
    }*/

    private Mono<Image> getSavedImage(boolean hasImage, List<byte[]> bytesList) {
        if (hasImage) {
            Image image = new Image(bytesList.get(0));
            Mono<Image> savedImage = imageRepository.save(image);
            return savedImage;
        } else {
            return Mono.empty();
        }
    }
}
