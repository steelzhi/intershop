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
    private static double[] totalPriceArray = new double[1];


    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ItemRepository itemRepository;

    public Mono<CartItem> addItemToCart(@PathVariable int id) {
       /* // Получаем товар из БД
        Mono<ItemDto> itemDtoMono = itemRepository.findById(id);

        // Оцениваем его количество (> 0 или нет)
        Mono<Boolean> doesItemDtoHasPositiveAmount = itemDtoMono
                .filter(itemDto -> itemDto.getAmount() > 0)
                .hasElement();

        *//* Если количество товара = 0, товар в "Корзину" не добавляется. Если количество > 0, то смотрим, был ли
        * этот товар уже добавлен в "Корзину" ранее. Если был, заменяем количество товара в "Корзине" на текущее.
        * Если не был, добавляем в "Корзину".
         *//*
        Mono<CartItem> cartItemMono = doesItemDtoHasPositiveAmount
                .flatMap(hasPositiveAmount -> {
                    if (hasPositiveAmount) {
                        Mono<CartItem> cartItemMono2 = cartRepository.findByItemId(id);
                        cartItemMono2
                                .hasElement()
                                .flatMap(hasCartItem -> {
                                    if (hasCartItem) {
                                        return cartItemMono2;
                                    } else {
                                        CartItem cartItem = new CartItem(id);
                                        return cartRepository.save(cartItem);
                                    }
                                });
                        return cartItemMono2;
                    } else {
                        return Mono.empty();
                    }
                });

        return cartItemMono;*/

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

    }

    public Mono<Void> removeItemFromCart(int id) {
        // Удаляем товар из "Корзины"
        Mono<Void> itemDtoDeleteFromCart = cartRepository.findByItemId(id)
                .flatMap(cartItem -> cartRepository.deleteById(cartItem.getId()));

        // Обнуляем количество у удаленного из "Корзины" товара
        Mono<ItemDto> itemDtoSetZeroAmount = itemRepository.findById(id)
                .map(itemDto -> {
                    itemDto.setAmount(0);
                    return itemDto;
                })
                .flatMap(itemDto -> itemRepository.save(itemDto));

        itemDtoSetZeroAmount
                .then(itemDtoDeleteFromCart)
                .subscribe();

        return Mono.empty();
    }

    public Iterable<ItemDto> getItemsDtosInCart() {
        totalPriceArray[0] = 0;
        Flux<CartItem> cartItems = cartRepository.findAll();
        Flux<ItemDto> itemDtoFlux = cartItems
                .flatMap(cartItem -> {
                    Mono<ItemDto> itemDtoMono = itemRepository.findById(cartItem.getItemId());
                    itemDtoMono
                            .filter(itemDto -> itemDto.getAmount() > 0)
                            // Вычислим в этом потоке общую стоимость товаров в "Корзине"
                            .map(itemDto -> {
                                totalPriceArray[0] += itemDto.getPrice() * itemDto.getAmount();
                                return itemDto;
                            })
                            .subscribe();
                    return itemDtoMono;
                });

        itemDtoFlux.blockLast();

        return itemDtoFlux.toIterable();



        // Старый метод с блокировками:
/*        List<ItemDto> itemDtos = new ArrayList<>();
        for (CartItem cartItem : cartItems.toIterable()) {
            Mono<ItemDto> itemDtoMono = itemRepository.findById(cartItem.getItemId());
            itemDtoMono
                    .filter(itemDto -> itemDto.getAmount() > 0)
                    .map(itemDto -> {
                        totalPrice += itemDto.getPrice() * itemDto.getAmount();
                        itemDtos.add(itemDto);
                        return itemDto;
                    }).subscribe();
            itemDtoMono.block();
        }

        return Flux.fromIterable(itemDtos);*/
    }

/*    public Map<ItemDto, Integer> getCart() {
        return cart;
    }*/

/*    public String getTotalPriceFormatted() {
        return Formatter.DECIMAL_FORMAT.format(totalPrice);

    }*/

    public String getTotalPriceFormatted() {
        return Formatter.DECIMAL_FORMAT.format(totalPriceArray[0]);
    }
}
