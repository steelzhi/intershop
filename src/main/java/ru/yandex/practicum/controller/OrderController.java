package ru.yandex.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.service.OrderService;

@Controller
public class OrderController {

    @Autowired
    OrderService orderService;

    @PostMapping("/createOrder")
    public String createOrder(Model model) {
        Order order = orderService.createOrder();
        model.addAttribute("order", order);
        return "order";
    }
}
