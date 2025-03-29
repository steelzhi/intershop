/*
package ru.yandex.practicum.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.enums.SortingCategory;
import ru.yandex.practicum.mapper.ItemMapper;
import ru.yandex.practicum.model.Item;
import ru.yandex.practicum.service.ItemService;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
public class ItemControllerWebLayerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ItemService itemService;

    @Test
    void getItemsList_shouldReturnItemsList() throws Exception {
        ItemDto itemDto1 = new ItemDto("itemDto1", "desc1", null, 1.0, 2);
        ItemDto itemDto2 = new ItemDto("itemDto2", "desc2", null, 2.0, 3);
        List<ItemDto> itemDtoList = List.of(itemDto1, itemDto2);
        when(itemService.getItemsList(10, 1))
                .thenReturn(itemDtoList);
        when(itemService.getItemListSize())
                .thenReturn(itemDtoList.size());

        mockMvc.perform(get("/main/items"))
                .andExpect(status().isOk())
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("pages"));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("pages"));

        verify(itemService, times(2)).getItemsList(10, 1);
        verify(itemService, times(2)).getItemListSize();
    }

    @Test
    void getItemsListSplittedByPages_shouldReturnSplittedItemsList() throws Exception {
        ItemDto itemDto1 = new ItemDto("itemDto1", "desc1", null, 1.0, 2);
        ItemDto itemDto2 = new ItemDto("itemDto2", "desc2", null, 2.0, 3);
        List<ItemDto> itemDtoList = List.of(itemDto1, itemDto2);
        when(itemService.getItemsList(2, 1))
                .thenReturn(itemDtoList);
        when(itemService.getItemListSize())
                .thenReturn(itemDtoList.size());

        mockMvc.perform(get("/main/items")
                        .param("itemsOnPage", "2")
                        .param("pageNumber", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("pages"));

        verify(itemService, times(1)).getItemsList(2, 1);
        verify(itemService, times(1)).getItemListSize();
    }

    @Test
    void getItemDto_shouldReturnItemDto() throws Exception {
        ItemDto itemDto1 = new ItemDto("itemDto1", "desc1", null, 1.0, 2);
        when((itemService.getItemDto(anyInt())))
                .thenReturn(itemDto1);

        mockMvc.perform(get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("item"))
                .andExpect(model().attributeExists("itemDto"));

        verify(itemService, times(1)).getItemDto(anyInt());
    }

    @Test
    void search_shouldReturnFoundItems() throws Exception {
        ItemDto itemDto1 = new ItemDto("DE", "zzz", null, 11.0, 2);
        ItemDto itemDto2 = new ItemDto("itemDto3", "def", null, 1.0, 2);

        when(itemService.search("de", SortingCategory.PRICE))
                .thenReturn(List.of(itemDto2, itemDto1));

        mockMvc.perform(get("/search")
                        .param("key", "de")
                        .param("sortingCategory", "PRICE"))
                .andExpect(status().isOk())
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("items"));

        verify(itemService, times(1)).search("de", SortingCategory.PRICE);
    }

    @Test
    void addItemToList_shouldAddItemAndReturnItemDto() throws Exception {
        Item item = new Item("Item", "Desc", null, 1.0);
        ItemDto itemDto = ItemMapper.mapToItemDto(item);
        when(itemService.addItem(item))
                .thenReturn(itemDto);

        mockMvc.perform(post("/item")
                        .param("name", "Item")
                        .param("description", "Desc")
                        .param("price", "1.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/main/items"));

        verify(itemService, times(1)).addItem(item);
    }

    @Test
    void decreaseItemAmount_shouldReturnItemWithAmountDecreased() throws Exception {
        ItemDto itemDto1 = new ItemDto("DE", "zzz", null, 11.0, 2);
        itemDto1.setId(1);
        itemDto1.setAmount(2);
        ItemDto itemDto2 = new ItemDto("DE", "zzz", null, 11.0, 2);
        itemDto2.setAmount(1);
        itemDto2.setId(1);
        when(itemService.decreaseItemAmount(1))
                .thenReturn(itemDto2);

        mockMvc.perform(post("/item/1/minus")
                .param("pageName", "MAIN"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/main/items"));

        verify(itemService, times(1)).decreaseItemAmount(1);
    }

    @Test
    void increaseItemAmount_shouldReturnItemWithAmountIncreased() throws Exception {
        ItemDto itemDto1 = new ItemDto("DE", "zzz", null, 11.0, 2);
        itemDto1.setId(1);
        itemDto1.setAmount(1);
        ItemDto itemDto2 = new ItemDto("DE", "zzz", null, 11.0, 2);
        itemDto2.setAmount(1);
        itemDto2.setId(2);
        when(itemService.decreaseItemAmount(1))
                .thenReturn(itemDto2);

        mockMvc.perform(post("/item/1/plus")
                        .param("pageName", "CART"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart/items"));

        verify(itemService, times(1)).increaseItemAmount(1);
    }
}
*/
