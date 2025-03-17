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
    private Map<ItemDto, Integer> cart = new HashMap<>();
    private static double totalPrice;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ItemRepository itemRepository;

    public void addItemToCart(@PathVariable int id) {
        ItemDto itemDtoInItemRepo = itemRepository.findById(id).get();
        if (itemDtoInItemRepo.getAmount() == 0) {
            return;
        }

        if (cart.containsKey(itemDtoInItemRepo)) {
            int existingCartId = cart.get(itemDtoInItemRepo);
            CartItem cartItem = cartRepository.findById(existingCartId).get();
            cartItem.setItemDto(itemDtoInItemRepo);
            cartRepository.save(cartItem);
            cart.put(itemDtoInItemRepo, existingCartId);
        } else {
            CartItem cartItem = new CartItem(itemDtoInItemRepo);
            CartItem savedCartItem = cartRepository.save(cartItem);
            cart.put(itemDtoInItemRepo, savedCartItem.getId());
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
