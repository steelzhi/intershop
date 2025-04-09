package ru.yandex.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.service.CartService;
import ru.yandex.practicum.service.OrderService;

@Controller
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    CartService cartService;

    @PostMapping("/create-order")
    public Mono<String> createOrder(Model model) {
        Mono<Order> orderMono = orderService.createOrder();

        Mono<OrderDto> orderDtoMono = orderMono.flatMap(order -> {
            Mono<OrderDto> orderDtoMono1 = orderService.getOrder(order.getId());
            orderDtoMono1
                    .doOnNext(orderDto -> System.out.println("This is OrderDto: " + orderDto))
                    .subscribe();
            return orderDtoMono1;
        });

        return orderDtoMono // orderDtoMono - Mono, содержащий заказ с добавленными товарами
                .map(orderDto -> {
                    System.out.println("This is OrderDto: " + orderDto);
                    model.addAttribute("orderDto", orderDto); // добавляем OrderDto во view для дальнейшего отображения
                    System.out.println(orderDto + " was added to view");
                    return orderDto;
                })
                .then(Mono.just("order") // Возвращаем view "order"
                        .doOnNext(s -> System.out.println("Viewing data from: " + s)));

                /* Mono<String> stringMono = orderMono.hasElement()
                .flatMap(hasElement -> {
                    if (hasElement) {
                        Mono<OrderDto> orderDtoMono = orderMono.flatMap(order1 -> orderService.getOrder(order1.getId()));
                        orderDtoMono.subscribe();
                        model.addAttribute("orderDto", orderDtoMono);
                        return Mono.just("order");
                    } else {
                        return Mono.just("redirect:/main/items");
                    }
                });*/
    }

    @GetMapping("/orders")
    public Mono<String> getOrders(Model model) {
        Flux<OrderDto> orderDtoFlux = orderService.getOrders();
        Mono<String> sumOfAllOrdersFormatted = orderService.getOrdersTotalSumFormatted();
        model.addAttribute("ordersDto", orderDtoFlux);
        model.addAttribute("sumOfAllOrdersFormatted", sumOfAllOrdersFormatted);

        return Mono.just("orders");
    }

    @GetMapping("/orders/{id}")
    public Mono<String> getOrder(Model model, @PathVariable int id) {
        Mono<OrderDto> orderDtoMono = orderService.getOrder(id);
        model.addAttribute("orderDto", orderDtoMono);
        return Mono.just("order");
    }
}