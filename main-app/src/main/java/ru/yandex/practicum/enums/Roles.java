package ru.yandex.practicum.enums;

public enum Roles {
    USER("user"),
    ADMIN("admin");

    private String name;

    Roles(String name) {
        this.name = name;
    }

    public static String getName(Roles role) {
        return role.name;
    }
}