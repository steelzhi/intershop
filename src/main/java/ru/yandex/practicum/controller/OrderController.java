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
            return orderDtoMono1;
        });

        return orderDtoMono
                .doOnNext(orderDto -> model.addAttribute("orderDto", orderDto))
                .then(Mono.just("order")
                        .doOnNext(s -> System.out.println("Viewing data from: " + s)));
    }

    @GetMapping("/orders")
    public Mono<String> getOrders(Model model) {
        Flux<OrderDto> orderDtoFlux = orderService.getOrders();
        Mono<String> sumOfAllOrdersFormatted = orderService.getOrdersTotalSumFormatted();
        Mono<String> mono = orderDtoFlux
                .doOnNext(i -> model.addAttribute("ordersDto", orderDtoFlux))
                .then(sumOfAllOrdersFormatted)
                .doOnNext(i -> model.addAttribute("sumOfAllOrdersFormatted", sumOfAllOrdersFormatted))
                .flatMap(s -> Mono.just("orders"));

        return mono;
    }

    @GetMapping("/orders/{id}")
    public Mono<String> getOrder(Model model, @PathVariable int id) {
        Mono<OrderDto> orderDtoMono = orderService.getOrder(id);
        Mono<String> mono = orderDtoMono
                .doOnNext(i -> model.addAttribute("orderDto", orderDtoMono))
                .flatMap(s -> Mono.just("order"));

        return mono;
    }
}