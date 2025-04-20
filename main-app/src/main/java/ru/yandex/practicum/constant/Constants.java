package ru.yandex.practicum.constant;

import lombok.Getter;

public class Constants {
    public static final String SCHEME = "http";
    public static final String HOST = "localhost";
    public static final String PORT = "9090";
    public static final String ROOT_PATH = "/payments";

    public static final int TTL = 5;

    private Constants() {
    }
}
