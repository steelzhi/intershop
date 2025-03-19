package ru.yandex.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.enums.PageNames;
import ru.yandex.practicum.enums.SortingCategory;
import ru.yandex.practicum.model.Item;
import ru.yandex.practicum.model.Pages;
import ru.yandex.practicum.service.ItemService;

import java.io.IOException;
import java.util.List;

@Controller
public class ItemController {
    public static final int ITEMS_ON_PAGE_DEFAULT = 10;
    public static final int PAGE_NUMBER_FIRST = 1;

    @Autowired
    private ItemService itemService;

    @GetMapping(value = {"/", "/main/items"})
    public String getItemsList(Model model,
                               @RequestParam(name = "itemsOnPage", required = false) Integer itemsOnPage,
                               @RequestParam(name = "pageNumber", required = false) Integer pageNumber) throws IOException {
        if (itemsOnPage == null) {
            itemsOnPage = ITEMS_ON_PAGE_DEFAULT;
            pageNumber = PAGE_NUMBER_FIRST;
        }
        List<ItemDto> itemList = itemService.getItemsList(itemsOnPage, pageNumber);
        int itemListFullSize = itemService.getItemListSize();
        Pages pages = new Pages(itemsOnPage, (itemListFullSize - 1) / itemsOnPage + 1);
        model.addAttribute("items", itemList);
        model.addAttribute("pages", pages);
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
    public String decreaseItemAmount(@PathVariable int id, @RequestParam String pageName) throws IOException {
        itemService.decreaseItemAmount(id);
        PageNames pageNames = PageNames.valueOf(pageName);
        return switch (pageNames) {
            case MAIN -> "redirect:/main/items";
            case ITEM -> "redirect:/items/" + id;
            case CART -> "redirect:/cart/items";
        };
    }

    @PostMapping("/item/{id}/plus")
    public String increaseItemAmount(@PathVariable int id, @RequestParam String pageName) throws IOException {
        itemService.increaseItemAmount(id);
        PageNames pageNames = PageNames.valueOf(pageName);
        return switch (pageNames) {
            case MAIN -> "redirect:/main/items";
            case ITEM -> "redirect:/items/" + id;
            case CART -> "redirect:/cart/items";
        };
    }
}
