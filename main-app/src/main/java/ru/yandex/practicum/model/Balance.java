package ru.yandex.practicum.model;

import lombok.Data;

@Data
public class Balance {
    private static double balance;

    public static double getBalance() {
        return balance;
    }

    public static void setBalance(double balance) {
        Balance.balance = balance;
    }
}
