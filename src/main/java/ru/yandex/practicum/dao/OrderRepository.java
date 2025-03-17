package ru.yandex.practicum.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.model.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {
}
