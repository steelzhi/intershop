package ru.yandex.practicum.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dao.*;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.enums.SortingCategory;
import ru.yandex.practicum.service.ItemAllOtherOpsService;
import ru.yandex.practicum.service.ItemGettingFromCacheService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@WebFluxTest(ItemController.class)
public class ItemControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ItemGettingFromCacheService itemService;

    @MockitoBean
    private ItemAllOtherOpsService itemsService;

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

    @AfterEach
    void setUp() {
        Mockito.reset(itemService);
        Mockito.reset(cartRepository);
        Mockito.reset(orderRepository);
        Mockito.reset(orderItemRepository);
        Mockito.reset(imageRepository);
        Mockito.reset(itemRepository);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void getItemsListForAuthorizedUser_shouldReturnItemsList() throws Exception {
        ItemDto itemDto1 = new ItemDto("itemDto1", "desc1", null, 1.0, 2);
        ItemDto itemDto2 = new ItemDto("itemDto2", "desc2", null, 2.0, 3);
        List<ItemDto> itemDtoList = List.of(itemDto1, itemDto2);
        when(itemsService.getItemsList(10, 1))
                .thenReturn(Flux.fromIterable(itemDtoList));
        when(itemsService.getItemListSize())
                .thenReturn(Mono.just(itemDtoList.size()));

        webTestClient.mutateWith(
                        // Создаём объект аутентификации
                        SecurityMockServerConfigurers.mockAuthentication(
                                // Создаём объект аутентификации с указанными ролями
                                new UsernamePasswordAuthenticationToken(
                                        "admin", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                                )
                        ))
                .get()
                .uri("/main/items")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("main"));
                    assertTrue(body.contains(itemDto1.getName()));
                    assertTrue(body.contains(itemDto2.getDescription()));
                });

        verify(itemsService, times(1)).getItemsList(10, 1);
        verify(itemsService, times(1)).getItemListSize();
    }

    // Страница с товарами доступна любым пользователям, поэтому перенаправления на страницу входа не будет
    @Test
    void getItemsListForUnathorizedUser_shouldReturnItemsList() throws Exception {
        ItemDto itemDto1 = new ItemDto("itemDto1", "desc1", null, 1.0, 2);
        ItemDto itemDto2 = new ItemDto("itemDto2", "desc2", null, 2.0, 3);
        List<ItemDto> itemDtoList = List.of(itemDto1, itemDto2);
        when(itemsService.getItemsList(10, 1))
                .thenReturn(Flux.fromIterable(itemDtoList));
        when(itemsService.getItemListSize())
                .thenReturn(Mono.just(itemDtoList.size()));

        webTestClient.mutateWith(
                        // Создаём объект аутентификации
                        SecurityMockServerConfigurers.mockAuthentication(
                                // Создаём объект аутентификации с указанными ролями
                                new UsernamePasswordAuthenticationToken(
                                        "admin", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                                )
                        ))
                .get()
                .uri("/main/items")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("main"));
                    assertTrue(body.contains(itemDto1.getName()));
                    assertTrue(body.contains(itemDto2.getDescription()));
                });

        verify(itemsService, times(1)).getItemsList(10, 1);
        verify(itemsService, times(1)).getItemListSize();
    }

    @Test
    void getItemsListSplittedByPages_shouldReturnSplittedItemsList() throws Exception {
        ItemDto itemDto1 = new ItemDto("itemDto1", "desc1", null, 1.0, 2);
        ItemDto itemDto2 = new ItemDto("itemDto2", "desc2", null, 2.0, 3);
        List<ItemDto> itemDtoList = List.of(itemDto1);
        when(itemsService.getItemsList(1, 1))
                .thenReturn(Flux.fromIterable(itemDtoList));
        when(itemsService.getItemListSize())
                .thenReturn(Mono.just(itemDtoList.size()));

        webTestClient.mutateWith(
                        SecurityMockServerConfigurers.mockAuthentication(
                                new UsernamePasswordAuthenticationToken(
                                        "admin", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                                )
                        ))
                .get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/main/items")
                                .queryParam("itemsOnPage", "1")
                                .queryParam("pageNumber", "1")
                                .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("main"));
                    assertTrue(body.contains(itemDto1.getName()));
                    assertFalse(body.contains(itemDto2.getDescription()));
                });

        verify(itemsService, times(1)).getItemsList(1, 1);
        verify(itemsService, times(1)).getItemListSize();
    }

    @Test
    void getItemDto_shouldReturnItemDto() throws Exception {
        ItemDto itemDto1 = new ItemDto("itemDto1", "desc1", null, 1.0, 2);
        when((itemsService.getItemDto(anyInt())))
                .thenReturn(Mono.just(itemDto1));

        webTestClient.mutateWith(
                        SecurityMockServerConfigurers.mockAuthentication(
                                new UsernamePasswordAuthenticationToken(
                                        "admin", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                                )
                        ))
                .get()
                .uri("/items/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("item"));
                    assertTrue(body.contains(itemDto1.getName()));
                    assertTrue(body.contains(itemDto1.getDescription()));
                });

        verify(itemsService, times(1)).getItemDto(anyInt());
    }

    @Test
    void search_shouldReturnFoundItems() throws Exception {
        ItemDto itemDto1 = new ItemDto("DE", "zzz", null, 11.0, 2);
        ItemDto itemDto2 = new ItemDto("itemDto3", "def", null, 1.0, 2);

        when(itemsService.search("zz", SortingCategory.PRICE))
                .thenReturn(Flux.just(itemDto1));

        List<ItemDto> itemDtoList = List.of(itemDto1);

        when(itemsService.getItemListSize())
                .thenReturn(Mono.just(itemDtoList.size()));

        webTestClient.mutateWith(
                        SecurityMockServerConfigurers.mockAuthentication(
                                new UsernamePasswordAuthenticationToken(
                                        "admin", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                                )
                        ))
                .get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/search")
                                .queryParam("key", "zz")
                                .queryParam("sortingCategory", "PRICE")
                                .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("main"));
                    assertTrue(body.contains(itemDto1.getName()));
                    assertTrue(body.contains(itemDto1.getDescription()));
                    assertFalse(body.contains(itemDto2.getDescription()));
                });

        verify(itemsService, times(1)).search("zz", SortingCategory.PRICE);
    }
}
