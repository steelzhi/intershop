package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {
    private static final int BALANCE_MULTIPLIER = 1_000_000;
    // Балансы пользователей
    private static Map<String, Double> usersBalances = new HashMap<>();

    public Mono<Double> getBalance(String username) {
        if (usersBalances.containsKey(username)) {
            return Mono.just(usersBalances.get(username));
        } else {
            Double balance = Math.random() * BALANCE_MULTIPLIER;
            balance = (double) Math.round(balance * 100) / 100;
            usersBalances.put(username, balance);
            return Mono.just(balance);
        }
    }

    public Mono<Void> doPayment(double payment, String username) {
        double balance = usersBalances.get(username);
        balance -= payment;
        usersBalances.put(username, balance);
        return Mono.empty();
    }
}
