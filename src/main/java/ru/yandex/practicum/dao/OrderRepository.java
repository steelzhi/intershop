package ru.yandex.practicum.dao;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import ru.yandex.practicum.model.Order;
import org.springframework.data.r2dbc.repository.Query;


public interface OrderRepository extends R2dbcRepository<Order, Integer> {
    @Query("""
            SELECT SUM(totalSum)
            FROM Order
            """)
    Double getSumOfAllOrders();
}
