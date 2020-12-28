package org.lagonette.hellos.configuration;

import org.lagonette.hellos.entity.Payment;
import org.lagonette.hellos.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
@Profile("dev")
public class DemoConfig {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    // Add some data for demo and dev purposes
    @Bean
    public CommandLineRunner demo(PaymentRepository repository) {
        return (args) -> {
            // save a few payments
            Payment payment = new Payment(11, "2020-apo", 2.4f, "first", "last", "test@test.fr");
            payment.setInsertionDate(LocalDateTime.parse("2019-02-01T11:11:11"));
            payment.setError("a bug occurred");
            repository.save(payment);
            repository.save(new Payment(22, "2020-apo", 2.4f, "first", "last", "email2"));
            List<Payment> all = repository.findAll();
            LOGGER.info("all : {}", all);
        };
    }
}
