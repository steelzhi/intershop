package ru.yandex.practicum.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.config.Configuration;
import ru.yandex.practicum.dao.*;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.service.CartService;
import ru.yandex.practicum.service.OrderService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@WebFluxTest(OrderController.class)
@Import(Configuration.class)
public class OrderControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private OrderRepository orderRepository;

    @MockitoBean
    private CartRepository cartRepository;

    @MockitoBean
    private OrderItemRepository orderItemRepository;

    @MockitoBean
    private ImageRepository imageRepository;

    @MockitoBean
    private ItemRepository itemRepository;

    @Test
    void createNotEmptyOrder_shouldReturnCreatedOrder() throws Exception {
        Order order = new Order();
        Mono<Order> orderMono = Mono.just(order);
        when(orderService.createOrder())
                .thenReturn(orderMono);

        OrderDto orderDto = new OrderDto(order.getId(), order.getTotalSum());
        when(orderService.getOrder(order.getId()))
                .thenReturn(Mono.just(orderDto));

        webTestClient.post()
                .uri("/create-order")
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("order"));
                    assertTrue(body.contains(orderDto.getTotalSumFormatted()));
                });

        verify(orderService, times(1)).createOrder();
    }

    @Test
    void getOrders_shouldReturnOrders() throws Exception {
        Order order1 = new Order();
        Order order2 = new Order();
        OrderDto orderDto1 = new OrderDto(order1.getId(), order1.getTotalSum());
        OrderDto orderDto2 = new OrderDto(order2.getId(), order2.getTotalSum());

        Flux<OrderDto> orderFlux = Flux.just(orderDto1, orderDto2);
        when(orderService.getOrders())
                .thenReturn(orderFlux);

        when(orderService.getOrdersTotalSumFormatted())
                .thenReturn(Mono.just("0"));

        webTestClient.get()
                .uri("/orders")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("orders"));
                    assertTrue(body.contains(orderDto1.getTotalSumFormatted()));
                    assertTrue(body.contains(orderDto2.getTotalSumFormatted()));
                });

        verify(orderService, times(1)).getOrders();
        verify(orderService, times(1)).getOrdersTotalSumFormatted();
    }

    @Test
    void getOrder_shouldReturnOrder() throws Exception {
        Order order1 = new Order();
        order1.setId(1);
        OrderDto orderDto1 = new OrderDto(order1.getId(), order1.getTotalSum());
        orderDto1.setId(1);
        Mono<OrderDto> orderDtoMono = Mono.just(orderDto1);
        when(orderService.getOrder(1))
                .thenReturn(orderDtoMono);

        webTestClient.get()
                .uri("/orders/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("order"));
                    assertTrue(body.contains(orderDto1.getTotalSumFormatted()));
                });

        verify(orderService, times(1)).getOrder(1);
    }
}
