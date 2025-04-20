package ru.yandex.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
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

/*
 * Пришлось разбить первоначальный сервис ItemService на 2 сервиса: ItemAddingGettingService и ItemAllOtherOpsService.
 * 1-й сервис содержит метод получения товара из кэша. Соответственно, чтобы не происходило @Cacheable self-invocation,
 * другие аннотированные методы были вынесены во 2-й сервис
 */
@Service
public class ItemAddingGettingService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ImageRepository imageRepository;

    // сначала берет данные их кэша, а потом уже вычисляет новое значение
    @Cacheable(value = "item", key = "#id")
    public Mono<ItemDto> getItemDto(int id) {
        System.out.println("Cache doesn't contain item with id = " + id + ". Adding...");
        Mono<ItemDto> itemDtoMono = itemRepository.findById(id);
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
}
