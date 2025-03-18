package ru.yandex.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.dao.CartRepository;
import ru.yandex.practicum.dao.ItemRepository;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.model.CartItem;

import java.util.*;

@Service
public class CartService {
    // Для уменьшения дороговизны операций сохраняем текущие заказы в т.ч. в ОЗУ
    //Set<ItemDto> cart = new HashSet<>();
    // ключ - товар, значение - id объекта CartItem
    private Map<ItemDto, Integer> cart = new HashMap<>();
    private static double totalPrice;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ItemRepository itemRepository;

    public CartItem addItemToCart(@PathVariable int id) {
        ItemDto itemDtoInItemRepo = itemRepository.findById(id).get();
        if (itemDtoInItemRepo.getAmount() == 0) {
            return null;
        }

        if (cart.containsKey(itemDtoInItemRepo)) {
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
        }
    }

    public void removeItemFromCart(int id) {
        ItemDto itemDtoInItemRepo = itemRepository.findById(id).get();

        if (cart.containsKey(itemDtoInItemRepo)) {
            itemDtoInItemRepo.setAmount(0);
            itemRepository.save(itemDtoInItemRepo);
            int cartItemId = cart.get(itemDtoInItemRepo);
            cartRepository.deleteById(cartItemId);
            cart.remove(itemDtoInItemRepo);
        }
    }

    public List<ItemDto> getItemsDtosInCart() {
        List<CartItem> cartItems = cartRepository.findAll();
        List<ItemDto> itemDtos = new ArrayList<>();
        totalPrice = 0;
        for (CartItem cartItem : cartItems) {
            ItemDto itemDto = cartItem.getItemDto();
            itemDtos.add(itemDto);
            totalPrice += itemDto.getPrice() * itemDto.getAmount();
        }

        return itemDtos;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public Map<ItemDto, Integer> getCart() {
        return cart;
    }
}
