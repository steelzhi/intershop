package ru.yandex.practicum.all.layers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.config.SecurityConfigTest;
import ru.yandex.practicum.dao.ImageRepository;
import ru.yandex.practicum.dao.ItemRepository;
import ru.yandex.practicum.dao.UserRepository;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.mapper.ItemMapper;
import ru.yandex.practicum.model.Image;
import ru.yandex.practicum.model.Item;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Import(SecurityConfigTest.class)
public class ItemAllLayersTest {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    UserRepository userRepository;

    @AfterEach
    void clearDb() {
        itemRepository.deleteAll();
        imageRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void getItemsList() throws Exception {
        byte[] imageBytes1 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\armature.txt"));
        Image image1 = new Image(imageBytes1);
        Mono<Image> imageMono1 = imageRepository.save(image1);
        Item item1 = new Item("Арматура", "Арматура для строительства", null, 65_000);
        Mono<ItemDto> itemDto1 = ItemMapper.mapToItemDto(Mono.just(item1), imageMono1)
                .doOnNext(itemDto -> itemDto.setAmount(1))
                .flatMap(itemDto -> itemRepository.save(itemDto));
        ItemDto itemDto = itemDto1.block();

        webTestClient.get()
                .uri("/main/items")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("main"));
                    assertTrue(body.contains(itemDto.getName()));
                    assertTrue(body.contains(itemDto.getDescription()));
                });
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void getItemsListSplittedByPages() throws Exception {
        byte[] imageBytes1 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\armature.txt"));
        Image image1 = new Image(imageBytes1);
        Mono<Image> imageMono1 = imageRepository.save(image1);
        Item item1 = new Item("itemDto1", "abcdesc1", null, 1.0);
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

        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/main/items")
                                .queryParam("itemsOnPage", "10")
                                .queryParam("pageNumber", "1")
                                .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("main"));
                    assertTrue(body.contains(itemDto1.getName()));
                    assertTrue(body.contains(itemDto1.getDescription()));
                });
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void getItemDto() throws Exception {
        byte[] imageBytes1 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\armature.txt"));
        Image image1 = new Image(imageBytes1);
        Mono<Image> imageMono1 = imageRepository.save(image1);
        Item item1 = new Item("itemDto1", "abcdesc1", null, 1.0);
        Mono<ItemDto> itemDtoMono1 = ItemMapper.mapToItemDto(Mono.just(item1), imageMono1)
                .doOnNext(itemDto -> itemDto.setAmount(1))
                .flatMap(itemDto -> itemRepository.save(itemDto));
        ItemDto itemDto1 = itemDtoMono1.block();

        webTestClient.get()
                .uri("/items/" + itemDto1.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("main"));
                    assertTrue(body.contains(itemDto1.getName()));
                    assertTrue(body.contains(itemDto1.getDescription()));
                });
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void search() throws Exception {
        byte[] imageBytes1 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\armature.txt"));
        Image image1 = new Image(imageBytes1);
        Mono<Image> imageMono1 = imageRepository.save(image1);
        Item item1 = new Item("itemDto1", "abcdesc1", null, 1.0);
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

        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/search")
                                .queryParam("key", "cde")
                                .queryParam("sortingCategory", "PRICE")
                                .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("main"));
                    assertTrue(body.contains(itemDto1.getName()));
                    assertTrue(body.contains(itemDto1.getDescription()));
                    assertFalse(body.contains(itemDto2.getDescription()));
                });
    }
}
