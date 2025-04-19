package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PaymentService {
    private static final int BALANCE_MULTIPLIER = 1_000_000;
    private static Double balance;

    public Mono<Double> getBalance() {
        if (balance == null) {
            balance = Math.random() * BALANCE_MULTIPLIER;
            balance = (double) Math.round(balance * 100) / 100;
        }
        return Mono.just(balance);
    }

    public Mono<Void> doPayment(double payment) {
        balance -= payment;
        return Mono.empty();
    }
}
