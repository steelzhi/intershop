package ru.yandex.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dao.CartRepository;
import ru.yandex.practicum.dao.ItemRepository;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.model.CartItem;
import ru.yandex.practicum.util.Formatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartService {
    // Для снижения обращений к БД будем также хранить текущие заказы в кэше
    // ключ - товар, значение - id объекта CartItem
    //private Map<ItemDto, Integer> cart = new HashMap<>();
    private static double totalPrice;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ItemRepository itemRepository;

    public Mono<CartItem> addItemToCart(@PathVariable int id) {
        Mono<Boolean> isItemAmountPositive = itemRepository.findById(id)
                .filter(itemDto -> itemDto.getAmount() > 0)
                .hasElement();

        if (!isItemAmountPositive.block()) {
            return Mono.empty();
        }

        Mono<CartItem> cartItemMono = cartRepository.findByItemId(id);
        if (cartItemMono.block() == null) {
            CartItem cartItem = new CartItem(id);
            cartItemMono = cartRepository.save(cartItem);
        }
        return cartItemMono;

/*        if (cart.containsKey(itemDtoInItemRepo)) {
            int existingCartItemId = cart.get(itemDtoInItemRepo);
            CartItem cartItem = cartRepository.findById(existingCartItemId).get();
            cartItem.setItemDto(itemDtoInItemRepo);
            cart.put(itemDtoInItemRepo, existingCartItemId);
            return cartRepository.save(cartItem);
        } else {
            CartItem cartItem = new CartItem(itemDtoInItemRepo);
            CartItem savedCartItem = cartRepository.save(cartItem);
            cart.put(itemDtoInItemRepo, savedCartItem.getId());
            return savedCartItem;
        }*/
    }

    public Mono<Void> removeItemFromCart(int id) {
        Mono<CartItem> cartItemMono = cartRepository.findByItemId(id);
        if (cartItemMono.block() == null) {
            return Mono.empty();
        }

        ItemDto itemDto = itemRepository.findById(id).block();
        itemDto.setAmount(0);
        itemRepository.save(itemDto);

        int cartItemId = cartItemMono.block().getId();
        Mono<Void> deletion = cartRepository.deleteById(cartItemId);
        return deletion;
    }

    public Flux<ItemDto> getItemsDtosInCart() {
        Flux<CartItem> cartItems = cartRepository.findAll();
        List<ItemDto> itemDtos = new ArrayList<>();
        totalPrice = 0;
        /*Flux<ItemDto> itemDtoFlux = cartItems.flatMap(cartItem -> itemRepository.findById(cartItem.getItemId()));*/

        for (CartItem cartItem : cartItems.toIterable()) {
            ItemDto itemDto = itemRepository.findById(cartItem.getItemId()).block();
            itemDtos.add(itemDto);
            totalPrice += itemDto.getPrice() * itemDto.getAmount();
        }

        return Flux.fromIterable(itemDtos);
        /*return itemDtoFlux;*/
    }

    public double getTotalPrice() {
        return totalPrice;
    }

/*    public Map<ItemDto, Integer> getCart() {
        return cart;
    }*/

    public String getTotalPriceFormatted() {
        double totalPrice = getTotalPrice();
        return Formatter.DECIMAL_FORMAT.format(totalPrice);
    }
}
