package ru.yandex.practicum.all.layers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dao.*;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.mapper.ItemMapper;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.service.CartService;
import ru.yandex.practicum.service.OrderService;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class OrderAllLayersTest {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    OrderService orderService;

    @Autowired
    CartService cartService;

    @Autowired
    UserRepository userRepository;

    @AfterEach
    void clearDb() {
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        cartRepository.deleteAll();
        itemRepository.deleteAll();
        imageRepository.deleteAll();
        userRepository.deleteAll();
    }

    @BeforeEach
    void clearBalance() {
        Balance.setBalance(0);
    }

    @Test
    @WithMockUser(username = "user3", roles = {"USER"})
    void createNotEmptyOrderWhenBalanceIsEnoughForMakingOrder_shouldOrder() throws Exception {
        double balance = 1_000_000;
        Balance.setBalance(balance);
        byte[] imageBytes1 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\armature.txt"));
        Image image1 = new Image(imageBytes1);
        Mono<Image> imageMono1 = imageRepository.save(image1);
        Item item1 = new Item("Арматура", "Арматура для строительства", null, 65_000);
        Mono<ItemDto> itemDto1 = ItemMapper.mapToItemDto(Mono.just(item1), imageMono1)
                .doOnNext(itemDto -> itemDto.setAmount(1))
                .flatMap(itemDto -> itemRepository.save(itemDto));
        ItemDto itemDto = itemDto1.block();

        User user = new User("user3", "pass");
        User savedUser = userRepository.save(user).block();
        CartItem cartItem = new CartItem(itemDto.getId(), savedUser.getUsername());
        cartRepository.save(cartItem).block();

        webTestClient.post()
                .uri("/create-order")
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("order"));
                    assertTrue(body.contains(itemDto.getName()));
                });
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void createNotEmptyOrderWhenBalanceIsNotEnoughForMakingOrder_shouldReturnDenialMessage() throws Exception {
        byte[] imageBytes1 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\armature.txt"));
        Image image1 = new Image(imageBytes1);
        Mono<Image> imageMono1 = imageRepository.save(image1);
        Item item1 = new Item("Арматура", "Арматура для строительства", null, 65_000);
        Mono<ItemDto> itemDto1 = ItemMapper.mapToItemDto(Mono.just(item1), imageMono1)
                .doOnNext(itemDto -> itemDto.setAmount(1))
                .flatMap(itemDto -> itemRepository.save(itemDto));
        ItemDto itemDto = itemDto1.block();

        User user = new User("user", "pass");
        User savedUser = userRepository.save(user).block();
        CartItem cartItem = new CartItem(itemDto.getId(), savedUser.getUsername());
        cartRepository.save(cartItem).block();

        webTestClient.post()
                .uri("/create-order")
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertTrue(body.contains(
                            "<h2>Недостаточно денег на счете для совершения заказа. Не хватает суммы: "));
                });
    }

    @Test
    @WithMockUser(username = "user4", roles = {"USER"})
    void getOrders() throws Exception {
        byte[] imageBytes1 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\armature.txt"));
        Image image1 = new Image(imageBytes1);
        Mono<Image> imageMono1 = imageRepository.save(image1);
        Item item1 = new Item("Арматура", "Арматура для строительства", null, 65_000);
        Mono<ItemDto> itemDtoMono1 = ItemMapper.mapToItemDto(Mono.just(item1), imageMono1)
                .doOnNext(itemDto -> itemDto.setAmount(1))
                .flatMap(itemDto -> itemRepository.save(itemDto));
        ItemDto itemDto1 = itemDtoMono1.block();

        User user = new User("user4", "pass");
        User savedUser = userRepository.save(user).block();
        CartItem cartItem1 = new CartItem(itemDto1.getId(), savedUser.getUsername());
        cartRepository.save(cartItem1).block();

        Order order1 = new Order(savedUser.getUsername());
        Mono<Order> orderMono1 = orderRepository.save(order1);
        Order savedOrder1 = orderMono1.block();

        OrderItem orderItem
                = new OrderItem(savedOrder1.getId(), itemDto1.getId(), itemDto1.getPrice(), itemDto1.getAmount());
        orderItemRepository.save(orderItem).block();

        OrderDto orderDto1 = orderService.getOrder(savedOrder1.getId()).block();

        byte[] imageBytes2 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\beam.txt"));
        Image image2 = new Image(imageBytes2);
        Mono<Image> imageMono2 = imageRepository.save(image2);
        Item item2 = new Item("itemDto2z", "descghy", null, 12.0);
        Mono<ItemDto> itemDtoMono2 = ItemMapper.mapToItemDto(Mono.just(item2), imageMono2)
                .doOnNext(itemDto -> itemDto.setAmount(1))
                .flatMap(itemDto -> itemRepository.save(itemDto));
        ItemDto itemDto2 = itemDtoMono2.block();

        CartItem cartItem2 = new CartItem(itemDto2.getId(), savedUser.getUsername());
        cartRepository.save(cartItem2).block();

        Order order2 = new Order(savedUser.getUsername());
        Mono<Order> orderMono2 = orderRepository.save(order2);
        Order savedOrder2 = orderMono2.block();

        OrderItem orderItem2
                = new OrderItem(savedOrder2.getId(), itemDto2.getId(), itemDto2.getPrice(), itemDto2.getAmount());
        orderItemRepository.save(orderItem2).block();

        OrderDto orderDto2 = orderService.getOrder(savedOrder2.getId()).block();

        webTestClient.get()
                .uri("/orders")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("orders"));
                    assertTrue(body.contains(itemDto1.getName()));
                    assertTrue(body.contains(itemDto1.getDescription()));
                });
    }

    @Test
    @WithMockUser(username = "user5", roles = {"USER"})
    void getOrder() throws Exception {
        byte[] imageBytes1 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\armature.txt"));
        Image image1 = new Image(imageBytes1);
        Mono<Image> imageMono1 = imageRepository.save(image1);
        Item item1 = new Item("Арматура", "Арматура для строительства", null, 65_000);
        Mono<ItemDto> itemDtoMono1 = ItemMapper.mapToItemDto(Mono.just(item1), imageMono1)
                .doOnNext(itemDto -> itemDto.setAmount(1))
                .flatMap(itemDto -> itemRepository.save(itemDto));
        ItemDto itemDto1 = itemDtoMono1.block();

        User user = new User("user5", "pass");
        User savedUser = userRepository.save(user).block();
        CartItem cartItem1 = new CartItem(itemDto1.getId(), savedUser.getUsername());
        cartRepository.save(cartItem1).block();

        Order order1 = new Order(savedUser.getUsername());
        Mono<Order> orderMono1 = orderRepository.save(order1);
        Order savedOrder1 = orderMono1.block();

        OrderItem orderItem
                = new OrderItem(savedOrder1.getId(), itemDto1.getId(), itemDto1.getPrice(), itemDto1.getAmount());
        orderItemRepository.save(orderItem).block();

        OrderDto orderDto = orderService.getOrder(savedOrder1.getId()).block();

        webTestClient.get()
                .uri("/orders/" + orderDto.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("order"));
                    assertTrue(body.contains(itemDto1.getDescription()));
                });
    }

    @Test
    void getOrdersForUnauthorizedUser() throws Exception {
        webTestClient.get()
                .uri("/orders")
                .exchange()
                .expectStatus().isFound(); // перенаправление на страницу входа
    }
}
