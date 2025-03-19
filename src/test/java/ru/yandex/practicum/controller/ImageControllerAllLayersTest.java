package ru.yandex.practicum.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.dao.CartRepository;
import ru.yandex.practicum.dao.ImageRepository;
import ru.yandex.practicum.dao.ItemRepository;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.model.CartItem;
import ru.yandex.practicum.model.Image;
import ru.yandex.practicum.service.ItemService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Sql("classpath:test-schema.sql")
public class ImageControllerAllLayersTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ItemService itemService;

    @AfterEach
    void clearDb() {
        imageRepository.deleteAll();
    }

    @Test
    void getImage() throws Exception {
        byte[] imageBytes = Files.readAllBytes(Paths.get("src/test/resources/images-bytes/pipe.txt"));
        Image image = new Image(imageBytes);
        imageRepository.save(image);

        mockMvc.perform(get("/1/image"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/octet-stream"));
    }
}
