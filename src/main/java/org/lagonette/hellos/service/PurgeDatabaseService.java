package org.lagonette.hellos.service;

import org.lagonette.hellos.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class PurgeDatabaseService {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final PaymentRepository paymentRepository;
    @Value("${database.retention.days}")
    private int nbOfDays;

    public PurgeDatabaseService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }


    @Transactional
    @Scheduled(cron = "${database.purge.cron}")
    public void purgeDatabase() {
        LOGGER.info("Begin of the database purge");
        List<Integer> paymentsToDelete = paymentRepository.findIdsByInsertionDateBefore(LocalDateTime.now().minusDays(nbOfDays));
        int size = paymentsToDelete.size();
        LOGGER.info("{} records to delete", size);
        if (size > 0) {
            paymentRepository.deleteById(paymentsToDelete);
        }
        LOGGER.info("End of the database purge");
    }
}
