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
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.OrderItemDto;
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
        if (!cartItems.hasElements().block()
                || !doesCartHaveNotNullItems(cartItemList)) {
            return Mono.empty();
        }
/*        if (!cartItems.hasElements().block()
                || !doesCartHaveNotNullItems(cartItems).block()) {
            return Mono.empty();
        }*/

        Order order = new Order();
        Mono<Order> savedOrderMono = orderRepository.save(order);
        Order savedOrder = savedOrderMono.block();
        int savedOrderId = savedOrder.getId();
        //double totalSum = 0;
        Mono<String> totalSumMono = Mono.just("0");
        cartItems.flatMap(cartItem -> itemRepository.findById(cartItem.getItemId()))
                .filter(itemDto -> itemDto.getAmount() > 0)
                .map(itemDto -> {
                    OrderItem orderItem = new OrderItem(savedOrderId, itemDto.getId(), itemDto.getPrice(), itemDto.getAmount());
                    orderItemRepository.save(orderItem).subscribe();

                    // Строка ниже не исполняется
                    totalSumMono.map(d -> String.valueOf(Double.parseDouble(d) + orderItem.getItemAmount() * orderItem.getItemPrice())).subscribe();
                    return itemDto;
                })
                .flatMap(i -> cartRepository.deleteAll())
                .blockLast();

/*        for (CartItem cartItem : cartItemList) {
            ItemDto itemDto = itemRepository.findById(cartItem.getItemId()).block();
            OrderItem orderItem;
            if (itemDto.getAmount() != 0) {
                orderItem = new OrderItem(savedOrderId, itemDto.getId(), itemDto.getPrice(), itemDto.getAmount());
                orderItemRepository.save(orderItem).subscribe();
                totalSum += orderItem.getItemAmount() * orderItem.getItemPrice();
            }
        }*/

        double totalSum = Double.parseDouble(totalSumMono.block());
        //savedOrder.setTotalSum(totalSum);

/*        Mono<Void> updated = orderRepository.setTotalSum(totalSum, savedOrderId);
        updated.subscribe();*/
        Mono<Order> updatedOrder = orderRepository.findById(savedOrderId);
        updatedOrder.subscribe();

        return updatedOrder;
    }

    public Flux<OrderItem> getOrderItems() {
        return orderItemRepository.findAll();
    }

    public List<OrderDto> getOrders() {
        Flux<Order> orderFlux = orderRepository.findAll();
        Iterable<Order> orderIterable = orderFlux.toIterable();
        List<OrderDto> orderDtos = new ArrayList<>();
        for (Order order : orderIterable) {
            orderDtos.add(getOrder(order.getId()));
        }

        return orderDtos;

/*        Flux<OrderDto> orderDtoFlux = orderFlux
                .map(order -> new OrderDto(order.getId(), order.getTotalSum()))
                .map(orderDto -> {
                    int orderId = orderDto.getId();
                    Flux<OrderItem> orderItemFlux = orderItemRepository.findAllByOrderId(orderId);
                    orderItemFlux.map(orderItem -> {
                        Flux<>

                    })
                    orderDto.setOrderItemFlux(orderItemFlux);
                    return orderDto;
                });

        return orderDtoFlux;*/
    }

    public OrderDto getOrder(int id) {
        Order order = orderRepository.findById(id).block();
        OrderDto orderDto = new OrderDto(order.getId(), order.getTotalSum());

        Flux<OrderItem> orderItemFlux = orderItemRepository.findAllByOrderId(orderDto.getId());
        Iterable<OrderItem> orderItemIterable = orderItemFlux.toIterable();
        List<OrderItemDto> orderItemDtoList = new ArrayList<>();
        orderItemIterable.forEach(orderItem -> {
            ItemDto itemDto = itemRepository.findById(orderItem.getItemId()).block();
            OrderItemDto orderItemDto = new OrderItemDto(orderItem.getOrderId(), order.getId(), itemDto);
            orderItemDtoList.add(orderItemDto);
        });

        orderDto.setOrderItemDto(orderItemDtoList);
        return orderDto;
    }
/*
    private Mono<Boolean> doesCartHaveNotNullItems(Flux<CartItem> cartItems) {
        *//*
     * Из CartItem (это класс для промежуточной таблицы, связующей "Корзину" с товарами, в котором хранится по
     * 1 добавленному в "Корзину" товару) получаем id хранящегося в нем товара и далее по id - сам товар (ItemDto -
     * товар со всеми его параметрами)
     *//*
        Flux<ItemDto> itemDtoFlux = cartItems.map(cartItem -> cartItem.getItemId())
                .map(itemId -> itemRepository.findById(itemId).block());

        *//*
     * Проверяем, что среди товаров есть хотя бы 1 с ненулевым количеством
     *//*
        Mono<Boolean> doesCartHaveNotNullItems = itemDtoFlux.filter(itemDto -> itemDto.getAmount() > 0)
                .hasElements();

        return doesCartHaveNotNullItems;
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

    public Mono<Double> getOrdersTotalSum() {
        return orderRepository.getSumOfAllOrders();
    }

    public String getOrdersTotalSumFormatted() {
        Double sumOfAllOrders = getOrdersTotalSum().block();
        return Formatter.DECIMAL_FORMAT.format(sumOfAllOrders != null ? sumOfAllOrders : 0);
    }
}