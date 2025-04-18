package ru.yandex.practicum.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dao.*;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.model.CartItem;
import ru.yandex.practicum.service.CartService;
import ru.yandex.practicum.service.ItemService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@WebFluxTest(CartController.class)
public class CartControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private ItemService itemService;

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
    void addItemToCart_shouldAddItemtAndRedirect() throws Exception {
        //ItemDto itemDto = new ItemDto("itemDto", "desc", null, 1.0, 2);
        int itemId = 1;
        CartItem cartItem = new CartItem(1, itemId);
        when(cartService.addItemToCart(anyInt()))
                .thenReturn(Mono.just(cartItem));

        webTestClient.post()
                .uri("/cart/add/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cartItem)
                .exchange()
                .expectStatus().is3xxRedirection();

        verify(cartService, times(1)).addItemToCart(anyInt());
    }

    @Test
    void getCart_shouldGetCart() throws Exception {
        ItemDto itemDto1 = new ItemDto("itemDto1", "desc1", null, 1.0, 2);
        ItemDto itemDto2 = new ItemDto("itemDto2", "desc2", null, 2.0, 3);
        Flux<ItemDto> itemDtoFlux = Flux.just(itemDto1, itemDto2);
        when(cartService.getItemsDtosInCart())
                .thenReturn(itemDtoFlux);

        when(cartService.getTotalPriceFormatted(itemDtoFlux))
                .thenReturn(Mono.just("0"));

        webTestClient.get()
                .uri("/cart/items")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("cart"));
                    assertTrue(body.contains(itemDto1.getName()));
                    assertTrue(body.contains(itemDto2.getDescription()));
                });

        verify(cartService, times(1)).getItemsDtosInCart();
        verify(cartService, times(1)).getTotalPriceFormatted(itemDtoFlux);
    }
}
