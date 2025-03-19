package ru.yandex.practicum.controller;

import org.aspectj.weaver.ast.Or;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.enums.SortingCategory;
import ru.yandex.practicum.mapper.ItemMapper;
import ru.yandex.practicum.model.Item;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.service.CartService;
import ru.yandex.practicum.service.ItemService;
import ru.yandex.practicum.service.OrderService;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
public class OrderControllerWebLayerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private CartService cartService;

    @Test
    void createNotEmptyOrder_shouldReturnCreatedOrder() throws Exception {
        Order order = new Order();
        when(orderService.createOrder())
                .thenReturn(order);

        mockMvc.perform(post("/createOrder"))
                .andExpect(status().isOk())
                .andExpect(view().name("order"))
                .andExpect(model().attributeExists("order"));

        verify(orderService, times(1)).createOrder();
    }

    @Test
    void createEmptyOrder_shouldRedirect() throws Exception {
        Order order = null;
        when(orderService.createOrder())
                .thenReturn(order);

        mockMvc.perform(post("/createOrder"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/main/items"));

        verify(orderService, times(1)).createOrder();
    }

    @Test
    void getOrders_shouldReturnOrders() throws Exception {
        Order order1 = new Order();
        Order order2 = new Order();
        when(orderService.getOrders())
                .thenReturn(List.of(order1, order2));


        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders"))
                .andExpect(model().attributeExists("sumOfAllOrders"))
                .andExpect(model().attributeExists("orders"));
    }

    @Test
    void getOrder_shouldReturnOrder() throws Exception {
        Order order1 = new Order();
        when(orderService.getOrder(1))
                .thenReturn(order1);

        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("order"))
                .andExpect(model().attributeExists("order"));
    }
}
