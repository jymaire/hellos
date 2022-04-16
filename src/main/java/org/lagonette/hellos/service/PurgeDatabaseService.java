package org.lagonette.hellos.service;

import io.github.cdimascio.dotenv.Dotenv;
import org.lagonette.hellos.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class PurgeDatabaseService {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final PaymentRepository paymentRepository;
    private final Dotenv dotenv;

    public PurgeDatabaseService(PaymentRepository paymentRepository, Dotenv dotenv) {
        this.paymentRepository = paymentRepository;
         this.dotenv = dotenv;
    }

    @Transactional
    @Scheduled(cron = "${database.purge.cron}")
    public void purgeDatabase() {
        LOGGER.info("Begin of the database purge");

        long nbOfDaysForPayments = Long.parseLong(dotenv.get("DATABASE_RETENTION_PAYMENT_DAY"));
        // Payment purge
        List<Integer> paymentsToDelete = paymentRepository.findIdsByInsertionDateBefore(LocalDateTime.now().minusDays(nbOfDaysForPayments));
        int paymentsSize = paymentsToDelete.size();
        LOGGER.info("{} records of payments to delete", paymentsSize);
        if (paymentsSize > 0) {
            paymentRepository.deleteById(paymentsToDelete);
        }

        LOGGER.info("End of the database purge");
    }
}
