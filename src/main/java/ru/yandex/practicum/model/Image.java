package ru.yandex.practicum.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import ru.yandex.practicum.dto.ItemDto;

import java.sql.Types;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Table(name = "images")
public class Image {

    @Id
    int id;

    //@JdbcTypeCode(Types.BINARY)
    @Column("image_bytes")
    byte[] imageBytes;

/*    @OneToOne(mappedBy = "image")
    ItemDto itemDto;*/

    public Image(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }
}
