/*
package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.util.Formatter;

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

    @Column(name = "order_item_total_sum")
    double orderItemTotalSum;

    public OrderItem(Order order, ItemDto itemDto, int itemAmount) {
        this.order = order;
        this.itemDto = itemDto;
        this.itemAmount = itemAmount;
        this.orderItemTotalSum = itemDto.getPrice() * itemAmount;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", itemDto=" + itemDto +
                ", itemAmount=" + itemAmount +
                ", orderItemTotalSum=" + orderItemTotalSum +
                '}';
    }

    public String getOrderItemTotalSumFormatted() {
        return Formatter.DECIMAL_FORMAT.format(orderItemTotalSum);
    }
}
*/
