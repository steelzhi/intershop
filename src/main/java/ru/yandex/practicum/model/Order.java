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

/*    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "order_items",
            joinColumns = @JoinColumn(name = "order_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "item_id", referencedColumnName = "id"))
    List<ItemDto> itemDtos;
            */

    @OneToMany
    @JoinColumn(name = "order_id")
    List<OrderItem> orderItems;

/*    public Order(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }*/

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
