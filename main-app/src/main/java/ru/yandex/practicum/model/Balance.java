package ru.yandex.practicum.model;

import lombok.Data;

/*
 * В CartController происходит запрос о доступности сервиса платежей, в OrderController - запрос баланса и списание
 * суммы заказа с баланса. Чтобы не делать дважды запрос в сервис платежей (1-й раз - когда проверяем доступность сервиса,
 * 2-й раз - когда запрашиваем текущий баланс), сохраним баланс в поле класса Balance на этапе проверки доступности
 * сервиса платежей (и в OrderControlle#createOrder обратимся к значению этого поля)
 */
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
