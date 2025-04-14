package ru.yandex.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.domain.PageRequest;
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

@Service
public class ItemService {
    /*    // Для снижения обращений к БД будем также хранить текущий список товаров в кэше
    private Map<Integer, ItemDto> existingItemsDtos = new HashMap<>();*/

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ImageRepository imageRepository;

    public Flux<ItemDto> getItemsList(int itemsOnPage, int pageNumber) throws IOException {

        PageRequest page = PageRequest.of(pageNumber - 1, itemsOnPage);

        Flux<ItemDto> allItems = itemRepository.findAllByOrderById(page);
        return allItems;
    }

    public Mono<ItemDto> getItemDto(int id) {
        //return existingItemsDtos.get(id);
        return itemRepository.findById(id);
    }

    public Flux<ItemDto> search(String key, SortingCategory sortingCategory) {
        Flux<ItemDto> itemDtos = switch (sortingCategory) {
            case NO ->
                    itemRepository.findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingOrderById(key, key);
            case ALPHA ->
                    itemRepository.findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingOrderByName(key, key);
            case PRICE ->
                    itemRepository.findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingOrderByPrice(key, key);
        };

        return itemDtos;
    }

    public Mono<ItemDto> addItem(Item item) {
        Mono<Item> itemMono = Mono.just(item);
        Mono<Image> savedImageMono = addImageToDbAndGetMono(item.getImageFile());
        Mono<ItemDto> itemDtoMono = ItemMapper.mapToItemDto(itemMono, savedImageMono);
        Mono<ItemDto> savedItemDto = itemDtoMono.flatMap(itemDto -> itemRepository.save(itemDto));

        //existingItemsDtos.put(savedItemDto.getId(), savedItemDto);
        return savedItemDto;
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

    public Mono<ItemDto> decreaseItemAmount(int id) {
        //ItemDto itemDto = existingItemsDtos.get(id);

        Mono<ItemDto> itemDtoMono = getItemDto(id);
        itemDtoMono
                .doOnNext(itemDto -> itemDto.decreaseAmount())
                .flatMap(itemDto -> itemRepository.save(itemDto))
                .subscribe();
        return itemDtoMono;
    }

    public Mono<ItemDto> increaseItemAmount(int id) {
        //ItemDto itemDto = existingItemsDtos.get(id);

        Mono<ItemDto> itemDtoMono = getItemDto(id);
        itemDtoMono
                .doOnNext(itemDto -> itemDto.increaseAmount())
                .flatMap(itemDto -> itemRepository.save(itemDto))
                .subscribe();
        return itemDtoMono;
    }

    public Mono<Integer> getItemListSize() {
        return itemRepository.getItemListSize();
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

    /*public Map<Integer, ItemDto> getExistingItemsDtos() {
        return existingItemsDtos;
    }

    public void setExistingItemDtosAllDtosAmountToZero() {
        for (Integer itemDtoId : existingItemsDtos.keySet()) {
            setInExistingItemDtosItemDtoAmountToZero(itemDtoId);
        }
    }

    public void setInExistingItemDtosItemDtoAmountToZero(int id) {
        existingItemsDtos.get(id).setAmount(0);
    }*/
}
