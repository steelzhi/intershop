package ru.yandex.practicum.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "orders")
public class Order {
    @Id
    int id;

    String username;

    @Column("total_sum")
    private double totalSum;

    public Order(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Order{" +
               "id=" + id +
               '}';
    }
}
