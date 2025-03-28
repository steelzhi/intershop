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
import ru.yandex.practicum.mapper.ItemMapper;
import ru.yandex.practicum.model.Image;
import ru.yandex.practicum.model.Item;

import java.io.IOException;

@Service
public class ItemService {
    // Для снижения обращений к БД будем также хранить текущий список товаров в кэше
    //private Map<Integer, ItemDto> existingItemsDtos = new HashMap<>();

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ImageRepository imageRepository;

    public Flux<ItemDto> getItemsList(int itemsOnPage, int pageNumber) {
        PageRequest page = PageRequest.of(pageNumber - 1, itemsOnPage);

        Flux<ItemDto> allItems = /*itemRepository.findAllByOrderById(page);*/ itemRepository.findAll();
        return allItems;
    }

   /* public ItemDto getItemDto(int id) {
        return existingItemsDtos.get(id);
    }*/

   /* public List<ItemDto> search(String key, SortingCategory sortingCategory) {
        List<ItemDto> itemDtos = null;

        switch (sortingCategory) {
            case NO -> itemDtos
                    = itemRepository.findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingOrderById(key, key);
            case ALPHA -> itemDtos
                    = itemRepository.findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingOrderByName(key, key);
            case PRICE -> itemDtos
                    = itemRepository.findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingOrderByPrice(key, key);
        }

        return itemDtos;
    }*/

    public Mono<ItemDto> addItem(Item item) throws IOException {
        Mono<Image> savedImage = addImageToDbAndGetMono(item.getImageFile());
        ItemDto itemDto = ItemMapper.mapToItemDto(item, savedImage);
        Mono<ItemDto> savedItemDto = itemRepository.save(itemDto);

        //existingItemsDtos.put(savedItemDto.getId(), savedItemDto);
        return savedItemDto;
    }

    private Mono<Image> addImageToDbAndGetMono(MultipartFile imageFile) throws IOException {
        if (imageFile == null) {
            return Mono.just(null);
        } else {
            byte[] imageBytes = imageFile.getBytes();
            Image image = new Image(imageBytes);
            Mono<Image> savedImage = imageRepository.save(image);
            return savedImage;
        }
    }

   /* public ItemDto decreaseItemAmount(@PathVariable int id) {
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
    }*/

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
