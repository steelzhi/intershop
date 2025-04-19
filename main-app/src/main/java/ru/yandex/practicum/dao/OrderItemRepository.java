package ru.yandex.practicum.dao;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ru.yandex.practicum.model.OrderItem;

@Repository
public interface OrderItemRepository extends R2dbcRepository<OrderItem, Integer> {
    Flux<OrderItem> findAllByOrderId(int orderId);
}
