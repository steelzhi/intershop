package ru.yandex.practicum.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.config.SecurityConfigTest;
import ru.yandex.practicum.config.WebClientConfiguration;
import ru.yandex.practicum.constant.Constants;
import ru.yandex.practicum.dao.*;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.model.CartItem;
import ru.yandex.practicum.service.CartService;
import ru.yandex.practicum.service.ItemGettingFromCacheService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@WebFluxTest(CartController.class)
@Import({WebClientConfiguration.class, SecurityConfigTest.class})
public class CartControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private ItemGettingFromCacheService itemService;

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
    void addItemToCart_shouldAddItemtAndRedirect() {
        int itemId = 1;
        CartItem cartItem = new CartItem(1, itemId, "user");
        when(cartService.addItemToCart(1, "user"))
                .thenReturn(Mono.just(cartItem));

        webTestClient.post()
                .uri("/cart/add/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cartItem)
                .exchange()
                .expectStatus().is3xxRedirection();

        verify(cartService, times(1)).addItemToCart(1, "user");
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void getCart_shouldGetCart() {
        ItemDto itemDto1 = new ItemDto("itemDto1", "desc1", null, 1.0, 2);
        ItemDto itemDto2 = new ItemDto("itemDto2", "desc2", null, 2.0, 3);
        Flux<ItemDto> itemDtoFlux = Flux.just(itemDto1, itemDto2);
        when(cartService.getItemsDtosInCart("user"))
                .thenReturn(itemDtoFlux);

        when(cartService.getTotalSumFormatted("user"))
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

        verify(cartService, times(1)).getItemsDtosInCart("user");
        verify(cartService, times(1)).getTotalSumFormatted("user");
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void checkPaymentServiceAvailabilityWhenPaymentServiceIsUnavailable() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme(Constants.SCHEME)
                        .host(Constants.HOST)
                        .port(Constants.PORT)
                        .path(Constants.ROOT_PATH + "/balance")
                        .build())
                .exchange()
                .expectStatus().isNotFound();
    }
}
