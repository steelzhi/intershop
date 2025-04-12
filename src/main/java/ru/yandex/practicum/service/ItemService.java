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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class ItemService {
    /*    // Для снижения обращений к БД будем также хранить текущий список товаров в кэше
    private Map<Integer, ItemDto> existingItemsDtos = new HashMap<>();*/

    // Нужно для тестового набора данных
    private boolean wasTestItemAdded;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ImageRepository imageRepository;

    public Flux<ItemDto> getItemsList(int itemsOnPage, int pageNumber) throws IOException {

        // Для удобства добавим тестовые товары
/*
        if (!wasTestItemAdded) {
            addTestItems();
            wasTestItemAdded = true;
        }
*/

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

    public Mono<ItemDto> addItem(Item item) throws IOException {
        Mono<Item> itemMono = Mono.just(item);
        Mono<Image> savedImageMono = addImageToDbAndGetMono(item.getImageFile());
        Mono<ItemDto> itemDtoMono = ItemMapper.mapToItemDto(itemMono, savedImageMono);
        Mono<ItemDto> savedItemDto = itemDtoMono.flatMap(itemDto -> itemRepository.save(itemDto));

        //existingItemsDtos.put(savedItemDto.getId(), savedItemDto);
        return savedItemDto;
    }

    private Mono<Image> addImageToDbAndGetMono(FilePart filePart) throws IOException {
        if (filePart == null) {
            return Mono.empty();
        }

        List<byte[]> bytesList = new ArrayList<>();

        Mono<Boolean> booleanFlux = DataBufferUtils.join(filePart.content())
                .map(content ->
                        bytesList.add(content.asByteBuffer().array()));

        Mono<Image> imageMono = booleanFlux
                .flatMap(hasImage -> {
                    if (hasImage) {
                        Image image = new Image(bytesList.get(0));
                        Mono<Image> savedImage = imageRepository.save(image);
                        return savedImage;
                    } else {
                        return Mono.empty();
                    }
                });

        return imageMono;
    }

    public Mono<ItemDto> decreaseItemAmount(int id) {
        //ItemDto itemDto = existingItemsDtos.get(id);

        Mono<ItemDto> itemDtoMono = getItemDto(id);
        itemDtoMono
                .map(itemDto -> {
                    itemDto.decreaseAmount();
                    return itemDto;
                })
                .flatMap(itemDto -> itemRepository.save(itemDto))
                .subscribe();
        return itemDtoMono;
    }

    public Mono<ItemDto> increaseItemAmount(int id) {
        //ItemDto itemDto = existingItemsDtos.get(id);

        Mono<ItemDto> itemDtoMono = getItemDto(id);
        itemDtoMono
                .map(itemDto -> {
                    itemDto.increaseAmount();
                    return itemDto;
                })
                .flatMap(itemDto -> itemRepository.save(itemDto))
                .subscribe();
        return itemDtoMono;
    }

    public Mono<Integer> getItemListSize() {
        return itemRepository.getItemListSize();
    }

    private void addTestItems() throws IOException {
        byte[] imageBytes1 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\armature.txt"));
        Image image1 = new Image(imageBytes1);
        Mono<Image> imageMono1 = imageRepository.save(image1);
        Item item1 = new Item("Арматура", "Арматура для строительства", null, 65_000);
        Mono<ItemDto> itemDto1 = ItemMapper.mapToItemDto(Mono.just(item1), imageMono1)
                .doOnNext(itemDto -> itemDto.setAmount(1))
                .flatMap(itemDto -> itemRepository.save(itemDto));

        byte[] imageBytes2 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\beam.txt"));
        Image image2 = new Image(imageBytes2);
        Mono<Image> imageMono2 = imageRepository.save(image2);
        Item item2 = new Item("Балка", "Балка для перекрытий", null, 130_000);
        Mono<ItemDto> itemDto2 = ItemMapper.mapToItemDto(Mono.just(item2), imageMono2)
                .doOnNext(itemDto -> itemDto.setAmount(5))
                .flatMap(itemDto -> itemRepository.save(itemDto));

        byte[] imageBytes3 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\pipe.txt"));
        Image image3 = new Image(imageBytes3);
        Mono<Image> imageMono3 = imageRepository.save(image3);
        Item item3 = new Item("Труба стальная", "Труба для водо- и газопроводов", null, 70_000);
        Mono<ItemDto> itemDto3 = ItemMapper.mapToItemDto(Mono.just(item3), imageMono3)
                .doOnNext(itemDto -> itemDto.setAmount(3))
                .flatMap(itemDto -> itemRepository.save(itemDto));

        byte[] imageBytes4 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\sheet.txt"));
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

    public boolean isWasTestItemAdded() {
        return wasTestItemAdded;
    }

    public void setWasTestItemAdded(boolean wasTestItemAdded) {
        this.wasTestItemAdded = wasTestItemAdded;
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
