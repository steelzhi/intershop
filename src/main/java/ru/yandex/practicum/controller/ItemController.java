package ru.yandex.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.ItemDto;
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
    public String addItem(@ModelAttribute Item item) throws IOException {
        //return itemService.addItem(new Item("item1", null, 100.0));
        itemService.addItem(item);
        return "redirect:/main/items";
    }
}
