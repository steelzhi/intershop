package ru.yandex.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.constant.Constants;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.enums.PageNames;
import ru.yandex.practicum.model.Balance;
import ru.yandex.practicum.service.CartService;
import ru.yandex.practicum.service.ItemGettingFromCacheService;
import ru.yandex.practicum.util.RedirectionPage;
import ru.yandex.practicum.util.TokenStringMono;

import java.security.Principal;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private ItemGettingFromCacheService itemService;

    @Autowired
    private WebClient webClient;

    @Autowired
    ReactiveOAuth2AuthorizedClientManager manager;

    @PostMapping("/cart/add/{id}")
    public Mono<String> addItemToCart(@PathVariable int id, Principal principal) {
        String username = principal.getName();

        cartService.addItemToCart(id, username);
        return Mono.just("redirect:/main/items");
    }

    @PostMapping("/cart/remove/{id}")
    public Mono<String> removeItemFromCart(ServerWebExchange exchange, @PathVariable int id, Principal principal) {
        String username = principal.getName();

        return exchange.getFormData()
                .flatMap(formData -> {
                    String pageName = formData.getFirst("pageName");
                    cartService.removeItemFromCart(id, username).subscribe();

                    //itemService.setInExistingItemDtosItemDtoAmountToZero(id);
                    PageNames pageNames = PageNames.valueOf(pageName);
                    Mono<String> redirectionPageMono = RedirectionPage.getRedirectionPage(pageNames, id);
                    return redirectionPageMono;
                });
    }

    @GetMapping("/cart/items")
    public Mono<String> getCart(Model model, Principal principal) {
        String username = principal.getName();

        Flux<ItemDto> itemDtosFlux = cartService.getItemsDtosInCart(username);
        Mono<String> totalPriceFormattedMono = cartService.getTotalSumFormatted(username);

        boolean[] isPaymentServiceAvailable = new boolean[1];
        isPaymentServiceAvailable[0] = true;

        Mono<String> tokenStringMono = TokenStringMono.getTokenStringMono(manager);

        Mono<Double> balanceMono = tokenStringMono
                .flatMap(accessToken -> webClient.get()
/*                        .uri(Constants.SCHEME + "://" + Constants.HOST + ":" + Constants.PORT + Constants.ROOT_PATH
                             + "/balance")*/
                                .uri(uriBuilder -> uriBuilder
                                        .scheme(Constants.SCHEME)
                                        .host(Constants.HOST)
                                        .port(Constants.PORT)
                                        .path(Constants.ROOT_PATH + "/balance")
                                        .queryParam("username", username)
                                        .build())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .retrieve()
                        .toEntity(Double.class)
                )
                .map(re -> (Double) re.getBody())
                .flatMap(balance -> {
                    // Запишем текущий баланс в поле класса, чтобы не запрашивать баланс еще раз в OrderController
                    System.out.println("Current balance is for username = " + username + " is: " + balance);
                    Balance.setBalance(balance);
                    return Mono.just(balance);
                })
                .onErrorResume(error -> {
                    System.out.println("PaymentService is not available (1)");
                    isPaymentServiceAvailable[0] = false;
                    return Mono.empty();
                });

        model.addAttribute("principal", principal);
        model.addAttribute("items", itemDtosFlux);
        model.addAttribute("totalPriceFormatted", totalPriceFormattedMono);

        return balanceMono
                .doOnNext(i -> {
                    model.addAttribute("isPaymentServiceAvailable", isPaymentServiceAvailable[0]);
                    if (!isPaymentServiceAvailable[0]) {
                        System.out.println("PaymentService is not available (2)");
                    }
                })
                .then(Mono.just("cart"));
    }
}
