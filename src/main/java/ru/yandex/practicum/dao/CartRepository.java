package ru.yandex.practicum.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.model.CartItem;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<CartItem, Integer> {
    List<CartItem> findAll();


}
