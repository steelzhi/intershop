package ru.yandex.practicum.dao;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import ru.yandex.practicum.model.OrderItem;

public interface OrderItemRepository extends R2dbcRepository<OrderItem, Integer> {
}
