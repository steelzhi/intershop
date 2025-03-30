package ru.yandex.practicum.dao;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.model.Order;
import org.springframework.data.r2dbc.repository.Query;


public interface OrderRepository extends R2dbcRepository<Order, Integer> {
    @Query("""
            SELECT SUM(total_sum)
            FROM orders
            """)
    Mono<Double> getSumOfAllOrders();
}
