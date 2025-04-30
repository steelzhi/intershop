package ru.yandex.practicum.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cart_items")
public class CartItem {
    @Id
    int id;

    @Column("item_id")
    int itemId;

    String username;

    public CartItem(int itemId, String username) {
        this.itemId = itemId;
        this.username = username;
    }

    @Override
    public String toString() {
        return "CartItem{" +
               "id=" + id +
               ", itemDtoId=" + itemId +
               ", username=" + username +
               '}';
    }
}
