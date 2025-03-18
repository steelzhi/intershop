package ru.yandex.practicum.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.model.CartItem;
import ru.yandex.practicum.service.CartService;
import ru.yandex.practicum.service.ImageService;
import ru.yandex.practicum.service.ItemService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ImageController.class)
public class ImageControllerWebLayerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ImageService imageService;

    private static byte[] image;

    @BeforeAll
    static void getImage() throws IOException {
        image = Files.readAllBytes(Paths.get("src/test/resources/images-bytes/armature.txt"));
    }

    @Test
    void getImage_shouldReturnImage() throws Exception {
        when(imageService.getImage(1))
                .thenReturn(image);

        mockMvc.perform(get("/1/image"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/octet-stream"));

        verify(imageService, times(1)).getImage(1);
    }


}
