package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.dto.ItemDto;

import java.util.List;

@Data
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @OneToMany
    @JoinColumn(name = "order_id")
    List<OrderItem> orderItems;

    @Column(name = "total_sum")
    private double totalSum;

    @Override
    public String toString() {
        return "Order{" +
               "id=" + id +
               ", orderItems=" + orderItems +
               '}';
    }
}
