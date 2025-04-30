package ru.yandex.practicum.dao;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.model.Order;

@Repository
public interface OrderRepository extends R2dbcRepository<Order, Integer> {
    Flux<Order> findAllByUsername(String username);

    @Query("""
            SELECT SUM(total_sum)
            FROM orders
            WHERE username = :username
            """)
    Mono<Double> getSumOfAllOrdersForUser(String username);
}
