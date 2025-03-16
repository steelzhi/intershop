package ru.yandex.practicum.dto;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.model.Image;

@Data
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Table(name = "items")
public class ItemDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    String name;

    String description;

    //@Lob
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id", referencedColumnName = "id")
    Image image;
/*    @JdbcTypeCode(Types.BINARY)
    byte[] image;*/

    double price;

    public ItemDto(String name, String description, Image image, double price) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.price = price;
    }

    @Override
    public String toString() {
        return "ItemDto{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", description='" + description + '\'' +
               ", price=" + price +
               '}';
    }
}
