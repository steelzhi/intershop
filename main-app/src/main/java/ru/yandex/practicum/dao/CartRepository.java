package ru.yandex.practicum.dao;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.model.CartItem;

@Repository
public interface CartRepository extends R2dbcRepository<CartItem, Integer> {
    Mono<CartItem> findByItemIdAndUsername(int itemId, String username);

    Flux<CartItem> findAllByUsername(String username);

    @Query("""
            SELECT SUM(i.price * i.amount)
            FROM cart_items ci
            LEFT JOIN items i ON i.id = ci.item_id
            WHERE ci.username = :username
            """)
    Mono<Double> getTotalSum(String username);
}
