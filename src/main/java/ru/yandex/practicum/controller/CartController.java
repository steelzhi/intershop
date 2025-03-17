package ru.yandex.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.service.CartService;

import java.util.List;
import java.util.Set;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/cart/add/{id}")
    public String addItemToCart(@PathVariable int id) {
        cartService.addItemToCart(id);
        return "redirect:/main/items";
    }

    @GetMapping("/cart/items")
    public String getCart(Model model) {
        List<ItemDto> itemDtos = cartService.getItemsDtosInCart();
        double totalPrice = cartService.getTotalPrice();
        model.addAttribute("items", itemDtos);
        model.addAttribute("total", totalPrice);
        return "cart";
    }
}
