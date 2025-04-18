package ru.yandex.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.enums.PageNames;
import ru.yandex.practicum.model.Balance;
import ru.yandex.practicum.service.CartService;
import ru.yandex.practicum.service.ItemService;
import ru.yandex.practicum.util.RedirectionPage;

@Controller
public class CartController {
    private static final String SCHEME = "http";
    private static final String HOST = "localhost";
    private static final String PORT = "9090";
    private static final String ROOT_PATH = "/payments";

    @Autowired
    private CartService cartService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private WebClient webClient;

    @PostMapping("/cart/add/{id}")
    public Mono<String> addItemToCart(@PathVariable int id) {
        cartService.addItemToCart(id);
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
                    Mono<String> redirectionPageMono = RedirectionPage.getRedirectionPage(pageNames, id);
                    return redirectionPageMono;
                });
    }

    @GetMapping("/cart/items")
    public Mono<String> getCart(Model model) {
        Flux<ItemDto> itemDtosFlux = cartService.getItemsDtosInCart();
        Mono<String> totalPriceFormattedMono = cartService.getTotalPriceFormatted(itemDtosFlux);

        boolean[] isPaymentServiceAvailable = new boolean[1];
        isPaymentServiceAvailable[0] = true;

        Mono<Double> balanceMono = webClient.get()
                .uri(SCHEME + "://" + HOST + ":" + PORT + ROOT_PATH + "/balance")
                .retrieve()
                .toEntity(Double.class)
                .flatMap(doubleResponseEntity -> {
                    double balance = doubleResponseEntity.getBody();
                    // Запишем текущий баланс в поле класса, чтобы не запрашивать баланс еще раз в OrderController
                    Balance.setBalance(balance);
                    return Mono.just(balance);
                })
                .onErrorResume(error -> {
                    System.out.println("1: PaymentService is not available");
                    isPaymentServiceAvailable[0] = false;
                    return Mono.empty();
                });

        model.addAttribute("items", itemDtosFlux);
        model.addAttribute("totalPriceFormatted", totalPriceFormattedMono);

        return balanceMono
                .doOnNext(i -> {
                    model.addAttribute("isPaymentServiceAvailable", isPaymentServiceAvailable[0]);
                    if (!isPaymentServiceAvailable[0]) {
                        System.out.println("2: PaymentService is not available");
                    }
                })
                .then(Mono.just("cart"));
    }
}
