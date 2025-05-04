/*
package ru.yandex.practicum.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {ItemAllOtherOpsService.class, ItemGettingFromCacheService.class})
public class ItemAllOtherOpsServiceWithMockedRepoTest {

    @Autowired
    ItemAllOtherOpsService itemsService;

    @Autowired
    ItemGettingFromCacheService itemService;

    @MockitoBean
    ItemRepository itemRepository;

    @MockitoBean
    ImageRepository imageRepository;

    @BeforeEach
    void setUp() throws IOException {
        Mockito.reset(itemRepository);
    }

    @Test
    void testGetItemsList() throws IOException {
        byte[] imageBytes1 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\armature.txt"));
        Image image1 = new Image(imageBytes1);
        when(imageRepository.save(image1))
                .thenReturn(Mono.just(image1));
        Item item1 = new Item("item1", "desc", null, 1.0);
        Mono<ItemDto> itemDtoMono1 = ItemMapper.mapToItemDto(Mono.just(item1), Mono.just(image1))
                .doOnNext(itemDto -> itemDto.setId(1));

        byte[] imageBytes2 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\beam.txt"));
        Image image2 = new Image(imageBytes2);
        when(imageRepository.save(image2))
                .thenReturn(Mono.just(image2));
        Item item2 = new Item("item2", "desc", null, 1.0);
        Mono<ItemDto> itemDtoMono2 = ItemMapper.mapToItemDto(Mono.just(item2), Mono.just(image2))
                .doOnNext(itemDto -> itemDto.setId(2));

        when(itemRepository.getAllItemIdsOnPage(1, 10))
                .thenReturn(Flux.just(1, 2));

        when(itemService.getItemDto(1))
                .thenReturn(itemDtoMono1);

        when(itemService.getItemDto(2))
                .thenReturn(itemDtoMono2);

        Flux<ItemDto> itemDtoFlux1 = itemsService.getItemsList(10, 1);
        itemDtoFlux1.blockLast();
        List<ItemDto> itemDtos = itemDtoFlux1.toStream().toList();

        assertTrue(itemDtos.contains(itemDtoMono1.block()), "Flux should contain itemDto1");
        assertTrue(itemDtos.contains(itemDtoMono2.block()), "Flux should contain itemDto2");

        verify(itemRepository, times(1)).getAllItemIdsOnPage(1, 10);
    }

    @Test
    void testSearchAndOrderById() throws IOException {
        byte[] imageBytes1 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\armature.txt"));
        Image image1 = new Image(imageBytes1);
        when(imageRepository.save(image1))
                .thenReturn(Mono.just(image1));
        Item item1 = new Item("itemDto1", "abcdesc1", null, 1.0);
        Mono<ItemDto> itemDtoMono1 = ItemMapper.mapToItemDto(Mono.just(item1), Mono.just(image1))
                .doOnNext(itemDto -> itemDto.setId(1));

        byte[] imageBytes2 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\beam.txt"));
        Image image2 = new Image(imageBytes2);
        when(imageRepository.save(image2))
                .thenReturn(Mono.just(image2));
        Item item2 = new Item("itemDto2z", "descghy", null, 12.0);
        Mono<ItemDto> itemDtoMono2 = ItemMapper.mapToItemDto(Mono.just(item2), Mono.just(image2))
                .doOnNext(itemDto -> itemDto.setId(2));

        byte[] imageBytes3 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\pipe.txt"));
        Image image3 = new Image(imageBytes3);
        when(imageRepository.save(image3))
                .thenReturn(Mono.just(image3));
        Item item3 = new Item("itemDto3", "desczzy", null, 4.0);
        Mono<ItemDto> itemDtoMono3 = ItemMapper.mapToItemDto(Mono.just(item3), Mono.just(image3))
                .doOnNext(itemDto -> itemDto.setId(3));

        Flux<Integer> itemDtoFluxForOrderById = Flux.just(1, 2, 3);
        when(itemRepository.findIdsByNameOrDescriptionOrderById("itemdto"))
                .thenReturn(itemDtoFluxForOrderById);

        when(itemService.getItemDto(1))
                .thenReturn(itemDtoMono1);

        when(itemService.getItemDto(2))
                .thenReturn(itemDtoMono2);

        when(itemService.getItemDto(3))
                .thenReturn(itemDtoMono3);

        List<ItemDto> searchedItemDtos = itemsService.search("itemdto", SortingCategory.NO)
                .toStream().toList();
        assertTrue(searchedItemDtos.contains(itemDtoMono1.block()), "Flux should contain itemDto1");
        assertTrue(searchedItemDtos.contains(itemDtoMono2.block()), "Flux should contain itemDto2");
        assertTrue(searchedItemDtos.contains(itemDtoMono3.block()), "Flux should contain itemDto3");

        verify(itemRepository, times(1))
                .findIdsByNameOrDescriptionOrderById("itemdto");
    }

    @Test
    void testSearchAndOrderByName() throws IOException {
        byte[] imageBytes1 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\armature.txt"));
        Image image1 = new Image(imageBytes1);
        when(imageRepository.save(image1))
                .thenReturn(Mono.just(image1));
        Item item1 = new Item("itemDto1", "abcdesc1", null, 1.0);
        Mono<ItemDto> itemDtoMono1 = ItemMapper.mapToItemDto(Mono.just(item1), Mono.just(image1))
                .doOnNext(itemDto -> itemDto.setId(1));

        byte[] imageBytes2 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\beam.txt"));
        Image image2 = new Image(imageBytes2);
        when(imageRepository.save(image2))
                .thenReturn(Mono.just(image2));
        Item item2 = new Item("itemDto2z", "descghy", null, 12.0);
        Mono<ItemDto> itemDtoMono2 = ItemMapper.mapToItemDto(Mono.just(item2), Mono.just(image2))
                .doOnNext(itemDto -> itemDto.setId(2));

        byte[] imageBytes3 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\pipe.txt"));
        Image image3 = new Image(imageBytes3);
        when(imageRepository.save(image3))
                .thenReturn(Mono.just(image3));
        Item item3 = new Item("itemDto3", "desczzy", null, 4.0);
        Mono<ItemDto> itemDtoMono3 = ItemMapper.mapToItemDto(Mono.just(item3), Mono.just(image3))
                .doOnNext(itemDto -> itemDto.setId(3));

        Flux<Integer> itemDtoFluxForOrderByName = Flux.just(2, 3);
        when(itemRepository.findIdsByNameOrDescriptionOrderByName("z")).thenReturn(itemDtoFluxForOrderByName);

        when(itemService.getItemDto(1))
                .thenReturn(itemDtoMono1);

        when(itemService.getItemDto(2))
                .thenReturn(itemDtoMono2);

        when(itemService.getItemDto(3))
                .thenReturn(itemDtoMono3);

        List<ItemDto> searchedItemDtos = itemsService.search("z", SortingCategory.ALPHA)
                .toStream().toList();
        assertFalse(searchedItemDtos.contains(itemDtoMono1.block()), "Flux shouldn't contain itemDto1");
        assertTrue(searchedItemDtos.contains(itemDtoMono2.block()), "Flux should contain itemDto2");
        assertTrue(searchedItemDtos.contains(itemDtoMono3.block()), "Flux should contain itemDto3");

        verify(itemRepository, times(1))
                .findIdsByNameOrDescriptionOrderByName("z");
    }

    @Test
    void testSearchAndOrderByPrice() throws IOException {
        byte[] imageBytes1 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\armature.txt"));
        Image image1 = new Image(imageBytes1);
        when(imageRepository.save(image1))
                .thenReturn(Mono.just(image1));
        Item item1 = new Item("itemDto1", "abcdesc1", null, 1.0);
        Mono<ItemDto> itemDtoMono1 = ItemMapper.mapToItemDto(Mono.just(item1), Mono.just(image1))
                .doOnNext(itemDto -> itemDto.setId(1));

        byte[] imageBytes2 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\beam.txt"));
        Image image2 = new Image(imageBytes2);
        when(imageRepository.save(image2))
                .thenReturn(Mono.just(image2));
        Item item2 = new Item("itemDto2z", "descghy", null, 12.0);
        Mono<ItemDto> itemDtoMono2 = ItemMapper.mapToItemDto(Mono.just(item2), Mono.just(image2))
                .doOnNext(itemDto -> itemDto.setId(2));

        byte[] imageBytes3 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\pipe.txt"));
        Image image3 = new Image(imageBytes3);
        when(imageRepository.save(image3))
                .thenReturn(Mono.just(image3));
        Item item3 = new Item("itemDto3", "desczzy", null, 4.0);
        Mono<ItemDto> itemDtoMono3 = ItemMapper.mapToItemDto(Mono.just(item3), Mono.just(image3))
                .doOnNext(itemDto -> itemDto.setId(3));

        Flux<Integer> itemDtoFluxForOrderByName = Flux.just(1, 3, 2);
        when(itemRepository.findIdsByNameOrDescriptionOrderByPrice("DTO"))
                .thenReturn(itemDtoFluxForOrderByName);

        when(itemService.getItemDto(1))
                .thenReturn(itemDtoMono1);

        when(itemService.getItemDto(2))
                .thenReturn(itemDtoMono2);

        when(itemService.getItemDto(3))
                .thenReturn(itemDtoMono3);

        List<ItemDto> searchedItemDtos = itemsService.search("DTO", SortingCategory.PRICE)
                .toStream().toList();
        assertTrue(searchedItemDtos.size() == 3, "List size should be 3");
        assertEquals(searchedItemDtos.get(1), itemDtoMono3.block(), "On 2nd place in list should be itemDto3");

        verify(itemRepository, times(1))
                .findIdsByNameOrDescriptionOrderByPrice("DTO");
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

        itemsService.addItem(item)
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
        ItemDto itemDtoDecreased = itemsService.decreaseItemAmount(id).block();
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

        ItemDto itemDtoIncreased = itemsService.increaseItemAmount(id).block();
        assertEquals(amount + 1, itemDtoIncreased.getAmount(), "Incorrect amount increasing");

        verify(itemRepository, times(1)).findById(id);
    }
}
*/
