/*
package ru.yandex.practicum.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.model.Image;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataR2dbcTest
@Sql("classpath:test-schema.sql")
@TestPropertySource(locations = "classpath:test-application.yml")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ImageRepositoryTest {

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @AfterEach
    void clearDb() {
        imageRepository.deleteAll();
        itemRepository.deleteAll();
    }

*/
/*    @Test
    void testAddImage() throws IOException {
        byte[] imageBytes1 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\armature.txt"));
        Image image1 = new Image(imageBytes1);
        Mono<Image> imageMono1 = imageRepository.save(image1);
        Image savedImage = imageMono1.block();

        assertTrue(savedImage != null, "image should exist in Db");

    }*//*


    @Test
    void test1() {

    }

*/
/*    @Test
    void findById() throws IOException {
        byte[] imageBytes1 = Files.readAllBytes(Paths.get("src/test/resources/images-bytes/sheet.txt"));
        Image image1 = new Image(imageBytes1);
        imageRepository.save(image1);
        byte[] imageBytes2 = Files.readAllBytes(Paths.get("src/test/resources/images-bytes/beam.txt"));
        Image image2 = new Image(imageBytes2);
        imageRepository.save(image2);
        Optional<Image> savedImage2 = imageRepository.findById(2);
        assertTrue(savedImage2.isPresent(), "image 2 should exist in Db");
    }*//*

}
*/
