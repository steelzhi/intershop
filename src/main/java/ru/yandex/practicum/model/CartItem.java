/*
package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.dto.ItemDto;

@Data
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cart_items")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @ManyToOne
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    ItemDto itemDto;

    public CartItem(ItemDto itemDto) {
        this.itemDto = itemDto;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "id=" + id +
                ", itemDto=" + itemDto +
                '}';
    }
}
*/
