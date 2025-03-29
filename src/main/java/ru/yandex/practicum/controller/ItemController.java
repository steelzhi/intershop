package ru.yandex.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
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
    private int itemsOnPageDefaultForAllItems = 10;
    private int pageNumberDefault = 1;

    @Autowired
    private ItemService itemService;

    @GetMapping(value = {"/", "/main/items"})
    public Mono<String> getItemsList(Model model,
                                      @RequestParam(name = "itemsOnPage", required = false) Integer itemsOnPage,
                                      @RequestParam(name = "pageNumber", required = false) Integer pageNumber) {

        /*
        * Действие ниже нужно, чтобы при изменении количества товаров на странице такое новое количество фиксировалось
        * и до последующего его изменения на странице всегда отображалось именно такое количество товаров
         */
        itemsOnPage = getHandledItemsOnPage(itemsOnPage);
        pageNumber = getHandledItemsPageNumber(pageNumber);
        Flux<ItemDto> itemList = itemService.getItemsList(itemsOnPage, pageNumber);
        //ItemDto itemList2 = itemList.blockFirst();

        Pages pages = getPages(itemsOnPage);
        model.addAttribute("items", itemList);
        model.addAttribute("pages", pages);
        return Mono.just("main");
    }


/*    @GetMapping("/items/{id}")
    public String getItemDto(Model model, @PathVariable int id) throws IOException {
        ItemDto itemDto = itemService.getItemDto(id);
        model.addAttribute("itemDto", itemDto);
        return "item";
    }

    @GetMapping("/search")
    public String search(Model model, @RequestParam String key, @RequestParam SortingCategory sortingCategory) {
        List<ItemDto> foundItemDtos = itemService.search(key, sortingCategory);
        model.addAttribute("items", foundItemDtos);
        Pages pages = new Pages();
        model.addAttribute("pages", pages);

        return "main";
    }*/

    @PostMapping("/item")
    public Mono<String> addItemToList(@ModelAttribute Item item) throws IOException {
        itemService.addItem(item).subscribe();
        return Mono.just("redirect:/main/items");
    }

    /*@PostMapping("/item/{id}/minus")
    public String decreaseItemAmount(@PathVariable int id, @RequestParam String pageName) {
        itemService.decreaseItemAmount(id);
        PageNames pageNames = PageNames.valueOf(pageName);
        return switch (pageNames) {
            case MAIN -> "redirect:/main/items";
            case ITEM -> "redirect:/items/" + id;
            case CART -> "redirect:/cart/items";
        };
    }

    @PostMapping("/item/{id}/plus")
    public String increaseItemAmount(@PathVariable int id, @RequestParam String pageName) {
        itemService.increaseItemAmount(id);
        PageNames pageNames = PageNames.valueOf(pageName);
        return switch (pageNames) {
            case MAIN -> "redirect:/main/items";
            case ITEM -> "redirect:/items/" + id;
            case CART -> "redirect:/cart/items";
        };
    }*/

    private int getHandledItemsOnPage(Integer itemsOnPage) {
        if (itemsOnPage == null) {
            itemsOnPage = itemsOnPageDefaultForAllItems;
        } else {
            itemsOnPageDefaultForAllItems = itemsOnPage;
        }
        return itemsOnPage;
    }

    private int getHandledItemsPageNumber(Integer pageNumber) {
        if (pageNumber == null) {
            pageNumber = pageNumberDefault;
        }
        return pageNumber;
    }

    private Pages getPages(Integer itemsOnPage) {
        int itemListFullSize = itemService.getItemListSize().block();
        return new Pages(itemsOnPage, (itemListFullSize - 1) / itemsOnPage + 1);
    }
}
