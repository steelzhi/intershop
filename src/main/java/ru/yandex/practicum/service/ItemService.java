package ru.yandex.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
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

@Service
public class ItemService {
    // Для снижения обращений к БД будем также хранить текущий список товаров в кэше
    //private Map<Integer, ItemDto> existingItemsDtos = new HashMap<>();

    // Нужно для теста
    private boolean wasTestItemAdded;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ImageRepository imageRepository;

    public Flux<ItemDto> getItemsList(int itemsOnPage, int pageNumber) throws IOException {

        // Добавим тестовые товары
        /*if (!wasTestItemAdded) {
            byte[] imageBytes1 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\armature.txt"));
            Image image1 = new Image(imageBytes1);
            Mono<Image> imageMono1 = imageRepository.save(image1);
            Item item1 = new Item("Арматура", "Арматура для строительства", null, 65_000);
            ItemDto itemDto1 = ItemMapper.mapToItemDto(item1, imageMono1);
            itemDto1.setAmount(1);
            itemRepository.save(itemDto1).subscribe();

            byte[] imageBytes2 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\beam.txt"));
            Image image2 = new Image(imageBytes2);
            Mono<Image> imageMono2 = imageRepository.save(image2);
            Item item2 = new Item("Балка", "Балка для перекрытий", null, 130_000);
            ItemDto itemDto2 = ItemMapper.mapToItemDto(item2, imageMono2);
            itemDto2.setAmount(5);
            itemRepository.save(itemDto2).subscribe();

            wasTestItemAdded = true;
        }*/

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
            case NO -> itemRepository.findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingOrderById(key, key);
            case ALPHA -> itemRepository.findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingOrderByName(key, key);
            case PRICE -> itemRepository.findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingOrderByPrice(key, key);
        };

        return itemDtos;
    }

    public Mono<ItemDto> addItem(Item item) throws IOException {
        Mono<Item> itemMono = Mono.just(item);
        Mono<Image> savedImageMono = addImageToDbAndGetMono(item.getImageFile());
/*      Mono<ItemDto> itemDtoMono = ItemMapper.mapToItemDto(itemMono, savedImageMono);
        Mono<ItemDto> savedItemDto = itemDtoMono.flatMap(itemDto -> itemRepository.save(itemDto));*/
        ItemDto itemDtoMono = ItemMapper.mapToItemDto(itemMono.block(), savedImageMono);
        Mono<ItemDto> savedItemDto = itemRepository.save(itemDtoMono);

        //existingItemsDtos.put(savedItemDto.getId(), savedItemDto);
        return savedItemDto;
    }

    private Mono<Image> addImageToDbAndGetMono(MultipartFile imageFile) throws IOException {
        if (imageFile == null) {
            return Mono.empty();
        }

        Mono<MultipartFile> multipartFileMono = Mono.just(imageFile);
        Mono<Image> imageMono = multipartFileMono.hasElement()
                .flatMap(hasMultipartFile -> {
                    if (hasMultipartFile) {
                        byte[] imageBytes;
                        try {
                            imageBytes = imageFile.getBytes();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        Image image = new Image(imageBytes);
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
