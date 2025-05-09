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
@NoArgsConstructor
@Table(name = "order_items")
public class OrderItem {
    @Id
    int id;

    int orderId;

    int itemId;

    double itemPrice;

    @Column("item_amount")
    int itemAmount;

    @Column("order_item_total_sum")
    double orderItemTotalSum;

    public OrderItem(int orderId, int itemId, double itemPrice, int itemAmount) {
        this.orderId = orderId;
        this.itemId = itemId;
        this.itemAmount = itemAmount;
        this.itemPrice = itemPrice;
        this.orderItemTotalSum = itemPrice * itemAmount;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", itemId=" + itemId +
                ", itemPrice=" + itemPrice +
                ", itemAmount=" + itemAmount +
                ", orderItemTotalSum=" + orderItemTotalSum +
                '}';
    }
}
