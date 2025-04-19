package ru.yandex.practicum.util;

import reactor.core.publisher.Mono;
import ru.yandex.practicum.enums.PageNames;

public class RedirectionPage {
    private RedirectionPage(){
    }

    public static Mono<String> getRedirectionPage(PageNames pageNames, int id) {
        return switch (pageNames) {
            case MAIN -> Mono.just("redirect:/main/items");
            case ITEM -> Mono.just("redirect:/items/" + id);
            case CART -> Mono.just("redirect:/cart/items");
        };
    }
}
