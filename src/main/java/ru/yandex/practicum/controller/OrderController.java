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

import java.util.List;

@Controller
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    CartService cartService;

    @PostMapping("/create-order")
    public Mono<String> createOrder(Model model) {
/*        Рабочий метод с блокировкой
Order order = orderService.createOrder().block();
        if (order != null) {
            int orderId = order.getId();
            Mono<OrderDto> orderDtoMono = orderService.getOrder(orderId);
            model.addAttribute("orderDto", orderDtoMono);
            //cartService.getCart().clear();
            return Mono.just("order");
        } else {
            return Mono.just("redirect:/main/items");
        }*/

        Mono<Order> orderMono = orderService.createOrder();
        //orderMono.subscribe(order -> System.out.println(order.toString().isEmpty() ? "Order is empty" : order));

        Mono<Boolean> hasElement1 = orderMono.hasElement();
        orderMono
                .then(hasElement1)
                .subscribe(i -> System.out.println(i));


        Mono<String> stringMono = orderMono.hasElement()
                .flatMap(hasElement -> {
                    if (hasElement) {
                        Mono<OrderDto> orderDtoMono = orderMono.flatMap(order1 -> orderService.getOrder(order1.getId()));
                        model.addAttribute("orderDto", orderDtoMono);
                        return Mono.just("order");
                    } else {
                        return Mono.just("redirect:/main/items");
                    }
                });

        orderMono
                .then(stringMono)
                .subscribe(s -> System.out.println(s));

/*        orderMono
                .then(stringMono)
                .subscribe();*/
        return stringMono;
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