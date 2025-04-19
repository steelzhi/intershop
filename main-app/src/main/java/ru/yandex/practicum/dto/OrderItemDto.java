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

    int itemDtoAmount;

    ItemDto itemDto;

    double orderItemTotalSum;

    public OrderItemDto(int id, int orderId, int itemDtoAmount, ItemDto itemDto) {
        this.id = id;
        this.orderId = orderId;
        this.itemDtoAmount = itemDtoAmount;
        this.itemDto = itemDto;
        this.orderItemTotalSum = itemDtoAmount * itemDto.getPrice();
    }

    public String getOrderItemTotalSumFormatted() {
        return Formatter.DECIMAL_FORMAT.format(orderItemTotalSum);
    }
}
