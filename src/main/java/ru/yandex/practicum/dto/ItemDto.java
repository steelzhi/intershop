package ru.yandex.practicum.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
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
    Integer imageId;

    double price;

    int amount;

    @JsonIgnore
    @Transient
    double priceFormatted;

    public ItemDto(String name, String description, Integer imageId, double price, int amount) {
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
               ", amount=" + amount +
               ", imageId=" + imageId +
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

    public void increaseAmount() {
        setAmount(getAmount() + 1);
    }

    public void decreaseAmount() {
        if (amount > 0) {
            setAmount(getAmount() - 1);
        }
    }
}
