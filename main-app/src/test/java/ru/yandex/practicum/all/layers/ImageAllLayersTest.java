/*
package ru.yandex.practicum.all.layers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.yandex.practicum.config.SecurityConfigTest;
import ru.yandex.practicum.dao.ImageRepository;
import ru.yandex.practicum.model.Image;

import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Import(SecurityConfigTest.class)
public class ImageAllLayersTest {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    ImageRepository imageRepository;

    @AfterEach
    void clearDb() {
        imageRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void getImage() throws Exception {
        byte[] imageBytes = Files.readAllBytes(Paths.get("src/test/resources/images-bytes/pipe.txt"));
        Image image = new Image(imageBytes);
        imageRepository.save(image).block();

        webTestClient.get()
                .uri("/image/1")
                .exchange()
                .expectHeader().contentType("application/json")
                .expectStatus().isOk()
                .expectBody(byte[].class);
    }
}
*/
