package ru.yandex.practicum.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dao.CartRepository;
import ru.yandex.practicum.dao.ImageRepository;
import ru.yandex.practicum.dao.ItemRepository;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.enums.SortingCategory;
import ru.yandex.practicum.mapper.ItemMapper;
import ru.yandex.practicum.model.CartItem;
import ru.yandex.practicum.model.Image;
import ru.yandex.practicum.model.Item;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        ItemDto itemDto1 = ItemMapper.mapToItemDto(item1, Mono.just(image1));

        byte[] imageBytes2 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\beam.txt"));
        Image image2 = new Image(imageBytes2);
        when(imageRepository.save(image2))
                .thenReturn(Mono.just(image2));
        Item item2 = new Item("item2", "desc", null, 1.0);
        ItemDto itemDto2 = ItemMapper.mapToItemDto(item2, Mono.just(image2));

        PageRequest page = PageRequest.of(0, 10);

        when(itemRepository.findAllByOrderById(page))
                .thenReturn(Flux.fromIterable(List.of(itemDto1, itemDto2)));

        Iterable<ItemDto> itemDtos = itemService.getItemsList(10, 1)
                .toIterable();
        List<ItemDto> itemDtos1 = new ArrayList<>();
        itemDtos.forEach(itemDtos1::add);
        assertTrue(itemDtos1.contains(itemDto1), "Flux should contain itemDto1");
        assertTrue(itemDtos1.contains(itemDto2), "Flux should contain itemDto2");

        verify(itemRepository, times(1)).findAllByOrderById(page);
    }

    @Test
    void testSearchWithDifferentSorting() throws IOException {
        byte[] imageBytes1 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\armature.txt"));
        Image image1 = new Image(imageBytes1);
        when(imageRepository.save(image1))
                .thenReturn(Mono.just(image1));
        Item item1 = new Item("itemDto1", "abcdesc1", null, 1.0);
        ItemDto itemDto1 = ItemMapper.mapToItemDto(item1, Mono.just(image1));

        byte[] imageBytes2 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\beam.txt"));
        Image image2 = new Image(imageBytes2);
        when(imageRepository.save(image2))
                .thenReturn(Mono.just(image2));
        Item item2 = new Item("itemDto2z", "descghy", null, 12.0);
        ItemDto itemDto2 = ItemMapper.mapToItemDto(item2, Mono.just(image2));

        byte[] imageBytes3 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\pipe.txt"));
        Image image3 = new Image(imageBytes3);
        when(imageRepository.save(image3))
                .thenReturn(Mono.just(image3));
        Item item3 = new Item("itemDto3", "desczzy", null, 4.0);
        ItemDto itemDto3 = ItemMapper.mapToItemDto(item3, Mono.just(image3));

        when(itemRepository.findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingOrderById(
                "itemdto", "itemdto"))
                .thenReturn(Flux.fromIterable(List.of(itemDto1, itemDto2, itemDto3)));

        Iterable<ItemDto> seachedItemDtos1 = itemService.search("itemdto", SortingCategory.NO)
                .toIterable();
        List<ItemDto> seachedItemDtos1List = new ArrayList<>();
        seachedItemDtos1.forEach(seachedItemDtos1List::add);
        assertTrue(seachedItemDtos1List.contains(itemDto1), "Flux should contain itemDto1");
        assertTrue(seachedItemDtos1List.contains(itemDto2), "Flux should contain itemDto2");
        assertTrue(seachedItemDtos1List.contains(itemDto3), "Flux should contain itemDto3");

        verify(itemRepository, times(1))
                .findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingOrderById(
                        "itemdto", "itemdto");


        when(itemRepository.findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingOrderByName("z", "z"))
                .thenReturn(Flux.fromIterable(List.of(itemDto2, itemDto3)));

        Iterable<ItemDto> seachedItemDtos2 = itemService.search("z", SortingCategory.ALPHA)
                .toIterable();
        List<ItemDto> seachedItemDtos2List = new ArrayList<>();
        seachedItemDtos2.forEach(seachedItemDtos2List::add);
        assertTrue(seachedItemDtos2List.size() == 2, "List size should be 2");
        assertTrue(seachedItemDtos2List.contains(itemDto3), "Flux should contain itemDto3");

        verify(itemRepository, times(1))
                .findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingOrderByName("z", "z");


        when(itemRepository.findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingOrderByPrice(
                "DTO", "DTO"))
                .thenReturn(Flux.fromIterable(List.of(itemDto1, itemDto3, itemDto2)));

        Iterable<ItemDto> seachedItemDtos3 = itemService.search("DTO", SortingCategory.PRICE)
                .toIterable();
        List<ItemDto> seachedItemDtos3List = new ArrayList<>();
        seachedItemDtos3.forEach(seachedItemDtos3List::add);
        assertTrue(seachedItemDtos3List.size() == 3, "List size should be 3");
        assertEquals(seachedItemDtos3List.get(1), itemDto3, "On 2nd place in list should be itemDto3");

        verify(itemRepository, times(1))
                .findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingOrderByPrice(
                        "DTO", "DTO");
    }

    @Test
    void testAddItem() throws IOException {
        byte[] imageBytes1 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\armature.txt"));
        Image image1 = new Image(imageBytes1);
        when(imageRepository.save(image1))
                .thenReturn(Mono.just(image1));

        Item item = new Item("item", "desc", null, 1.0);
        ItemDto itemDto = ItemMapper.mapToItemDto(item, Mono.just(image1));
        when(itemRepository.save(itemDto))
                .thenReturn(Mono.just(itemDto));

        itemService.addItem(item)
                .doOnNext(itemDto1 -> Assertions.assertThat(itemDto1)
                        .isNotNull()
                        .isEqualTo(itemDto)
                ).block();

        verify(itemRepository, times(1)).save(itemDto);
    }

    @Test
    void testDecreaseItemAmount() throws IOException {
        byte[] imageBytes1 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\armature.txt"));
        Image image1 = new Image(imageBytes1);
        when(imageRepository.save(image1))
                .thenReturn(Mono.just(image1));

        Item item = new Item("item", "desc", null, 1.0);
        ItemDto itemDto = ItemMapper.mapToItemDto(item, Mono.just(image1));
        itemDto.setId(1);
        int amount = 1;
        itemDto.setAmount(amount);
        ItemDto itemDtoDecreased = ItemMapper.mapToItemDto(item, Mono.just(image1));
        itemDtoDecreased.setId(1);
        itemDtoDecreased.setAmount(itemDto.getAmount() - 1);

        when(itemRepository.findById(1))
                .thenReturn(Mono.just(itemDto));
        when(itemRepository.save(itemDtoDecreased))
                .thenReturn(Mono.just(itemDtoDecreased));
        Mono<ItemDto> itemDtoMonoDecreased = itemService.decreaseItemAmount(1);
        assertEquals(amount - 1, itemDtoMonoDecreased.block().getAmount(), "Incorrect amount decreasing");

        verify(itemRepository, times(1)).findById(1);
    }

    @Test
    void testIncreaseItemAmount() throws IOException {
        byte[] imageBytes1 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\armature.txt"));
        Image image1 = new Image(imageBytes1);
        when(imageRepository.save(image1))
                .thenReturn(Mono.just(image1));

        Item item = new Item("item", "desc", null, 1.0);
        ItemDto itemDto = ItemMapper.mapToItemDto(item, Mono.just(image1));
        itemDto.setId(1);
        int amount = 1;
        itemDto.setAmount(amount);
        ItemDto itemDtoIncreased = ItemMapper.mapToItemDto(item, Mono.just(image1));
        itemDtoIncreased.setId(1);
        itemDtoIncreased.setAmount(itemDto.getAmount() - 1);

        when(itemRepository.findById(1))
                .thenReturn(Mono.just(itemDto));
        when(itemRepository.save(itemDtoIncreased))
                .thenReturn(Mono.just(itemDtoIncreased));
        Mono<ItemDto> itemDtoMonoIncreased = itemService.increaseItemAmount(1);
        assertEquals(amount + 1, itemDtoMonoIncreased.block().getAmount(), "Incorrect amount increasing");

        verify(itemRepository, times(1)).findById(1);
    }
}
