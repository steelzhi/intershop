package ru.yandex.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dao.CartRepository;
import ru.yandex.practicum.dao.OrderRepository;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.model.CartItem;
import ru.yandex.practicum.model.Order;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    CartRepository cartRepository;

    public Order createOrder() {
        List<CartItem> cartItems = cartRepository.findAll();
        List<ItemDto> itemDtos = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            ItemDto itemDto = cartItem.getItemDto();
            itemDtos.add(itemDto);
        }

        Order order = new Order(itemDtos);
        orderRepository.save(order);
        cartRepository.deleteAll();

        return order;
    }
}
