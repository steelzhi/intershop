package ru.yandex.practicum.dao;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.Image;

@Repository
public interface ImageRepository extends R2dbcRepository<Image, Integer> {
}
