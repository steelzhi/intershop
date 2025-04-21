package ru.yandex.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.constant.Constants;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.model.Balance;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.service.CartService;
import ru.yandex.practicum.service.OrderService;

@Controller
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    CartService cartService;

    @Autowired
    WebClient webClient;

    @PostMapping("/create-order")
    public Mono<String> createOrder(Model model) {
        Mono<Order> orderMono = orderService.createOrder();

        Mono<OrderDto> orderDtoMono = orderMono.flatMap(order -> {
            Mono<OrderDto> orderDtoMono1 = orderService.getOrder(order.getId());
            return orderDtoMono1;
        });

        return orderDtoMono
                .doOnNext(orderDto -> {
                    model.addAttribute("orderDto", orderDto);
                    model.addAttribute("balance", Balance.getBalance());
                })
                .flatMap(orderDto -> {
                    if (orderDto.getTotalSum() <= Balance.getBalance()) {
                        System.out.println("Order is successful");

                        // Списываем сумму заказа с баланса
                        webClient.post()
                                .uri(uriBuilder -> uriBuilder
                                        .scheme(Constants.SCHEME)
                                        .host(Constants.HOST)
                                        .port(Constants.PORT)
                                        .path(Constants.ROOT_PATH + "/do-payment")
                                        .queryParam("payment", String.valueOf(orderDto.getTotalSum()))
                                        .build())
                                .exchange()
                                .doOnNext(i -> System.out.println("Deducting order sum from balance"))
                                .subscribe();

                        return Mono.just("order");
                    } else {
                        System.out.println("Order is unsuccessful - not enough money on account");
                        orderService.deleteOrder(orderDto.getId()).subscribe();
                        return Mono.just("not-enough-money-on-account");
                    }
                });
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