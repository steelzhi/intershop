package ru.yandex.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
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
        Mono<Order> order = orderService.createOrder();
        Flux<OrderItem> orderItemFlux = orderService.getOrderItems();
        if (order != null) {
            model.addAttribute("order", order.block());
            model.addAttribute("orderItems", orderItemFlux.toIterable());
            //cartService.getCart().clear();
            return Mono.just("order");
        } else {
            return Mono.just("redirect:/main/items");
        }
    }

    /*@GetMapping("/orders")
    public String getOrders(Model model) {
        List<Order> orders = orderService.getOrders();
        String sumOfAllOrdersFormatted = orderService.getOrdersTotalSumFormatted();
        model.addAttribute("sumOfAllOrdersFormatted", sumOfAllOrdersFormatted);
        model.addAttribute("orders", orders);
        return "orders";
    }

    @GetMapping("/orders/{id}")
    public String getOrder(Model model, @PathVariable int id) {
        Order order = orderService.getOrder(id);
        model.addAttribute("order", order);
        return "order";
    }*/
}
