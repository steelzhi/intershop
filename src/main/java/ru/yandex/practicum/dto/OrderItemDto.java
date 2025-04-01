package ru.yandex.practicum.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.util.Formatter;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItemDto {
    int id;

    int orderId;

    ItemDto itemDto;

    double orderItemTotalSum;

    public OrderItemDto(int id, int orderId, ItemDto itemDto) {
        this.id = id;
        this.orderId = orderId;
        this.itemDto = itemDto;
        this.orderItemTotalSum = itemDto.getAmount() * itemDto.getPrice();
    }

    public String getOrderItemTotalSumFormatted() {
        return Formatter.DECIMAL_FORMAT.format(orderItemTotalSum);
    }
}
