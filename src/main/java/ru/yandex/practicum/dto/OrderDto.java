package ru.yandex.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.util.Formatter;

import java.util.List;

@AllArgsConstructor
@Data
public class OrderDto {
    int id;

    private double totalSum;

    private List<OrderItemDto> orderItemDto;

    public String getTotalSumFormatted() {
        return Formatter.DECIMAL_FORMAT.format(totalSum);
    }

    public OrderDto(int id, double totalSum) {
        this.id = id;
        this.totalSum = totalSum;
    }
}
