package ru.yandex.practicum.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.config.SecurityConfigTest;
import ru.yandex.practicum.config.WebClientConfiguration;
import ru.yandex.practicum.dao.*;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.service.CartService;
import ru.yandex.practicum.service.OrderService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@WebFluxTest(OrderController.class)
@Import({WebClientConfiguration.class, SecurityConfigTest.class})
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

    @MockitoBean
    private UserRepository userRepository;

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void createNotEmptyOrder_shouldReturnCreatedOrder() throws Exception {
        Order order = new Order("user");
        Mono<Order> orderMono = Mono.just(order);
        when(orderService.createOrder("user"))
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

        verify(orderService, times(1)).createOrder("user");
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void getOrders_shouldReturnOrders() throws Exception {
        Order order1 = new Order("user");
        Order order2 = new Order("user");
        OrderDto orderDto1 = new OrderDto(order1.getId(), order1.getTotalSum());
        OrderDto orderDto2 = new OrderDto(order2.getId(), order2.getTotalSum());

        Flux<OrderDto> orderFlux = Flux.just(orderDto1, orderDto2);
        when(orderService.getOrders("user"))
                .thenReturn(orderFlux);

        when(orderService.getOrdersTotalSumFormatted("user"))
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

        verify(orderService, times(1)).getOrders("user");
        verify(orderService, times(1)).getOrdersTotalSumFormatted("user");
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void getOrder_shouldReturnOrder() throws Exception {
        Order order1 = new Order("user");
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
