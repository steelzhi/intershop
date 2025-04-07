package ru.yandex.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.model.Image;
import ru.yandex.practicum.service.ImageService;

@Controller
public class ImageController {
    @Autowired
    private ImageService imageService;

    @ResponseBody
    @GetMapping("/image/{imageId}")
    public Mono<byte[]> getImage(@PathVariable(name = "imageId") int imageId) {
        //return imageService.getImage(imageId).block().getImageBytes();
        return imageService.getImage(imageId);
    }
}
