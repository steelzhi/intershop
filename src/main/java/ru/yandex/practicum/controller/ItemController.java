package ru.yandex.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.enums.PageNames;
import ru.yandex.practicum.enums.SortingCategory;
import ru.yandex.practicum.model.Item;
import ru.yandex.practicum.model.Pages;
import ru.yandex.practicum.service.ItemService;

import java.io.IOException;

@Controller
public class ItemController {
    private int itemsOnPageDefaultForAllItems = 10;
    private int pageNumberDefault = 1;

    @Autowired
    private ItemService itemService;

    @GetMapping(value = {"/", "/main/items"})
    public Mono<String> getItemsList(Model model,
                                     @RequestParam(name = "itemsOnPage", required = false) Integer itemsOnPage,
                                     @RequestParam(name = "pageNumber", required = false) Integer pageNumber) throws IOException {

        /*
         * Действие ниже нужно, чтобы при изменении количества товаров на странице такое новое количество фиксировалось
         * и до последующего его изменения на странице всегда отображалось именно такое количество товаров
         */
        itemsOnPage = getHandledItemsOnPage(itemsOnPage);
        pageNumber = getHandledItemsPageNumber(pageNumber);
        Flux<ItemDto> itemList = itemService.getItemsList(itemsOnPage, pageNumber);

        Mono<Pages> pagesMono = getPages(itemsOnPage);

        model.addAttribute("items", itemList);
        model.addAttribute("pages", pagesMono);
        return Mono.just("main");
    }

    @GetMapping("/items/{id}")
    public String getItemDto(Model model, @PathVariable int id) throws IOException {
        Mono<ItemDto> itemDto = itemService.getItemDto(id);
        model.addAttribute("itemDto", itemDto);
        return "item";
    }

    @GetMapping("/search")
    public Mono<String> search(Model model, @RequestParam String key, @RequestParam SortingCategory sortingCategory) {
        Flux<ItemDto> foundItemDtos = itemService.search(key, sortingCategory);
        model.addAttribute("items", foundItemDtos);
        Pages pages = new Pages();
        model.addAttribute("pages", pages);

        return Mono.just("main");
    }

/*    @PostMapping("/item")
    public Mono<String> addItemToList(ServerRequest request) throws IOException {
        itemService.addItem(item);
        return Mono.just("redirect:/main/items");
    }*/

    @PostMapping(value = "/item", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> addItemToList(@ModelAttribute Mono<Item> itemMono) {
        try {
            itemMono
                    .flatMap(item -> {
                        try {
                            Mono<ItemDto> itemDtoMono = itemService.addItem(item);
                            return itemDtoMono;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .subscribe(System.out::println);

            return Mono.just("redirect:/main/items");
        } catch (Exception e) {
            e.printStackTrace();;
        }

        return null;
    }

    @PostMapping("/item/{id}/minus")
    public Mono<String> decreaseItemAmount(ServerWebExchange exchange, @PathVariable int id) {
        return exchange.getFormData()
                .flatMap(formData -> {
                    String pageName = formData.getFirst("pageName");
                    itemService.decreaseItemAmount(id);
                    PageNames pageNames = PageNames.valueOf(pageName);
                    return switch (pageNames) {
                        case MAIN -> Mono.just("redirect:/main/items");
                        case ITEM -> Mono.just("redirect:/items/" + id);
                        case CART -> Mono.just("redirect:/cart/items");
                    };
                });
    }

    @PostMapping("/item/{id}/plus")
    public Mono<String> increaseItemAmount(ServerWebExchange exchange, @PathVariable int id) {
        return exchange.getFormData()
                .flatMap(formData -> {
                    String pageName = formData.getFirst("pageName");
                    itemService.increaseItemAmount(id);
                    PageNames pageNames = PageNames.valueOf(pageName);
                    return switch (pageNames) {
                        case MAIN -> Mono.just("redirect:/main/items");
                        case ITEM -> Mono.just("redirect:/items/" + id);
                        case CART -> Mono.just("redirect:/cart/items");
                    };
                });
    }

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

    private Mono<Pages> getPages(Integer itemsOnPage) {
        return itemService.getItemListSize()
                .map(amount -> new Pages(itemsOnPage, (amount - 1) / itemsOnPage + 1));
    }
}
