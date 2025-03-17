package ru.yandex.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.enums.PageNames;
import ru.yandex.practicum.enums.SortingCategory;
import ru.yandex.practicum.model.Item;
import ru.yandex.practicum.service.ItemService;

import java.io.IOException;
import java.util.List;

@Controller
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping(value = {"/", "/main/items"})
    public String getItemsList(Model model) throws IOException {
        List<List<ItemDto>> itemList = itemService.getItemsList();
        model.addAttribute("items", itemList);
        return "main";
    }

    @GetMapping("/items/{id}")
    public String getItemDto(Model model, @PathVariable int id) throws IOException {
        ItemDto itemDto = itemService.getItemDto(id);
        model.addAttribute("itemDto", itemDto);
        return "item";
    }

    @GetMapping("/search")
    public String search(Model model, @RequestParam String key, @RequestParam SortingCategory sortingCategory) {
        List<ItemDto> foundItemDtos = itemService.search(key, sortingCategory);
        model.addAttribute("items", foundItemDtos);
        return "main";
    }


    @PostMapping("/item")
    public String addItemToList(@ModelAttribute Item item) throws IOException {
        itemService.addItem(item);
        return "redirect:/main/items";
    }

    @PostMapping("/item/{id}/minus")
    public String decreaseItemAmountOnMainPage(@PathVariable int id, @RequestParam String pageName) throws IOException {
        itemService.decreaseItemAmount(id);
        PageNames pageNames = PageNames.valueOf(pageName);
        return switch (pageNames) {
            case MAIN -> "redirect:/main/items";
            case ITEM -> "redirect:/items/" + id;
            case CART -> "redirect:/cart/items";
        };
    }

    @PostMapping("/item/{id}/plus")
    public String increaseItemAmountOnMainPage(@PathVariable int id, @RequestParam String pageName) throws IOException {
        itemService.increaseItemAmount(id);
        PageNames pageNames = PageNames.valueOf(pageName);
        return switch (pageNames) {
            case MAIN -> "redirect:/main/items";
            case ITEM -> "redirect:/items/" + id;
            case CART -> "redirect:/cart/items";
        };
    }
}
