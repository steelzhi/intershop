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

    public Mono<Order> createOrder(String username) {
        int[] orderId = new int[1];
        double[] totalSumArray = new double[1];

        Order order = new Order(username);
        Mono<Order> orderMono = orderRepository.save(order);

        Flux<CartItem> cartItemsFlux = cartRepository.findAllByUsername(username);
        Flux<ItemDto> itemDtosFlux
                = cartItemsFlux.flatMap(cartItem -> itemRepository.findById(cartItem.getItemId()))
                .filter(itemDto -> itemDto.getAmount() > 0)
                .doOnNext(itemDto -> saveOrderItemAndIncreaseTotalSum(itemDto, orderId, totalSumArray));

        Mono<Void> voidMono = orderMono
                .doOnNext(savedOrder -> orderId[0] = savedOrder.getId())
                .thenMany(itemDtosFlux
                        .doOnNext(itemDto -> System.out.println(itemDto + " was saved in DB")))
                .flatMap(itemDto -> orderRepository.findById(orderId[0]))
                .doOnNext(order1 -> setTotalSumAndSaveOrder(order1, totalSumArray))
                .then(cartRepository.deleteAll()
                        .doOnNext(i -> System.out.println("All cartItems were deleted from DB")));

        return voidMono
                .then(Mono.just(order))
                .doOnNext(i -> System.out.println("Returning from OrderService#createOrder"));
    }

    public Flux<OrderDto> getOrders(String username) {
        Flux<Order> orderFlux = orderRepository.findAllByUsername(username);
        Flux<OrderDto> orderDtoFlux = orderFlux.flatMap(order -> {
            Mono<OrderDto> orderDto = getOrder(order.getId());

            return orderDto;
        });

        return orderDtoFlux;
    }

    public Mono<OrderDto> getOrder(int id) {
        Mono<Order> orderMono = orderRepository.findById(id);
        Mono<OrderDto> orderDtoMono = orderMono.map(order1 -> new OrderDto(order1.getId(), order1.getTotalSum()));
        Flux<OrderItem> orderItemFlux = orderDtoMono
                .flatMapMany(orderDto1 -> orderItemRepository.findAllByOrderId(orderDto1.getId()));
        List<OrderItemDto> orderItemDtoList = new ArrayList<>();

        Flux<OrderItemDto> orderItemDtoFlux
                = orderItemFlux.flatMap(orderItem -> getDtoFromOrderItem(orderItem, orderItemDtoList));

        Mono<OrderDto> orderDtoWithOrderItemsMono = orderDtoMono
                .doOnNext(orderDto -> orderDto.setOrderItemDtoList(orderItemDtoList));

        Mono<OrderDto> orderDtoMono1 = orderItemDtoFlux
                .then(orderDtoWithOrderItemsMono);

        return orderDtoMono1;
    }

    public Mono<Double> getOrdersTotalSum(String username) {
        return orderRepository.getSumOfAllOrdersForUser(username);
    }

    public Mono<String> getOrdersTotalSumFormatted(String username) {
        Mono<Double> sumOfAllOrdersMono = getOrdersTotalSum(username);
        Mono<String> sumOfAllOrdersFormattedMono = sumOfAllOrdersMono
                .map(sumOfAllOrders -> Formatter.DECIMAL_FORMAT.format(
                        sumOfAllOrders != null ? sumOfAllOrders : 0));

        return sumOfAllOrdersFormattedMono;
    }

    public Mono<Void> deleteOrder(int orderId) {
        return orderRepository.deleteById(orderId);
    }

    private void setTotalSumAndSaveOrder(Order order, double[] totalSumArray) {
        order.setTotalSum(totalSumArray[0]);
        orderRepository.save(order).subscribe();
    }

    private void saveOrderItemAndIncreaseTotalSum(ItemDto itemDto, int[] orderId, double[] totalSumArray) {
        OrderItem orderItem = new OrderItem(orderId[0], itemDto.getId(), itemDto.getPrice(), itemDto.getAmount());
        orderItemRepository.save(orderItem).subscribe();
        totalSumArray[0] += orderItem.getItemAmount() * orderItem.getItemPrice();
    }

    private Mono<OrderItemDto> getDtoFromOrderItem(OrderItem orderItem, List<OrderItemDto> orderItemDtoList) {
        Mono<ItemDto> itemDtoMono = itemRepository.findById(orderItem.getItemId());
        Mono<OrderItemDto> orderItemDtoMono = itemDtoMono.map(itemDto -> {
            OrderItemDto orderItemDto
                    = new OrderItemDto(orderItem.getId(), orderItem.getOrderId(), orderItem.getItemAmount(), itemDto);
            if (!orderItemDtoList.contains(orderItemDto)) {
                orderItemDtoList.add(orderItemDto);
            }
            return orderItemDto;
        });

        return orderItemDtoMono;
    }

    private Mono<Boolean> doesCartHaveNotNullItems(Flux<CartItem> cartItems) {
        Mono<Boolean> doesCartHaveNotNullItems = cartItems.map(cartItem -> cartItem.getItemId())
                .flatMap(itemId -> itemRepository.findById(itemId))
                .filter(itemDto -> itemDto.getAmount() > 0)
                .hasElements();

        return doesCartHaveNotNullItems;
    }
}