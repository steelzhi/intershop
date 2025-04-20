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

@SpringBootTest(classes = ItemAddingGettingService.class)
public class ItemAddingGettingServiceWithMockedRepoTest {

    @Autowired
    ItemAddingGettingService itemService;

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
}
