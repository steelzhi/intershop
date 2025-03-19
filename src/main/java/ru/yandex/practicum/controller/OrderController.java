package ru.yandex.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping("/createOrder")
    public String createOrder(Model model) {
        Order order = orderService.createOrder();
        if (order != null) {
            model.addAttribute("order", order);
            cartService.getCart().clear();
            return "order";
        } else {
            return "redirect:/main/items";
        }
    }

    @GetMapping("/orders")
    public String getOrders(Model model) {
        List<Order> orders = orderService.getOrders();
        double sumOfAllOrders = orderService.getOrdersTotalSum();
        model.addAttribute("orders", orders);
        model.addAttribute("sumOfAllOrders", sumOfAllOrders);
        return "orders";
    }

    @GetMapping("/orders/{id}")
    public String getOrder(Model model, @PathVariable int id) {
        Order order = orderService.getOrder(id);
        model.addAttribute("order", order);
        return "order";
    }
}
