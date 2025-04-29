package ru.yandex.practicum.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.multipart.MultipartFile;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {

    int id;

    String name;

    String description;

    FilePart imageFile;

    double price;

    public Item(String name, String description, FilePart imageFile, double price) {
        this.name = name;
        this.description = description;
        this.imageFile = imageFile;
        this.price = price;
    }
}
