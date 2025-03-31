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
import ru.yandex.practicum.model.OrderItem;
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
        Order order = orderService.createOrder().block();
        int orderId = order.getId();
        OrderDto orderDto = orderService.getOrder(orderId);
        model.addAttribute("orderDto", orderDto);
        if (order != null) {
            model.addAttribute("orderDto", orderDto);
            //cartService.getCart().clear();
            return Mono.just("order");
        } else {
            return Mono.just("redirect:/main/items");
        }
    }

    @GetMapping("/orders")
    public Mono<String> getOrders(Model model) {
        List<OrderDto> ordersDto = orderService.getOrders();
        String sumOfAllOrdersFormatted = orderService.getOrdersTotalSumFormatted();
        model.addAttribute("ordersDto", ordersDto);
        model.addAttribute("sumOfAllOrdersFormatted", sumOfAllOrdersFormatted);

        return Mono.just("orders");
    }

    @GetMapping("/orders/{id}")
    public Mono<String> getOrder(Model model, @PathVariable int id) {
        OrderDto orderDto = orderService.getOrder(id);
        model.addAttribute("orderDto", orderDto);
        return Mono.just("order");
    }
}
