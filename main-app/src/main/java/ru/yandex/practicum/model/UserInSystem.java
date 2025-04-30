package ru.yandex.practicum.model;

public class UserInSystem {
    private static int id;

    public static void setId(int id) {
        UserInSystem.id = id;
    }

    public static int getId() {
        return id;
    }
}
