package ru.yandex.practicum.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dao.*;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.enums.SortingCategory;
import ru.yandex.practicum.service.ItemService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@WebFluxTest(ItemController.class)
public class ItemControllerTest {
    @Autowired
    private WebTestClient webTestClient;

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
    void getItemsList_shouldReturnItemsList() throws Exception {
        ItemDto itemDto1 = new ItemDto("itemDto1", "desc1", null, 1.0, 2);
        ItemDto itemDto2 = new ItemDto("itemDto2", "desc2", null, 2.0, 3);
        List<ItemDto> itemDtoList = List.of(itemDto1, itemDto2);
        when(itemService.getItemsList(10, 1))
                .thenReturn(Flux.fromIterable(itemDtoList));
        when(itemService.getItemListSize())
                .thenReturn(Mono.just(itemDtoList.size()));

        webTestClient.get()
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

        verify(itemService, times(1)).getItemsList(10, 1);
        verify(itemService, times(1)).getItemListSize();
    }

    @Test
    void getItemsListSplittedByPages_shouldReturnSplittedItemsList() throws Exception {
        ItemDto itemDto1 = new ItemDto("itemDto1", "desc1", null, 1.0, 2);
        ItemDto itemDto2 = new ItemDto("itemDto2", "desc2", null, 2.0, 3);
        List<ItemDto> itemDtoList = List.of(itemDto1);
        when(itemService.getItemsList(1, 1))
                .thenReturn(Flux.fromIterable(itemDtoList));
        when(itemService.getItemListSize())
                .thenReturn(Mono.just(itemDtoList.size()));

        webTestClient.get()
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

        verify(itemService, times(1)).getItemsList(1, 1);
        verify(itemService, times(1)).getItemListSize();
    }

    @Test
    void getItemDto_shouldReturnItemDto() throws Exception {
        ItemDto itemDto1 = new ItemDto("itemDto1", "desc1", null, 1.0, 2);
        when((itemService.getItemDto(anyInt())))
                .thenReturn(Mono.just(itemDto1));

        webTestClient.get()
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

        verify(itemService, times(1)).getItemDto(anyInt());
    }

    @Test
    void search_shouldReturnFoundItems() throws Exception {
        ItemDto itemDto1 = new ItemDto("DE", "zzz", null, 11.0, 2);
        ItemDto itemDto2 = new ItemDto("itemDto3", "def", null, 1.0, 2);

        when(itemService.search("zz", SortingCategory.PRICE))
                .thenReturn(Flux.just(itemDto1));

        List<ItemDto> itemDtoList = List.of(itemDto1);

        when(itemService.getItemListSize())
                .thenReturn(Mono.just(itemDtoList.size()));

        webTestClient.get()
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

        verify(itemService, times(1)).search("zz", SortingCategory.PRICE);
    }
}
