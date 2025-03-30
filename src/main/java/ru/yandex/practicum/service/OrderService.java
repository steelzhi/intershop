package ru.yandex.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dao.CartRepository;
import ru.yandex.practicum.dao.ItemRepository;
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
    ItemRepository itemRepository;

    @Autowired
    CartRepository cartRepository;

    public Mono<Order> createOrder() {
        Flux<CartItem> cartItems = cartRepository.findAll();
        Iterable<CartItem> cartItemsIterable = cartItems.toIterable();
        List<CartItem> cartItemList = new ArrayList<>();
        cartItemsIterable.forEach(cartItemList::add);
        if (cartItemList.isEmpty() || !doesCartHaveNotNullItems(cartItemList)) {
            return Mono.empty();
        }

        Order order = new Order();
        Mono<Order> savedOrderMono = orderRepository.save(order);
        Order savedOrder = savedOrderMono.block();
        int savedOrderId = savedOrder.getId();
        double totalSum = 0;
        for (CartItem cartItem : cartItemList) {
            ItemDto itemDto = itemRepository.findById(cartItem.getItemId()).block();
            OrderItem orderItem;
            if (itemDto.getAmount() != 0) {
                orderItem = new OrderItem(savedOrderId, itemDto.getId(), itemDto.getPrice(), itemDto.getAmount());
                orderItemRepository.save(orderItem).subscribe();
                totalSum += orderItem.getItemAmount() * orderItem.getItemPrice();
            }
        }

        savedOrder.setTotalSum(totalSum);
        savedOrder.setId(0);

/*        Order savedOrder = savedOrderMono.block();
        savedOrder.setTotalSum(totalSum);*/
        Mono<Order> secondarySavedOrder = orderRepository.save(savedOrder);
        cartRepository.deleteAll();

        //return secondarySavedOrder;

        return savedOrderMono;
    }

    public Flux<OrderItem> getOrderItems() {
        return orderItemRepository.findAll();
    }

    /*public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    public Order getOrder(int id) {
        Order order = orderRepository.findById(id).get();
        return order;
    }*/

    private boolean doesCartHaveNotNullItems(List<CartItem> cartItems) {
        for (CartItem cartItem : cartItems) {
            ItemDto itemDto = itemRepository.findById(cartItem.getItemId()).block();
            if (itemDto.getAmount() != 0) {
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
