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

    public Mono<Image> getImage(int imageId) {
        return imageRepository.findById(imageId);
    }
}
