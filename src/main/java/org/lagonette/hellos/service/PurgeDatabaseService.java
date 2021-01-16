package org.lagonette.hellos.service;

import org.lagonette.hellos.repository.EmailLinkRepository;
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

    private final EmailLinkRepository emailLinkRepository;
    private final PaymentRepository paymentRepository;

    @Value("${database.retention.payment.days}")
    private int nbOfDaysForPayments;
    @Value("${database.retention.email.days}")
    private int nbOfDaysForEmails;

    public PurgeDatabaseService(EmailLinkRepository emailLinkRepository, PaymentRepository paymentRepository) {
        this.emailLinkRepository = emailLinkRepository;
        this.paymentRepository = paymentRepository;
    }


    @Transactional
    @Scheduled(cron = "${database.purge.cron}")
    public void purgeDatabase() {
        LOGGER.info("Begin of the database purge");

        // Payment purge
        List<Integer> paymentsToDelete = paymentRepository.findIdsByInsertionDateBefore(LocalDateTime.now().minusDays(nbOfDaysForPayments));
        int paymentsSize = paymentsToDelete.size();
        LOGGER.info("{} records of payments to delete", paymentsSize);
        if (paymentsSize > 0) {
            paymentRepository.deleteById(paymentsToDelete);
        }

        // Email purge
        List<String> emailsToDelete = emailLinkRepository.findIdsByInsertionDateBefore(LocalDateTime.now().minusDays(nbOfDaysForEmails));
        int emailsSize = emailsToDelete.size();
        LOGGER.info("{} records of emails to delete", emailsSize);
        if (emailsSize > 0) {
            emailLinkRepository.deleteById(emailsToDelete);
        }

        LOGGER.info("End of the database purge");
    }
}
