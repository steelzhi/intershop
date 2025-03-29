package ru.yandex.practicum.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import ru.yandex.practicum.util.Formatter;

import java.util.Objects;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Table(name = "items")
public class ItemDto {

    @Id
    int id;

    String name;

    String description;

    @Column("image_id")
    int imageId;

    double price;

    int amount;

    public ItemDto(String name, String description, int imageId, double price, int amount) {
        this.name = name;
        this.description = description;
        this.imageId = imageId;
        this.price = price;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "ItemDto{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", description='" + description + '\'' +
               ", price=" + price +
               ", count=" + amount +
               '}';
    }

    public String getPriceFormatted() {
        return Formatter.DECIMAL_FORMAT.format(price);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ItemDto itemDto = (ItemDto) o;
        return id == itemDto.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
