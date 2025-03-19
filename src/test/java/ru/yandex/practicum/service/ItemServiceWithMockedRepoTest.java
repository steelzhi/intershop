package ru.yandex.practicum.service;

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
import ru.yandex.practicum.dao.CartRepository;
import ru.yandex.practicum.dao.ItemRepository;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.enums.SortingCategory;
import ru.yandex.practicum.mapper.ItemMapper;
import ru.yandex.practicum.model.CartItem;
import ru.yandex.practicum.model.Item;

import java.io.IOException;
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

    @BeforeEach
    void setUp() {
        Mockito.reset(itemRepository);
    }

    @Test
    void testGetItemsList() throws IOException {
        ItemDto itemDto1 = new ItemDto("itemDto1", "desc1", null, 1.0, 2);
        ItemDto itemDto2 = new ItemDto("itemDto2", "desc2", null, 2.0, 3);
        List<ItemDto> itemDtos = List.of(itemDto1, itemDto2);
        PageRequest page = PageRequest.of(0, 5);
        Page<ItemDto> allItemsPages = new PageImpl<>(itemDtos);
        when(itemRepository.findAllByOrderById(page))
                .thenReturn(allItemsPages);

        List<ItemDto> itemDtosFromDao = itemService.getItemsList(5, 1);
        assertTrue(!itemDtosFromDao.isEmpty(), "List shouldn't be empty");
        assertEquals(itemDtos, itemDtosFromDao, "Lists should be equal");

        verify(itemRepository, times(1)).findAllByOrderById(page);
    }

    @Test
    void testSearchWithDifferentSorting() {
        ItemDto itemDto1 = new ItemDto("itemDto1", "abcdesc1", null, 1.0, 2);
        ItemDto itemDto2 = new ItemDto("itemDto2z", "descghy", null, 12.0, 2);
        ItemDto itemDto3 = new ItemDto("itemDto3", "desczzy", null, 4.0, 5);

        when(itemRepository
                .findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingOrderById(
                        "itemdto", "itemdto"))
                .thenReturn(List.of(itemDto1, itemDto2, itemDto3));

        List<ItemDto> seachedItemDtos1 = itemService.search("itemdto", SortingCategory.NO);
        assertTrue(!seachedItemDtos1.isEmpty(), "List shouldn't be empty");
        assertTrue(seachedItemDtos1.contains(itemDto1), "Found items list doesn't contain itemDto1");

        verify(itemRepository, times(1))
                .findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingOrderById(
                        "itemdto", "itemdto");

        when(itemRepository
                .findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingOrderByName("z", "z"))
                .thenReturn(List.of(itemDto2, itemDto3));

        List<ItemDto> seachedItemDtos2 = itemService.search("z", SortingCategory.ALPHA);
        assertTrue(seachedItemDtos2.size() == 2, "List size should be 2");
        assertTrue(seachedItemDtos2.contains(itemDto3), "Found items list doesn't contain itemDto3");

        verify(itemRepository, times(1))
                .findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingOrderByName("z", "z");

        when(itemRepository
                .findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingOrderByPrice(
                        "DTO", "DTO"))
                .thenReturn(List.of(itemDto1, itemDto3, itemDto2));

        List<ItemDto> seachedItemDtos3 = itemService.search("DTO", SortingCategory.PRICE);
        assertTrue(seachedItemDtos3.size() == 3, "List size should be 3");
        assertEquals(seachedItemDtos1.get(1), itemDto3, "On 2nd place in list should be itemDto3");

        verify(itemRepository, times(1))
                .findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingOrderByPrice(
                        "DTO", "DTO");
    }

    @Test
    void testAddItem() throws IOException {
        Item item = new Item("item", "desc", null, 1.0);
        ItemDto itemDto = ItemMapper.mapToItemDto(item);
        when(itemRepository.save(itemDto))
                .thenReturn(itemDto);

        ItemDto savedItemDto = itemService.addItem(item);
        assertEquals(itemDto, savedItemDto, "Incorrect itemDto saving");

        verify(itemRepository, times(1)).save(itemDto);
    }

    @Test
    void testDecreaseItemAmount() {
        ItemDto itemDto1 = new ItemDto("itemDto1", "abcdesc1", null, 1.0, 2);
        itemDto1.setId(0);

        when(itemRepository.save(itemDto1))
                .thenReturn(itemDto1);
        itemService.getExistingItemsDtos().put(itemDto1.getId(), itemDto1);
        ItemDto savedItemDto = itemService.decreaseItemAmount(itemDto1.getId());

        assertEquals(savedItemDto.getAmount(), itemDto1.getAmount(), "Incorrect amount decreasing");

        verify(itemRepository, times(1)).save(itemDto1);
    }

    @Test
    void testIncreaseItemAmount() {
        ItemDto itemDto1 = new ItemDto("itemDto1", "abcdesc1", null, 1.0, 2);
        itemDto1.setId(0);

        when(itemRepository.save(itemDto1))
                .thenReturn(itemDto1);
        itemService.getExistingItemsDtos().put(itemDto1.getId(), itemDto1);
        ItemDto savedItemDto = itemService.increaseItemAmount(itemDto1.getId());

        assertEquals(savedItemDto.getAmount(), itemDto1.getAmount(), "Incorrect amount increasing");

        verify(itemRepository, times(1)).save(itemDto1);
    }
}
