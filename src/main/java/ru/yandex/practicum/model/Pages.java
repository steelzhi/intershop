package ru.yandex.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Pages {
    private int itemsOnPage;
    private int numberOfPages;

    public int getItemsOnPage() {
        return itemsOnPage;
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }
}

