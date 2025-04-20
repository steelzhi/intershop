package ru.yandex.practicum.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dao.ImageRepository;
import ru.yandex.practicum.dao.ItemRepository;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.mapper.ItemMapper;
import ru.yandex.practicum.model.Image;
import ru.yandex.practicum.model.Item;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = ItemService.class)
public class ItemServiceWithMockedRepoTest {

    @Autowired
    ItemService itemService;

    @MockitoBean
    ItemRepository itemRepository;

    @MockitoBean
    ImageRepository imageRepository;

    @BeforeEach
    void setUp() {
        Mockito.reset(itemRepository);
    }

    @Test
    void testAddItem() throws IOException {
        byte[] imageBytes1 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\armature.txt"));
        Image image1 = new Image(imageBytes1);
        Mono<Image> imageMono = Mono.just(image1);
        when(imageRepository.save(image1))
                .thenReturn(imageMono);

        Item item = new Item("item", "desc", null, 1.0);
        Mono<ItemDto> itemDtoMono = ItemMapper.mapToItemDto(Mono.just(item), imageMono);
        ItemDto itemDto = itemDtoMono.block();
        when(itemRepository.save(itemDto))
                .thenReturn(itemDtoMono);

        itemService.addItem(item)
                .doOnNext(itemDto1 -> Assertions.assertThat(itemDto1)
                        .isNotNull()
                        .isEqualTo(itemDto)
                ).block();
    }

    @Test
    void testDecreaseItemAmount() throws IOException {
        byte[] imageBytes1 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\armature.txt"));
        Image image1 = new Image(imageBytes1);
        Mono<Image> imageMono = Mono.just(image1);
        when(imageRepository.save(image1))
                .thenReturn(imageMono);

        int id = 1;
        Item item = new Item("item", "desc", null, 1.0);
        Mono<ItemDto> itemDtoMono = ItemMapper.mapToItemDto(Mono.just(item), imageMono);
        int amount = 1;
        ItemDto itemDto = itemDtoMono.doOnNext(itemDto1 -> itemDto1.setAmount(amount))
                .doOnNext(itemDto1 -> itemDto1.setId(id))
                .block();

        when(itemRepository.findById(id))
                .thenReturn(Mono.just(itemDto));
        ItemDto itemDtoDecreased = itemService.decreaseItemAmount(id).block();
        assertEquals(amount - 1, itemDtoDecreased.getAmount(), "Incorrect amount decreasing");

        verify(itemRepository, times(1)).findById(id);
    }

    @Test
    void testIncreaseItemAmount() throws IOException {
        byte[] imageBytes1 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\armature.txt"));
        Image image1 = new Image(imageBytes1);
        Mono<Image> imageMono = Mono.just(image1);
        when(imageRepository.save(image1))
                .thenReturn(imageMono);

        int id = 1;
        Item item = new Item("item", "desc", null, 1.0);
        Mono<ItemDto> itemDtoMono = ItemMapper.mapToItemDto(Mono.just(item), imageMono);
        int amount = 1;
        ItemDto itemDto = itemDtoMono.doOnNext(itemDto1 -> itemDto1.setAmount(amount))
                .doOnNext(itemDto1 -> itemDto1.setId(id))
                .block();

        when(itemRepository.findById(id))
                .thenReturn(Mono.just(itemDto));
        ItemDto itemDtoIncreased = itemService.increaseItemAmount(id).block();
        assertEquals(amount + 1, itemDtoIncreased.getAmount(), "Incorrect amount increasing");

        verify(itemRepository, times(1)).findById(id);
    }
}
