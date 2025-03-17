package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.dto.ItemDto;

@Data
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    Order order;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "item_id")
    ItemDto itemDto;

    @Column(name = "item_amount")
    int itemAmount;

    public OrderItem(Order order, ItemDto itemDto, int itemAmount) {
        this.order = order;
        this.itemDto = itemDto;
        this.itemAmount = itemAmount;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
               "id=" + id +
               ", order=" + order +
               ", itemDto=" + itemDto +
               ", itemAmount=" + itemAmount +
               '}';
    }
}
