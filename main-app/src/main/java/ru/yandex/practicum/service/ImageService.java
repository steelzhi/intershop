package ru.yandex.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dao.ImageRepository;

@Service
public class ImageService {
    @Autowired
    private ImageRepository imageRepository;

    public Mono<byte[]> getImage(int imageId) {
        Mono<byte[]> imageBytes = imageRepository.findById(imageId)
                .map(image -> image.getImageBytes());
        imageBytes.subscribe();
        return imageBytes;
    }
}
