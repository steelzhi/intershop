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
import ru.yandex.practicum.dao.ItemRepository;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.model.CartItem;
import ru.yandex.practicum.service.ItemService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Sql("classpath:test-schema.sql")
public class CartControllerAllLayersTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ItemService itemService;

    @AfterEach
    void clearDb() {
        cartRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    void addItemToCart_shouldAddItemt() throws Exception {
        ItemDto itemDto = new ItemDto("itemDto", "desc", null, 1.0, 2);
        itemRepository.save(itemDto);
        Optional<ItemDto> savedItemDto = itemRepository.findById(1);
        CartItem cartItem = new CartItem(1, savedItemDto.get());

        mockMvc.perform(post("/cart/add/1")
                        .param("id", String.valueOf(savedItemDto.get().getId()))
                        .param("name", itemDto.getName())
                        .param("description", itemDto.getDescription())
                        .param("price", String.valueOf(itemDto.getPrice()))
                        .param("amount", String.valueOf(itemDto.getAmount())));


        Optional<CartItem> savedCartItem = cartRepository.findById(1);
        assertTrue(savedCartItem.isPresent(), "cartItem should exist in Db");
    }

    @Test
    void removeItem_shouldRemoveItemFromCart() throws Exception {
        ItemDto itemDto = new ItemDto("itemDto", "desc", null, 1.0, 2);
        ItemDto savedItemDto = itemRepository.save(itemDto);
        CartItem cartItem = new CartItem(savedItemDto);
        cartRepository.save(cartItem);

        mockMvc.perform(post("/cart/add/1")
                .param("id", String.valueOf(savedItemDto.getId()))
                .param("name", itemDto.getName())
                .param("description", itemDto.getDescription())
                .param("price", String.valueOf(itemDto.getPrice()))
                .param("amount", String.valueOf(itemDto.getAmount())));

        itemService.getExistingItemsDtos().put(savedItemDto.getId(), savedItemDto);

        mockMvc.perform(post("/cart/remove/1")
                .param("pageName", "MAIN"));

        Optional<CartItem> savedCartItem = cartRepository.findById(1);
        assertTrue(savedCartItem.isEmpty(), "cartItem shouldn't exist in Db");

        itemService.getExistingItemsDtos().clear();
    }

    @Test
    void getCart() throws Exception {
        ItemDto itemDto = new ItemDto("itemDto", "desc", null, 1.0, 2);
        ItemDto savedItemDto = itemRepository.save(itemDto);
        CartItem cartItem = new CartItem(savedItemDto);
        cartRepository.save(cartItem);

        ItemDto itemDto2 = new ItemDto("itemDto2", "desc", null, 1.0, 2);
        ItemDto savedItemDto2 = itemRepository.save(itemDto2);
        CartItem cartItem2 = new CartItem(savedItemDto2);
        cartRepository.save(cartItem2);

        mockMvc.perform(get("/cart/items"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("cart"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("total"));
    }
}
