package ru.yandex.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.yandex.practicum.dao.CartRepository;
import ru.yandex.practicum.dao.ImageRepository;
import ru.yandex.practicum.dao.ItemRepository;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.model.CartItem;
import ru.yandex.practicum.model.Image;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.Mockito.*;

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
        byte[] imageBytes = Files.readAllBytes(Paths.get("src/test/resources/images-bytes/beam.txt"));
        Image image = new Image(imageBytes);
        Mockito.when(imageRepository.findById(1))
                .thenReturn(Optional.of(image));

        byte[] imageFromDb = imageService.getImage(1);
        assertNotNull(imageFromDb, "Image should exist");
        assertArrayEquals(imageBytes, imageFromDb, "Image was saved or retrieved incorrectly");

        Mockito.verify(imageRepository, times(1)).findById(1);
    }
}
