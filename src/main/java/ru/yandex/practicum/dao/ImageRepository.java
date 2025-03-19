package ru.yandex.practicum.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {
}
