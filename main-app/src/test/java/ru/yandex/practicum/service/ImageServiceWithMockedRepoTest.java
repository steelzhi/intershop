/*
package ru.yandex.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dao.ImageRepository;
import ru.yandex.practicum.dao.ItemRepository;
import ru.yandex.practicum.model.Image;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;

@SpringBootTest(classes = ImageService.class)
public class ImageServiceWithMockedRepoTest {
    @Autowired
    ImageService imageService;

    @MockitoBean
    ImageRepository imageRepository;

    @MockitoBean
    ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        Mockito.reset(itemRepository);
    }

    @Test
    void testGetImage() throws IOException {
        byte[] imageBytes1 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\armature.txt"));
        Image image1 = new Image(imageBytes1);
        Mockito.when(imageRepository.findById(1))
                .thenReturn(Mono.just(image1));


        Mono<byte[]> imageBytes = imageService.getImage(1);
        assertNotNull(imageBytes.block(), "Image should exist");

        Mockito.verify(imageRepository, times(1)).findById(1);
    }
}
*/
