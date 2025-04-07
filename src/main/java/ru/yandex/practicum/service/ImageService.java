package ru.yandex.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dao.ImageRepository;
import ru.yandex.practicum.model.Image;

@Service
public class ImageService {
    @Autowired
    private ImageRepository imageRepository;

    public Mono<byte[]> getImage(int imageId) {
        Mono<Image> imageMono = imageRepository.findById(imageId);
        imageMono.subscribe();
        Mono<byte[]> imageBytes = imageMono.map(image -> image.getImageBytes());
        imageBytes.subscribe();
        return imageBytes;
    }
}
