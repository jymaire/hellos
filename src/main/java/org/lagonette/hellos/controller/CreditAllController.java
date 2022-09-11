package org.lagonette.hellos.controller;

import org.lagonette.hellos.repository.PaymentRepository;
import org.lagonette.hellos.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class CreditAllController {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;

    public CreditAllController(PaymentService paymentService, PaymentRepository paymentRepository) {
        this.paymentService = paymentService;
        this.paymentRepository = paymentRepository;
    }


    @PostMapping("/credit-all")
    public void receivePayment() {
        paymentService.creditAll();
    }

    @PostMapping("/delete-all")
    public void deleteAllPayment(HttpServletResponse httpResponse) throws IOException {
        paymentRepository.deleteAll();
        httpResponse.sendRedirect("/list");
    }
}
