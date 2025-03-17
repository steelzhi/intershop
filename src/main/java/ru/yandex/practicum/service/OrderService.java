package ru.yandex.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dao.CartRepository;
import ru.yandex.practicum.dao.OrderItemRepository;
import ru.yandex.practicum.dao.OrderRepository;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.model.CartItem;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.model.OrderItem;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    CartRepository cartRepository;

    public Order createOrder() {
        List<CartItem> cartItems = cartRepository.findAll();
        List<OrderItem> orderItems = new ArrayList<>();
        Order order = new Order();
        Order savedOrder = orderRepository.save(order);
        for (CartItem cartItem : cartItems) {
            ItemDto itemDto = cartItem.getItemDto();
            OrderItem orderItem = new OrderItem(savedOrder, itemDto, itemDto.getAmount());
            orderItems.add(orderItem);
            orderItemRepository.save(orderItem);
        }

        savedOrder.setOrderItems(orderItems);
        orderRepository.save(savedOrder);
        cartRepository.deleteAll();

        return order;
    }

    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    public Order getOrder(int id) {
        return orderRepository.findById(id).get();
    }
}
