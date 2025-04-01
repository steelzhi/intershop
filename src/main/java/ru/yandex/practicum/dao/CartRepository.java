package ru.yandex.practicum.dao;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.model.CartItem;

@Repository
public interface CartRepository extends R2dbcRepository<CartItem, Integer> {
    Mono<CartItem> findByItemId(int itemId);
}
