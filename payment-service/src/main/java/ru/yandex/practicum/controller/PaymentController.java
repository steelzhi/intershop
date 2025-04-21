package ru.yandex.practicum.controller;

import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.service.PaymentService;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/balance")
    public Mono<Double> getBalance() {
        return paymentService.getBalance();
    }

    @PostMapping("/do-payment")
    public Mono<Void> doPayment(
            @Parameter(description = "Сумма заказа для списания с баланса") @RequestParam double payment) {
        System.out.println("Got query to decrease balance to sum = " + payment);
        return paymentService.doPayment(payment);
    }
}
