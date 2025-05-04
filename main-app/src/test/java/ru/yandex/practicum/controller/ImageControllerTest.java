/*
package ru.yandex.practicum.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dao.*;
import ru.yandex.practicum.service.ImageService;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.mockito.Mockito.*;

@WebFluxTest(ImageController.class)
public class ImageControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ImageService imageService;

    @MockitoBean
    private OrderRepository orderRepository;

    @MockitoBean
    private CartRepository cartRepository;

    @MockitoBean
    private OrderItemRepository orderItemRepository;

    @MockitoBean
    private ImageRepository imageRepository;

    @MockitoBean
    private ItemRepository itemRepository;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void getImage_shouldReturnImage() throws Exception {
        byte[] image = Files.readAllBytes(Paths.get("src/test/resources/images-bytes/armature.txt"));

        when(imageService.getImage(1))
                .thenReturn(Mono.just(image));

        webTestClient.get()
                .uri("/image/1")
                .exchange()
                .expectHeader().contentType("application/json")
                .expectStatus().isOk()
                .expectBody(byte[].class);

        verify(imageService, times(1)).getImage(1);
    }
}
*/
