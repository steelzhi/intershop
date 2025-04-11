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

@Service
public class CartService {
/*    // Для снижения обращений к БД будем также хранить текущие заказы в кэше
    // ключ - товар, значение - id объекта CartItem
    private Map<ItemDto, Integer> cart = new HashMap<>();*/
    private static double[] totalPriceArray = new double[1];

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ItemRepository itemRepository;

    public Mono<CartItem> addItemToCart(@PathVariable int itemId) {
        // Получаем товар из БД
        Mono<ItemDto> itemDtoMono = itemRepository.findById(itemId);

        // Оцениваем его количество (> 0 или нет)
        Mono<Boolean> doesItemDtoHasPositiveAmount = itemDtoMono
                .filter(itemDto -> itemDto.getAmount() > 0)
                .hasElement();

        /* Если количество товара = 0, товар в "Корзину" не добавляется. Если количество > 0, то смотрим, был ли
         * этот товар уже добавлен в "Корзину" ранее. Если был, заменяем количество товара в "Корзине" на текущее.
         * Если не был, добавляем в "Корзину".
         */
        Mono<CartItem> cartItemMono = doesItemDtoHasPositiveAmount
                .flatMap(hasPositiveAmount -> {
                    if (hasPositiveAmount) {
                        Mono<CartItem> cartItemMono2 = cartRepository.findByItemId(itemId);
                        cartItemMono2
                                .hasElement()
                                .flatMap(hasCartItem -> {
                                    if (hasCartItem) {
                                        return cartItemMono2;
                                    } else {
                                        CartItem cartItem = new CartItem(itemId);
                                        return cartRepository.save(cartItem);
                                    }
                                })
                                .subscribe();
                        return cartItemMono2;
                    } else {
                        return Mono.empty();
                    }
                });

        cartItemMono.subscribe();

        return cartItemMono;
    }

    public Mono<Void> removeItemFromCart(int itemId) {
        // Удаляем товар из "Корзины"
        Mono<Void> itemDtoDeleteFromCart = cartRepository.findByItemId(itemId)
                .flatMap(cartItem -> cartRepository.deleteById(cartItem.getId()));

        // Обнуляем количество у удаленного из "Корзины" товара
        Mono<ItemDto> itemDtoSetZeroAmount = itemRepository.findById(itemId)
                .doOnNext(itemDto -> itemDto.setAmount(0))
                .flatMap(itemDto -> itemRepository.save(itemDto));

        itemDtoSetZeroAmount
                .then(itemDtoDeleteFromCart)
                .subscribe();

        return Mono.empty();
    }

    public Flux<ItemDto> getItemsDtosInCart() {
        Flux<CartItem> cartItems = cartRepository.findAll();
        return cartItems.flatMap(cartItem -> {
            Mono<ItemDto> itemDtoMono = itemRepository.findById(cartItem.getItemId());
            return itemDtoMono.filter(itemDto -> itemDto.getAmount() > 0);
        });
    }

    /*public Map<ItemDto, Integer> getCart() {
        return cart;
    }*/

    public Mono<String> getTotalPriceFormatted(Flux<ItemDto> itemDtosFlux) {
        return itemDtosFlux
                .map(itemDto -> itemDto.getPrice() * itemDto.getAmount())
                .reduce(0d, Double::sum)
                .map(Formatter.DECIMAL_FORMAT::format);
    }
}
