package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import ru.yandex.practicum.dto.ItemDto;

import java.sql.Types;

@Data
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Table(name = "images")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @JdbcTypeCode(Types.BINARY)
    @Column(name = "image_bytes")
    byte[] imageBytes;

    @OneToOne(mappedBy = "image")
    ItemDto itemDto;

    public Image(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }
}
