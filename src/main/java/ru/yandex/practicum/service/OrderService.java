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
        if (!cartItems.hasElements().block()
                || !doesCartHaveNotNullItems(cartItems).block()) {
            return Mono.empty();
        }

        Order order = new Order();
        Mono<Order> savedOrderMono = orderRepository.save(order);
        Order savedOrder = savedOrderMono.block();
        int savedOrderId = savedOrder.getId();
        double[] totalSumArray = new double[1];

        cartItems.flatMap(cartItem -> itemRepository.findById(cartItem.getItemId()))
                .filter(itemDto -> itemDto.getAmount() > 0)
                .map(itemDto -> {
                    OrderItem orderItem = new OrderItem(savedOrderId, itemDto.getId(), itemDto.getPrice(), itemDto.getAmount());
                    orderItemRepository.save(orderItem).subscribe();
                    totalSumArray[0] += orderItem.getItemAmount() * orderItem.getItemPrice();

                    return itemDto;
                })
                .flatMap(itemDto -> cartRepository.deleteAll())
                .blockLast();

        double totalSum = totalSumArray[0];
        //savedOrder.setTotalSum(totalSum);
        // Метод ниже падает с ошибкой:
        // orderRepository.setTotalSum(totalSum, savedOrderId).subscribe();

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
       /* Mono<Order> orderMono = orderRepository.findById(id);
        orderMono.subscribe();
        Mono<OrderDto> orderDtoMono = orderMono.map(order -> new OrderDto(order.getId(), order.getTotalSum()));
        orderDtoMono.subscribe();
        Flux<OrderItem> orderItemFlux
                = orderDtoMono.flatMapMany(orderDto -> orderItemRepository.findAllByOrderId(orderDto.getId()));
        Flux<OrderItemDto> orderItemDtoFlux = orderItemFlux.flatMap(orderItem -> {
            Mono<ItemDto> itemDtoMono = itemRepository.findById(orderItem.getItemId());
            itemDtoMono.subscribe();
            Mono<OrderItemDto> orderItemDtoMono
                    = itemDtoMono.map(itemDto -> new OrderItemDto(orderItem.getOrderId(), orderItem.getOrderId(), itemDto));

            return orderItemDtoMono;
        });

        Iterable<OrderItemDto> orderItemDtoIterable = orderItemDtoFlux.toIterable();
        List<OrderItemDto> orderItemDtoList = new ArrayList<>();
        orderItemDtoIterable.forEach(orderItemDtoList::add);

        orderDtoMono.map(orderDto -> {
            orderDto.setOrderItemDto(orderItemDtoList);
            return orderDto;
        });
        return orderDtoMono.block();*/

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

    private Mono<Boolean> doesCartHaveNotNullItems(Flux<CartItem> cartItems) {
        Mono<Boolean> doesCartHaveNotNullItems = cartItems.map(cartItem -> cartItem.getItemId())
                .flatMap(itemId -> itemRepository.findById(itemId))
                .filter(itemDto -> itemDto.getAmount() > 0)
                .hasElements();

        return doesCartHaveNotNullItems;
    }

    public Mono<Double> getOrdersTotalSum() {
        return orderRepository.getSumOfAllOrders();
    }

    public String getOrdersTotalSumFormatted() {
        Double sumOfAllOrders = getOrdersTotalSum().block();
        return Formatter.DECIMAL_FORMAT.format(sumOfAllOrders != null ? sumOfAllOrders : 0);
    }
}