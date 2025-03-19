package ru.yandex.practicum.controller;

import org.aspectj.weaver.ast.Or;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.dao.*;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.mapper.ItemMapper;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.service.CartService;
import ru.yandex.practicum.service.ItemService;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Sql("classpath:test-schema.sql")
public class OrderControllerAllLayersTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    CartService cartService;

    @AfterEach
    void clearDb() {
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        cartRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    void createNotEmptyOrder_shouldReturnCreatedOrder() throws Exception {
        ItemDto itemDto = new ItemDto("itemDto", "desc", null, 1.0, 2);
        ItemDto savedItemDto = itemRepository.save(itemDto);
        cartService.addItemToCart(savedItemDto.getId());

        mockMvc.perform(post("/createOrder"))
                .andExpect(status().isOk())
                .andExpect(view().name("order"))
                .andExpect(model().attributeExists("order"));
    }

    @Test
    void createEmptyOrder_shouldRedirect() throws Exception {
        mockMvc.perform(post("/createOrder"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/main/items"));
    }

    @Test
    void getOrders() throws Exception {
        ItemDto itemDto = new ItemDto("itemDto", "desc", null, 1.0, 2);
        ItemDto savedItemDto = itemRepository.save(itemDto);
        cartService.addItemToCart(savedItemDto.getId());

        mockMvc.perform(post("/createOrder"))
                .andExpect(status().isOk())
                .andExpect(view().name("order"))
                .andExpect(model().attributeExists("order"));

        ItemDto itemDto2 = new ItemDto("itemDto2", "desc", null, 1.0, 2);
        ItemDto savedItemDto2 = itemRepository.save(itemDto2);
        cartService.addItemToCart(savedItemDto2.getId());

        mockMvc.perform(post("/createOrder"))
                .andExpect(status().isOk())
                .andExpect(view().name("order"))
                .andExpect(model().attributeExists("order"));

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders"))
                .andExpect(model().attributeExists("orders"));
    }

    @Test
    void getOrder() throws Exception {
        ItemDto itemDto = new ItemDto("itemDto", "desc", null, 1.0, 2);
        ItemDto savedItemDto = itemRepository.save(itemDto);
        cartService.addItemToCart(savedItemDto.getId());

        mockMvc.perform(post("/createOrder"))
                .andExpect(status().isOk())
                .andExpect(view().name("order"))
                .andExpect(model().attributeExists("order"));

        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("order"))
                .andExpect(model().attributeExists("order"));
    }
}
