package ru.yandex.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.yandex.practicum.dao.CartRepository;
import ru.yandex.practicum.dao.ItemRepository;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.model.CartItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = CartService.class)
public class CartServiceWithMockedRepoTest {
    @Autowired
    CartService cartService;

    @MockitoBean
    CartRepository cartRepository;

    @MockitoBean
    ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        Mockito.reset(itemRepository);
        Mockito.reset(cartRepository);
    }

    @Test
    void testAddItemToCart() {
        ItemDto itemDto = new ItemDto("ItemDto", "Desc", null, 1.0, 7);
        itemDto.setId(1);
        when(itemRepository.findById(itemDto.getId()))
                .thenReturn(Optional.of(itemDto));

        CartItem cartItem = new CartItem(1, itemDto);
        when(cartRepository.findById(cartItem.getId()))
                .thenReturn(Optional.of(cartItem));

        when(cartRepository.save(cartItem))
                .thenReturn(cartItem);

        Map<ItemDto, Integer> cart = cartService.getCart();
        cart.put(itemDto, cartItem.getId());
        CartItem cartItemFromDao = cartService.addItemToCart(1);

        assertTrue(cartItemFromDao != null, "cartItem should't be emplty");
        ItemDto itemDtoFromCartItem = cartItemFromDao.getItemDto();
        assertEquals(itemDtoFromCartItem.getName(), itemDto.getName(), "Names are different");
        assertEquals(itemDtoFromCartItem.getDescription(), itemDto.getDescription(), "Descriptions are different");
        assertEquals(itemDtoFromCartItem.getPrice(), itemDto.getPrice(), "Prices are different");
        assertEquals(itemDtoFromCartItem.getAmount(), itemDto.getAmount(), "Amounts are different");

        cart.clear();
        verify(itemRepository, times(1)).findById(itemDto.getId());
        verify(cartRepository, times(1)).findById(cartItem.getId());
        verify(cartRepository, times(1)).save(cartItem);
    }

    @Test
    void testRemoveItemFromCart() {
        ItemDto itemDto = new ItemDto("ItemDto", "Desc", null, 1.0, 7);
        itemDto.setId(1);
        when(itemRepository.findById(itemDto.getId()))
                .thenReturn(Optional.of(itemDto));

        CartItem cartItem = new CartItem(1, itemDto);
        Map<ItemDto, Integer> cart = cartService.getCart();
        cart.put(itemDto, cartItem.getId());

        cartService.removeItemFromCart(itemDto.getId());

        verify(cartRepository, times(1)).deleteById(cartItem.getId());
    }

    @Test
    void testGetItemsDtosInCart() {
        ItemDto itemDto1 = new ItemDto("itemDto1", "desc1", null, 1.0, 2);
        ItemDto itemDto2 = new ItemDto("itemDto2", "desc2", null, 2.0, 3);

        CartItem cartItem1 = new CartItem(1, itemDto1);
        CartItem cartItem2 = new CartItem(2, itemDto2);
        List<CartItem> cartItems = List.of(cartItem1, cartItem2);

        when(cartRepository.findAll())
                .thenReturn(cartItems);

        List<ItemDto> itemDtos = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            itemDtos.add(cartItem.getItemDto());
        }

        List<ItemDto> itemsDtosInCart = cartService.getItemsDtosInCart();
        assertTrue(!itemsDtosInCart.isEmpty(), "List shouldn't be empty");
        assertEquals(itemDtos, itemsDtosInCart, "Lists should be equal");
    }
}
