package ru.yandex.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.enums.PageNames;
import ru.yandex.practicum.service.CartService;
import ru.yandex.practicum.service.ItemService;

import java.util.List;
import java.util.Map;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private ItemService itemService;

    @PostMapping("/cart/add/{id}")
    public String addItemToCart(@PathVariable int id) {
        cartService.addItemToCart(id);
        return "redirect:/main/items";
    }

    @PostMapping("/cart/remove/{id}")
    public String removeItemFromCart(@PathVariable int id, @RequestParam String pageName) {
        cartService.removeItemFromCart(id);
        Map<Integer, ItemDto> existingItemDtos = itemService.getExistingItemsDtos();
        existingItemDtos.get(id).setAmount(0);
        PageNames pageNames = PageNames.valueOf(pageName);
        return switch (pageNames) {
            case MAIN -> "redirect:/main/items";
            case ITEM -> "redirect:/items/" + id;
            case CART -> "redirect:/cart/items";
        };
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
