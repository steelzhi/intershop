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
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemGettingFromCacheService itemService;

    public Mono<CartItem> addItemToCart(int itemId, String username) {
        // Получаем товар из БД
        Mono<ItemDto> itemDtoMono = itemRepository.findById(itemId);

        // Оцениваем его количество (> 0 или нет)
        Mono<Boolean> doesItemDtoHasPositiveAmount = itemDtoMono
                .filter(itemDto -> itemDto.getAmount() > 0)
                .hasElement();

        Mono<CartItem> cartItemMono = doesItemDtoHasPositiveAmount
                .flatMap(hasPositiveAmount -> getCartItemForPositiveAmountMono(hasPositiveAmount, itemId, username));
        cartItemMono.subscribe();

        return cartItemMono;
    }

    public Mono<Void> removeItemFromCart(int itemId, String username) {
        // Удаляем товар из "Корзины"
        Mono<Void> itemDtoDeleteFromCart = cartRepository.findByItemIdAndUsername(itemId, username)
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

    public Flux<ItemDto> getItemsDtosInCart(String username) {
        Flux<CartItem> cartItems = cartRepository.findAllByUsername(username);
        return cartItems
                .flatMap(cartItem -> {
                    Mono<ItemDto> itemDtoMono = itemService.getItemDto(cartItem.getItemId());

                    return itemDtoMono.filter(itemDto -> itemDto.getAmount() > 0);
                });
    }

    public Mono<String> getTotalSumFormatted(String username) {
        return cartRepository
                .getTotalSum(username)
                .map(Formatter.DECIMAL_FORMAT::format);
    }

    // Если количество товара = 0, товар в "Корзину" не добавляется.
    private Mono<CartItem> getCartItemForPositiveAmountMono(boolean hasPositiveAmount, int itemId, String username) {
        if (hasPositiveAmount) {
            Mono<CartItem> cartItemMono = cartRepository.findByItemIdAndUsername(itemId, username);
            cartItemMono
                    .hasElement()
                    .flatMap(hasCartItem -> getCartItemForAlreadyAddedItemMono(cartItemMono, hasCartItem, itemId, username))
                    .subscribe();
            return cartItemMono;
        } else {
            return Mono.empty();
        }
    }

    /*
     * Если количество > 0, то смотрим, был ли этот товар уже добавлен в "Корзину" ранее. Если был, заменяем количество
     * товара в "Корзине" на текущее. Если не был, добавляем в "Корзину".
     */
    private Mono<CartItem> getCartItemForAlreadyAddedItemMono(
            Mono<CartItem> cartItemMono, boolean hasCartItem, int itemId, String username) {
        if (hasCartItem) {
            return cartItemMono;
        } else {
            CartItem cartItem = new CartItem(itemId, username);
            return cartRepository.save(cartItem);
        }
    }
}
