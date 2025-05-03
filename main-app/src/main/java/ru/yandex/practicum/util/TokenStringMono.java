package ru.yandex.practicum.util;

import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class TokenStringMono {
    private TokenStringMono() {
    }

    public static Mono<String> getTokenStringMono(ReactiveOAuth2AuthorizedClientManager manager) {
        Mono<OAuth2AuthorizedClient> clientMono = manager.authorize(OAuth2AuthorizeRequest
                .withClientRegistrationId("main-app")
                .principal("system") // У client_credentials нет имени пользователя, поэтому будем использовать system.
                .build());

        Mono<String> tokenStringMono = clientMono
                .doOnError(client -> System.out.println("Error!"))
                .doOnNext(client -> System.out.println("Client: " + client))
                .map(client -> {
                    System.out.println("Getting oAuth2AccessToken");
                    OAuth2AccessToken oAuth2AccessToken = client.getAccessToken();
                    System.out.println("oAuth2AccessToken: " + oAuth2AccessToken);
                    return oAuth2AccessToken;
                })
                .map(token -> {
                    String tokenValue = token.getTokenValue();
                    System.out.println("tokenValue: " + tokenValue);
                    return tokenValue;
                });

        return tokenStringMono;
    }
}
