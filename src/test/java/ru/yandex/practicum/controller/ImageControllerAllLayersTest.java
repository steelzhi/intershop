/*
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
import ru.yandex.practicum.dao.ImageRepository;
import ru.yandex.practicum.model.Image;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Sql("classpath:test-schema.sql")
public class ImageControllerAllLayersTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ImageRepository imageRepository;

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
*/
