/*
package ru.yandex.practicum.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.model.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    @Query("""
            SELECT SUM(totalSum)
            FROM Order
            """)
    Double getSumOfAllOrders();
}
*/
