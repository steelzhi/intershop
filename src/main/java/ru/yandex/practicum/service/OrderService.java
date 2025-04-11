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
        int[] orderId = new int[1];
        double[] totalSumArray = new double[1];

        Order order = new Order();
        Mono<Order> orderMono = orderRepository.save(order);

        Flux<CartItem> cartItemsFlux = cartRepository.findAll();
        Flux<ItemDto> itemDtosFlux = cartItemsFlux.flatMap(cartItem -> itemRepository.findById(cartItem.getItemId()))
                .filter(itemDto -> itemDto.getAmount() > 0)
                .doOnNext(itemDto -> {
                    OrderItem orderItem = new OrderItem(orderId[0], itemDto.getId(), itemDto.getPrice(), itemDto.getAmount());
                    orderItemRepository.save(orderItem).subscribe();
                    totalSumArray[0] += orderItem.getItemAmount() * orderItem.getItemPrice();
                });

        Mono<Void> voidMono = orderMono
                .doOnNext(order1 -> System.out.println(order1 + " was saved in DB"))
                .map(savedOrder -> {
                    orderId[0] = savedOrder.getId();
                    System.out.println("Order got id = " + orderId[0]);
                    return savedOrder;
                })
                .thenMany(itemDtosFlux
                        .doOnNext(itemDto -> System.out.println(itemDto + " was saved in DB")))
                .flatMap(itemDto -> orderRepository.findById(orderId[0]))
                .doOnNext(order1 -> {
                    order1.setTotalSum(totalSumArray[0]);
                    orderRepository.save(order1).subscribe();
                    System.out.println("totalSum = " + totalSumArray[0] + ". Order saved with totalSum");
                })
                .then(cartRepository.deleteAll()
                        .doOnNext(i -> System.out.println("All cartItems were deleted from DB")));

        return voidMono
                .then(Mono.just(order))
                .doOnNext(i -> System.out.println("Returning from OrderService#createOrder"));
    }

    public Flux<OrderDto> getOrders() {
        Flux<Order> orderFlux = orderRepository.findAll();
        Flux<OrderDto> orderDtoFlux = orderFlux.flatMap(order -> {
            Mono<OrderDto> orderDto;
            orderDto = getOrder(order.getId());

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

        Flux<OrderItemDto> orderItemDtoFlux = orderItemFlux.flatMap(orderItem -> {
            Mono<ItemDto> itemDtoMono = itemRepository.findById(orderItem.getItemId());
            Mono<OrderItemDto> orderItemDtoMono = itemDtoMono.map(itemDto -> {
                OrderItemDto orderItemDto
                        = new OrderItemDto(orderItem.getId(), orderItem.getOrderId(), orderItem.getItemAmount(), itemDto);
                orderItemDtoList.add(orderItemDto);
                System.out.println("OrderItemDto " + orderItemDto + " was created and added to list");
                return orderItemDto;
            });

            System.out.println("Returning created orderItemDtoMono");
            return orderItemDtoMono;
        });

        Mono<OrderDto> orderDtoWithOrderItemsMono = orderDtoMono
                .doOnNext(orderDto -> orderDto.setOrderItemDtoList(orderItemDtoList));

        Mono<OrderDto> orderDtoMono1 = orderItemDtoFlux
                .doOnNext(orderItemDto -> System.out.println("Entering OrderService#getOrder"))
                .doOnNext(orderItemDto -> System.out.println(orderItemDto + " was created from itemDto"))
                .then(orderDtoWithOrderItemsMono);

        return orderDtoMono1;
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

    public Mono<String> getOrdersTotalSumFormatted() {
        Mono<Double> sumOfAllOrdersMono = getOrdersTotalSum();
        Mono<String> sumOfAllOrdersFormattedMono = sumOfAllOrdersMono
                .map(sumOfAllOrders -> Formatter.DECIMAL_FORMAT.format(sumOfAllOrders != null ? sumOfAllOrders : 0));

        return sumOfAllOrdersFormattedMono;
    }
}