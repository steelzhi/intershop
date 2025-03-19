package ru.yandex.practicum.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.dao.CartRepository;
import ru.yandex.practicum.dao.ImageRepository;
import ru.yandex.practicum.dao.ItemRepository;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.enums.SortingCategory;
import ru.yandex.practicum.mapper.ItemMapper;
import ru.yandex.practicum.model.CartItem;
import ru.yandex.practicum.model.Image;
import ru.yandex.practicum.model.Item;
import ru.yandex.practicum.service.ItemService;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Sql("classpath:test-schema.sql")
public class ItemControllerAllLayersTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ItemService itemService;

    @AfterEach
    void clearDb() {
        itemRepository.deleteAll();
    }

    @Test
    void getItemsList() throws Exception {
        ItemDto itemDto1 = new ItemDto("itemDto1", "desc1", null, 1.0, 2);
        ItemDto itemDto2 = new ItemDto("itemDto2", "desc2", null, 2.0, 3);
        itemRepository.save(itemDto1);
        itemRepository.save(itemDto2);

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
    }

    @Test
    void getItemsListSplittedByPages() throws Exception {
        ItemDto itemDto1 = new ItemDto("itemDto1", "desc1", null, 1.0, 2);
        ItemDto itemDto2 = new ItemDto("itemDto2", "desc2", null, 2.0, 3);
        itemRepository.save(itemDto1);
        itemRepository.save(itemDto2);

        mockMvc.perform(get("/main/items")
                        .param("itemsOnPage", "2")
                        .param("pageNumber", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("pages"));
    }

    @Test
    void getItemDto() throws Exception {
        byte[] imageBytes = Files.readAllBytes(Paths.get("src/test/resources/images-bytes/pipe.txt"));
        Image image = new Image(imageBytes);

        ItemDto itemDto1 = new ItemDto("itemDto1", "desc1", image, 1.0, 2);
        ItemDto itemDto = itemRepository.save(itemDto1);

        itemService.getExistingItemsDtos().put(itemDto.getId(), itemDto);

        mockMvc.perform(get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("item"))
                .andExpect(model().attributeExists("itemDto"));
    }

    @Test
    void search() throws Exception {
        ItemDto itemDto1 = new ItemDto("itemDto1", "desc1", null, 1.0, 2);
        itemRepository.save(itemDto1);

        mockMvc.perform(get("/search")
                        .param("key", "Dto")
                        .param("sortingCategory", "ALPHA"))
                .andExpect(status().isOk())
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("items"));
    }


    @Test
    void addItemToList_shouldAddItemAndReturnItemDto() throws Exception {
        Item item = new Item("Item", "Desc", null, 1.0);
        ItemDto itemDto = ItemMapper.mapToItemDto(item);
        itemRepository.save(itemDto);

        mockMvc.perform(post("/item")
                        .param("name", "Item")
                        .param("description", "Desc")
                        .param("price", "1.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/main/items"));

        Optional<ItemDto> savedItemDto = itemRepository.findById(1);
        assertEquals(savedItemDto.get().getName(), itemDto.getName(), "After saving to Db name was changed");
    }

    @Test
    void decreaseItemAmount_shouldReturnItemWithAmountDecreased() throws Exception {
        ItemDto itemDto1 = new ItemDto("DE", "zzz", null, 11.0, 2);
        itemRepository.save(itemDto1);
        itemService.getExistingItemsDtos().put(itemDto1.getId(), itemDto1);
        itemDto1.setAmount(itemDto1.getAmount() - 1);

        mockMvc.perform(post("/item/1/minus")
                        .param("pageName", "MAIN"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/main/items"));

        Optional<ItemDto> itemDtoWithAmountDecreased = itemRepository.findById(1);
        assertTrue(itemDtoWithAmountDecreased.get().getAmount() == itemDto1.getAmount(), "Amount was decreased incorrectly");
        itemService.getExistingItemsDtos().clear();
    }

    @Test
    void increaseItemAmount_shouldReturnItemWithAmountIncreased() throws Exception {
        ItemDto itemDto1 = new ItemDto("DE", "zzz", null, 11.0, 2);
        itemRepository.save(itemDto1);
        itemService.getExistingItemsDtos().put(itemDto1.getId(), itemDto1);
        itemDto1.setAmount(itemDto1.getAmount() + 1);

        mockMvc.perform(post("/item/1/plus")
                        .param("pageName", "ITEM"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items/1"));

        Optional<ItemDto> itemDtoWithAmountDecreased = itemRepository.findById(1);
        assertTrue(itemDtoWithAmountDecreased.get().getAmount() == itemDto1.getAmount(), "Amount was increased incorrectly");
        itemService.getExistingItemsDtos().clear();
    }
}
