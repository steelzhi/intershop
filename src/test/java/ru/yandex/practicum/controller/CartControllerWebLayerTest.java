/*
package ru.yandex.practicum.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.model.CartItem;
import ru.yandex.practicum.service.CartService;
import ru.yandex.practicum.service.ItemService;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
public class CartControllerWebLayerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private ItemService itemService;

    @Test
    void addItemToCart_shouldAddItemtAndRedirect() throws Exception {
        ItemDto itemDto = new ItemDto("itemDto", "desc", null, 1.0, 2);
        CartItem cartItem = new CartItem(1, itemDto);
        when(cartService.addItemToCart(anyInt()))
                .thenReturn(cartItem);

        mockMvc.perform(post("/cart/add/1")
                        .param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/main/items"));

        verify(cartService, times(1)).addItemToCart(anyInt());
    }

    @Test
    void removeItemFromCart_shouldRemoveItemAndRedirectToCartItems() throws Exception {
        ItemDto itemDto = new ItemDto("itemDto", "desc", null, 1.0, 2);
        itemDto.setId(1);
        when(itemService.getExistingItemsDtos())
                .thenReturn(Map.of(itemDto.getId(), itemDto));

        doNothing().when(cartService).removeItemFromCart(anyInt());

        mockMvc.perform(post("/cart/remove/1")
                        .param("pageName", "CART"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart/items"));

        verify(cartService, times(1)).removeItemFromCart(1);
        verify(itemService, times(1)).setInExistingItemDtosItemDtoAmountToZero(1);
    }

    @Test
    void getCart_shouldGetCart() throws Exception {
        ItemDto itemDto1 = new ItemDto("itemDto1", "desc1", null, 1.0, 2);
        ItemDto itemDto2 = new ItemDto("itemDto2", "desc2", null, 2.0, 3);
        List<ItemDto> itemDtoList = List.of(itemDto1, itemDto2);
        when(cartService.getItemsDtosInCart())
                .thenReturn(itemDtoList);

        when(cartService.getTotalPrice())
                .thenReturn(itemDto1.getPrice() + itemDto2.getPrice());

        mockMvc.perform(get("/cart/items"))
                .andExpect(view().name("cart"))
                .andExpect(model().attributeExists("items"));

        verify(cartService, times(1)).getItemsDtosInCart();
        verify(cartService, times(1)).getTotalPriceFormatted();
    }
}
*/
