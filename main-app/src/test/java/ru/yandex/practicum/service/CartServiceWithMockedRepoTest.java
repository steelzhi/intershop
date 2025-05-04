/*
package ru.yandex.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dao.CartRepository;
import ru.yandex.practicum.dao.ImageRepository;
import ru.yandex.practicum.dao.ItemRepository;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.mapper.ItemMapper;
import ru.yandex.practicum.model.CartItem;
import ru.yandex.practicum.model.Image;
import ru.yandex.practicum.model.Item;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {CartService.class, ItemGettingFromCacheService.class})
public class CartServiceWithMockedRepoTest {
    @Autowired
    CartService cartService;

    @MockitoBean
    CartRepository cartRepository;

    @MockitoBean
    ItemRepository itemRepository;

    @MockitoBean
    ImageRepository imageRepository;

    @BeforeEach
    void setUp() {
        Mockito.reset(itemRepository);
        Mockito.reset(cartRepository);
    }

    @Test
    void testAddItemToCart() throws IOException {
        byte[] imageBytes1 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\armature.txt"));
        Image image1 = new Image(imageBytes1);
        when(imageRepository.save(image1))
                .thenReturn(Mono.just(image1));

        Item item1 = new Item("itemDto1", "abcdesc1", null, 1.0);
        int itemId = 1;
        Mono<ItemDto> itemDtoMono1 = ItemMapper.mapToItemDto(Mono.just(item1), Mono.just(image1))
                .doOnNext(itemDto -> itemDto.setId(itemId))
                .doOnNext(itemDto -> itemDto.setAmount(10));
        when(itemRepository.findById(itemId))
                .thenReturn(itemDtoMono1);
        ItemDto itemDto = itemDtoMono1.block();

        int cartId = 1;
        CartItem cartItem = new CartItem(cartId, itemId, "user");
        Mono<CartItem> cartItemMono = Mono.just(cartItem);
        when(cartRepository.findByItemIdAndUsername(cartItem.getItemId(), "user"))
                .thenReturn(cartItemMono);

        when(cartRepository.save(cartItem))
                .thenReturn(cartItemMono);

        Mono<CartItem> savedCartItemMono = cartService.addItemToCart(itemId, "user");
        CartItem savedCartItem = savedCartItemMono.block();

        assertTrue(savedCartItem != null, "cartItem shouldn't be empty");
        ItemDto itemDtoFromCart = itemRepository.findById(cartItem.getItemId()).block();

        assertEquals(itemDtoFromCart.getName(), itemDto.getName(), "Names are different");
        assertEquals(itemDtoFromCart.getDescription(), itemDto.getDescription(),
                "Descriptions are different");
        assertEquals(itemDtoFromCart.getPrice(), itemDto.getPrice(), "Prices are different");
        assertEquals(itemDtoFromCart.getAmount(), itemDto.getAmount(), "Amounts are different");

        verify(itemRepository, times(2)).findById(itemDto.getId());
        verify(cartRepository, times(2)).findByItemIdAndUsername(cartItem.getItemId(), "user");
    }

    @Test
    void testRemoveItemFromCart() throws IOException {
        byte[] imageBytes1 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\armature.txt"));
        Image image1 = new Image(imageBytes1);
        when(imageRepository.save(image1))
                .thenReturn(Mono.just(image1));

        Item item1 = new Item("itemDto1", "abcdesc1", null, 1.0);
        int itemId = 1;
        Mono<ItemDto> itemDtoMono1 = ItemMapper.mapToItemDto(Mono.just(item1), Mono.just(image1))
                .doOnNext(itemDto -> itemDto.setId(itemId))
                .doOnNext(itemDto -> itemDto.setAmount(10));
        when(itemRepository.findById(itemId))
                .thenReturn(itemDtoMono1);

        int cartId = 1;
        CartItem cartItem = new CartItem(cartId, itemId, "user");
        Mono<CartItem> cartItemMono = Mono.just(cartItem);
        when(cartRepository.findByItemIdAndUsername(cartItem.getItemId(), "user"))
                .thenReturn(cartItemMono);

        cartService.removeItemFromCart(itemId, "user").block();

        verify(cartRepository, times(1)).findByItemIdAndUsername(itemId, "user");
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testGetItemsDtosInCart() throws IOException {
        byte[] imageBytes1 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\armature.txt"));
        Image image1 = new Image(imageBytes1);
        when(imageRepository.save(image1))
                .thenReturn(Mono.just(image1));
        Item item1 = new Item("itemDto1", "abcdesc1", null, 1.0);
        int itemId1 = 1;
        Mono<ItemDto> itemDtoMono1 = ItemMapper.mapToItemDto(Mono.just(item1), Mono.just(image1))
                .doOnNext(itemDto -> itemDto.setId(itemId1))
                .doOnNext(itemDto -> itemDto.setAmount(10));

        byte[] imageBytes2 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\beam.txt"));
        Image image2 = new Image(imageBytes2);
        when(imageRepository.save(image2))
                .thenReturn(Mono.just(image2));
        Item item2 = new Item("itemDto2z", "descghy", null, 12.0);
        int itemId2 = 2;
        Mono<ItemDto> itemDtoMono2 = ItemMapper.mapToItemDto(Mono.just(item2), Mono.just(image2))
                .doOnNext(itemDto -> itemDto.setId(itemId2))
                .doOnNext(itemDto -> itemDto.setAmount(10));

        int cartId1 = 1;
        CartItem cartItem1 = new CartItem(cartId1, itemId1, "user");

        int cartId2 = 2;
        CartItem cartItem2 = new CartItem(cartId2, itemId2, "user");

        Flux<CartItem> cartItemFlux = Flux.just(cartItem1, cartItem2);

        when(cartRepository.findAllByUsername("user"))
                .thenReturn(cartItemFlux);

        when(itemRepository.findById(itemId1))
                .thenReturn(itemDtoMono1);

        when(itemRepository.findById(itemId2))
                .thenReturn(itemDtoMono2);

        Flux<ItemDto> foundItemDtosFlux = cartService.getItemsDtosInCart("user");
        foundItemDtosFlux.blockLast();
        List<ItemDto> foundItemDtosList = foundItemDtosFlux.toStream().toList();

        assertFalse(foundItemDtosList.isEmpty(), "List shouldn't be empty");
        assertTrue(foundItemDtosList.contains(itemDtoMono1.block()), "List should contain itemDto1");
        assertTrue(foundItemDtosList.contains(itemDtoMono2.block()), "List should contain itemDto2");

        verify(cartRepository, times(1)).findAllByUsername("user");
        verify(itemRepository, times(2)).findById(itemId1);
        verify(itemRepository, times(2)).findById(itemId2);


    }
}
*/
