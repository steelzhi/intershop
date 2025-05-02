package ru.yandex.practicum.all.layers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.config.SecurityConfigTest;
import ru.yandex.practicum.dao.*;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.enums.Roles;
import ru.yandex.practicum.mapper.ItemMapper;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.service.ItemGettingFromCacheService;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Import(SecurityConfigTest.class)
public class CartAllLayersTest {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ItemGettingFromCacheService itemService;

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    UserRepository userRepository;

    @AfterEach
    void clearDb() {
        cartRepository.deleteAll();
        itemRepository.deleteAll();
        imageRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void addItemToCart_shouldAddItemt() throws Exception {
        byte[] imageBytes1 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\armature.txt"));
        Image image1 = new Image(imageBytes1);
        Mono<Image> imageMono1 = imageRepository.save(image1);
        Item item1 = new Item("Арматура", "Арматура для строительства", null, 65_000);
        Mono<ItemDto> itemDto1 = ItemMapper.mapToItemDto(Mono.just(item1), imageMono1)
                .doOnNext(itemDto -> itemDto.setAmount(1))
                .flatMap(itemDto -> itemRepository.save(itemDto));
        ItemDto itemDto = itemDto1.block();

        CartItem cartItem = new CartItem(1, itemDto.getId(), "user");

        webTestClient.post()
                .uri("/cart/add/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cartItem)
                .exchange()
                .expectStatus().is3xxRedirection();
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void getCart() throws Exception {
        byte[] imageBytes1 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\armature.txt"));
        Image image1 = new Image(imageBytes1);
        Mono<Image> imageMono1 = imageRepository.save(image1);
        Item item1 = new Item("Арматура", "Арматура для строительства", null, 65_000);
        Mono<ItemDto> itemDtoMono1 = ItemMapper.mapToItemDto(Mono.just(item1), imageMono1)
                .doOnNext(itemDto -> itemDto.setAmount(1))
                .flatMap(itemDto -> itemRepository.save(itemDto));
        ItemDto itemDto1 = itemDtoMono1.block();

        byte[] imageBytes2 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\beam.txt"));
        Image image2 = new Image(imageBytes2);
        Mono<Image> imageMono2 = imageRepository.save(image2);
        Item item2 = new Item("itemDto2z", "descghy", null, 12.0);
        Mono<ItemDto> itemDtoMono2 = ItemMapper.mapToItemDto(Mono.just(item2), imageMono2)
                .doOnNext(itemDto -> itemDto.setAmount(1))
                .flatMap(itemDto -> itemRepository.save(itemDto));
        ItemDto itemDto2 = itemDtoMono2.block();

        User user = new User("user", "pass");
        User savedUser = userRepository.save(user).block();
        CartItem cartItem1 = new CartItem(itemDto1.getId(), savedUser.getUsername());
        cartRepository.save(cartItem1).block();
        CartItem cartItem2 = new CartItem(itemDto2.getId(), savedUser.getUsername());
        cartRepository.save(cartItem2).block();

        webTestClient.get()
                .uri("/cart/items")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("cart"));
                    assertTrue(body.contains(itemDto1.getName()));
                    assertTrue(body.contains(itemDto2.getDescription()));
                });
    }

    @Test
    void getCartForUnauthorizedUser() throws Exception {
        webTestClient.get()
                .uri("/cart/items")
                .exchange()
                .expectStatus().isFound(); // перенаправление на страницу входа
    }
}
