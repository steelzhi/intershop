package ru.yandex.practicum.model;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class Item {
    String name;

    String description;

    MultipartFile imageFile;

    double price;

    public Item(String name, String description, MultipartFile imageFile, double price) {
        this.name = name;
        this.description = description;
        this.imageFile = imageFile;
        this.price = price;
    }
}
