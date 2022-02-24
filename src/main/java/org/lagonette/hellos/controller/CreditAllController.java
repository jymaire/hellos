package org.lagonette.hellos.controller;

import org.lagonette.hellos.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class CreditAllController {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final PaymentService paymentService;

    public CreditAllController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }


    @PostMapping("/credit-all")
    public void receivePayment() throws IOException {
        paymentService.creditAll();
    }
}
