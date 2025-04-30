package ru.yandex.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
import ru.yandex.practicum.service.ItemAllOtherOpsService;
import ru.yandex.practicum.service.ItemGettingFromCacheService;
import ru.yandex.practicum.util.RedirectionPage;

import java.io.IOException;
import java.security.Principal;

@Controller
public class ItemController {
    private int itemsOnPageDefaultForAllItems = 10;
    private int pageNumberDefault = 1;
    private boolean wasCacheCleared = false;

    @Autowired
    private ItemGettingFromCacheService itemService;

    @Autowired
    private ItemAllOtherOpsService itemsService;

    @GetMapping(value = {"/", "/main/items"})
    public Mono<String> getItemsList(
            Model model,
            Principal principal,
            @RequestParam(name = "itemsOnPage", required = false) Integer itemsOnPage,
            @RequestParam(name = "pageNumber", required = false) Integer pageNumber) throws IOException {
        // Нужно зачищать кэш на старте очередного запуска программы
        if (!wasCacheCleared) {
            itemsService.clearCache();
            wasCacheCleared = true;
        }

        /*
         * Действие ниже нужно, чтобы при изменении количества товаров на странице такое новое количество фиксировалось
         * и до последующего его изменения на странице всегда отображалось именно такое количество товаров
         */
        itemsOnPage = getHandledItemsOnPage(itemsOnPage);
        pageNumber = getHandledItemsPageNumber(pageNumber);
        //       Flux<ItemDto> itemsFlux = itemService.getItemsList(itemsOnPage, pageNumber);
        Flux<ItemDto> itemsFlux = itemsService.getItemsList(itemsOnPage, pageNumber);

        Mono<Pages> pagesMono = getPages(itemsOnPage);

        model.addAttribute("principal", principal);
        model.addAttribute("items", itemsFlux);
        model.addAttribute("pages", pagesMono);
        return Mono.just("main");
    }

    @GetMapping("/items/{id}")
    public Mono<String> getItemDto(Model model, Principal principal, @PathVariable int id) {
        Mono<ItemDto> itemDtoMono = itemsService.getItemDto(id);
        return itemDtoMono.doOnNext(itemDto -> System.out.println("ItemDto amount: " + itemDto.getAmount()))
                .doOnNext(itemDto -> {
                    model.addAttribute("itemDto", itemDtoMono);
                    model.addAttribute("principal", principal);
                })
                .flatMap(itemDto -> Mono.just("item"));
    }

    @GetMapping("/search")
    public Mono<String> search(Model model, @RequestParam String key, @RequestParam SortingCategory sortingCategory) {
        Flux<ItemDto> foundItemDtos = itemsService.search(key, sortingCategory);
        model.addAttribute("items", foundItemDtos);
        Pages pages = new Pages();
        model.addAttribute("pages", pages);

        return Mono.just("main");
    }

    @PostMapping(value = "/item")
    public Mono<String> addItemToList(@ModelAttribute Mono<Item> itemMono) {
        return itemMono
                .flatMap(item -> itemsService.addItem(item))
                .then(Mono.just("redirect:/main/items"));
    }

    @PostMapping("/item/{id}/minus")
    public Mono<String> decreaseItemAmount(ServerWebExchange exchange, @PathVariable int id) {
        return itemsService.decreaseItemAmount(id)
                .then(exchange.getFormData()
                        .flatMap(formData -> {
                            String pageName = formData.getFirst("pageName");
                            PageNames pageNames = PageNames.valueOf(pageName);
                            Mono<String> redirectionPageMono = RedirectionPage.getRedirectionPage(pageNames, id);
                            return redirectionPageMono;
                        }));
    }

    @PostMapping("/item/{id}/plus")
    public Mono<String> increaseItemAmount(ServerWebExchange exchange, @PathVariable int id) {
        return itemsService.increaseItemAmount(id)
                .then(exchange.getFormData()
                        .flatMap(formData -> {
                            String pageName = formData.getFirst("pageName");
                            PageNames pageNames = PageNames.valueOf(pageName);
                            Mono<String> redirectionPageMono = RedirectionPage.getRedirectionPage(pageNames, id);
                            return redirectionPageMono;
                        }));
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
        return itemsService.getItemListSize()
                .map(amount -> new Pages(itemsOnPage, (amount - 1) / itemsOnPage + 1));
    }
}
