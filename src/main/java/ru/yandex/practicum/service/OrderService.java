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
import ru.yandex.practicum.util.Formatter;

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
        if (cartItems.isEmpty() || !doesCartHaveNotNullItems(cartItems)) {
            return null;
        }

        List<OrderItem> orderItems = new ArrayList<>();
        Order order = new Order();
        Order savedOrder = orderRepository.save(order);
        double totalSum = 0;
        for (CartItem cartItem : cartItems) {
            ItemDto itemDto = cartItem.getItemDto();
            OrderItem orderItem;
            if (itemDto.getAmount() != 0) {
                orderItem = new OrderItem(savedOrder, itemDto, itemDto.getAmount());
                orderItems.add(orderItem);
                orderItemRepository.save(orderItem);
                totalSum += orderItem.getItemAmount() * orderItem.getItemDto().getPrice();
            }
        }

        savedOrder.setOrderItems(orderItems);
        savedOrder.setTotalSum(totalSum);
        orderRepository.save(savedOrder);
        cartRepository.deleteAll();

        return savedOrder;
    }

    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    public Order getOrder(int id) {
        Order order = orderRepository.findById(id).get();
        return order;
    }

    private boolean doesCartHaveNotNullItems(List<CartItem> cartItems) {
        for (CartItem cartItem : cartItems) {
            if (cartItem.getItemDto().getAmount() != 0) {
                return true;
            }
        }

        return false;
    }

    public Double getOrdersTotalSum() {
        return orderRepository.getSumOfAllOrders();
    }

    public String getOrdersTotalSumFormatted() {
        Double sumOfAllOrders = getOrdersTotalSum();
        return Formatter.DECIMAL_FORMAT.format(sumOfAllOrders != null ? sumOfAllOrders : 0);
    }
}
