package ru.yandex.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.enums.PageNames;
import ru.yandex.practicum.service.CartService;
import ru.yandex.practicum.service.ItemService;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private ItemService itemService;

    @PostMapping("/cart/add/{id}")
    public Mono<String> addItemToCart(@PathVariable int id) {
        cartService.addItemToCart(id).subscribe();
        return Mono.just("redirect:/main/items");
    }

    @PostMapping("/cart/remove/{id}")
    public Mono<String> removeItemFromCart(ServerWebExchange exchange, @PathVariable int id) {
        return exchange.getFormData()
                .flatMap(formData -> {
                    String pageName = formData.getFirst("pageName");
                    cartService.removeItemFromCart(id).subscribe();

                    //itemService.setInExistingItemDtosItemDtoAmountToZero(id);
                    PageNames pageNames = PageNames.valueOf(pageName);
                    return switch (pageNames) {
                        case MAIN -> Mono.just("redirect:/main/items");
                        case ITEM -> Mono.just("redirect:/items/" + id);
                        case CART -> Mono.just("redirect:/cart/items");
                    };
                });
    }

    @GetMapping("/cart/items")
    public Mono<String> getCart(Model model) {
        Flux<ItemDto> itemDtosFlux = cartService.getItemsDtosInCart();
        Mono<String> totalPriceFormattedMono = cartService.getTotalPriceFormatted(itemDtosFlux);

        model.addAttribute("items", itemDtosFlux);
        model.addAttribute("totalPriceFormatted", totalPriceFormattedMono);
        return Mono.just("cart");
    }
}
