package ru.yandex.practicum.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.model.Item;

@Repository
public interface ItemRepository extends JpaRepository<ItemDto, Integer> {
}
