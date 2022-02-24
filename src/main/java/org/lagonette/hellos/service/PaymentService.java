package org.lagonette.hellos.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.lagonette.hellos.bean.Notification;
import org.lagonette.hellos.bean.ProcessResult;
import org.lagonette.hellos.bean.StatusPaymentEnum;
import org.lagonette.hellos.bean.helloasso.HelloAssoPaymentStateEnum;
import org.lagonette.hellos.bean.helloasso.notification.HelloAssoPaymentNotification;
import org.lagonette.hellos.entity.Configuration;
import org.lagonette.hellos.entity.Payment;
import org.lagonette.hellos.repository.ConfigurationRepository;
import org.lagonette.hellos.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static org.lagonette.hellos.service.ConfigurationService.MAIL_RECIPIENT;
import static org.lagonette.hellos.service.ConfigurationService.PAYMENT_AUTOMATIC_ENABLED;

@Component
public class PaymentService {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    public static final long NUMBER_LATE_HOURS_ACCEPTED = 12l;

    private final ConfigurationRepository configurationRepository;
    private final PaymentRepository paymentRepository;
    private final MailService mailService;
    private final Dotenv dotenv;
    private final HelloAssoService helloAssoService;
    private final CyclosService cyclosService;

    public PaymentService(ConfigurationRepository configurationRepository, PaymentRepository paymentRepository, MailService mailService, Dotenv dotenv, HelloAssoService helloAssoService, CyclosService cyclosService) {
        this.configurationRepository = configurationRepository;
        this.paymentRepository = paymentRepository;
        this.mailService = mailService;
        this.dotenv = dotenv;
        this.helloAssoService = helloAssoService;
        this.cyclosService = cyclosService;
    }

    public ProcessResult handleNewPayment(ProcessResult processResult, String helloAssoPaymentNotificationWrapper) throws IOException {

        if (helloAssoPaymentNotificationWrapper == null) {
            LOGGER.error("The payment is empty");
            processResult.getErrors().add("Le paiement est vide ");
            processResult.setStatusPayment(StatusPaymentEnum.fail);
            return processResult;
        }
        ObjectMapper mapper = new ObjectMapper();
        final HelloAssoPaymentNotification helloAssoPaymentNotification = mapper.readValue(helloAssoPaymentNotificationWrapper, HelloAssoPaymentNotification.class);

        // check if payment is real
        Map<Boolean, Notification> validPayment = helloAssoService.isValidPayment(helloAssoPaymentNotification);
        if (validPayment.containsKey(false)) {
            LOGGER.debug("The payment is not valid : {}", helloAssoPaymentNotificationWrapper);
            processResult.getErrors().add("Le paiement n'est pas valide :" + helloAssoPaymentNotificationWrapper);
            processResult.setStatusPayment(StatusPaymentEnum.fail);
            return processResult;
        }
        Notification notification = validPayment.get(true);
        final Payment paymentInDatabase = paymentRepository.findById(notification.getId());

        if (paymentInDatabase == null) {
            // register payment in database (convert cent to euro)
            Payment payment = new Payment(notification.getId(), notification.getDate(), (float) notification.getAmount() / 100,
                    notification.getFirstName(), notification.getName(), notification.getEmail());
            Payment paymentSaved = paymentRepository.save(payment);

            if ("true".equals(configurationRepository.findById(PAYMENT_AUTOMATIC_ENABLED).orElse(new Configuration(PAYMENT_AUTOMATIC_ENABLED, "false")).getValue())) {
                // Check date
                LocalDateTime paymentDate = null;
                try {
                    paymentDate = LocalDateTime.parse(payment.getDate(), DateTimeFormatter.ISO_DATE_TIME);

                } catch (Exception e) {
                    LOGGER.error("Error parsing date in {}", payment);
                }
                if (paymentDate != null && LocalDateTime.now().isAfter(paymentDate.plusHours(NUMBER_LATE_HOURS_ACCEPTED))) {
                    final Configuration mailRecipientConfiguration = configurationRepository.findById(MAIL_RECIPIENT).orElse(new Configuration(MAIL_RECIPIENT, dotenv.get("MAIL_RECIPIENT")));

                    mailService.sendEmail(mailRecipientConfiguration.getValue(), "[Hellos] Paiement en retard",
                            "Un paiement a été reçu en retard.\nId : " + payment.getId());
                    processResult.setStatusPayment(StatusPaymentEnum.tooLate);
                    return processResult;
                }
                if (HelloAssoPaymentStateEnum.Waiting.name().equals(notification.getState())) {
                    final Configuration mailRecipientConfiguration = configurationRepository.findById(MAIL_RECIPIENT).orElse(new Configuration(MAIL_RECIPIENT, dotenv.get("MAIL_RECIPIENT")));

                    mailService.sendEmail(mailRecipientConfiguration.getValue(), "[Hellos] Paiement en attente",
                            "Un paiement a été reçu avec l'état 'Attente'.\nId : " + payment.getId());
                    processResult.setStatusPayment(StatusPaymentEnum.waiting);
                    return processResult;
                }

                LOGGER.info("automatic payment to be proceed");
                processResult = creditCyclosAccount(processResult, paymentSaved);
                final Configuration mailRecipientConfiguration = configurationRepository.findById(MAIL_RECIPIENT).orElse(new Configuration(MAIL_RECIPIENT, dotenv.get("MAIL_RECIPIENT")));
                if (StatusPaymentEnum.success.equals(processResult.getStatusPayment())) {
                    mailService.sendEmail(mailRecipientConfiguration.getValue(), "[Hellos] Paiement réussi :)",
                            "Un paiement a été effectué avec succès.\nId : " + paymentSaved.getId());
                    processResult.setStatusPayment(StatusPaymentEnum.successAuto);
                } else {
                    mailService.sendEmail(mailRecipientConfiguration.getValue(), "[Hellos] Paiement en échec :(",
                            "Un paiement n'a pas pu être effectué.\nId : " + paymentSaved.getId());
                }
            }
        } else {
            LOGGER.debug("Payment already inserted in database : {}", notification.getId());
        }

        return processResult;
    }

    public ProcessResult creditAccount(ProcessResult processResult, int paymentId) {
        Payment payment = paymentRepository.findById(paymentId);
        if (payment == null) {
            LOGGER.error("Payment not found : {}", paymentId);
            return processResult;
        }

        creditCyclosAccount(processResult, payment);
        return processResult;
    }

    private ProcessResult creditCyclosAccount(ProcessResult processResult, Payment payment) {
        processResult = cyclosService.creditAccount(processResult, payment.getId());
        payment.setStatus(processResult.getStatusPayment());
        if (!processResult.getErrors().isEmpty() && processResult.getErrors().toString().length() > Payment.ERROR_LENGTH) {
            payment.setError(processResult.getErrors().toString().substring(0, Payment.ERROR_LENGTH - 100));
        } else {
            payment.setError(processResult.getErrors().toString());
        }
        paymentRepository.save(payment);
        sendErrorEmail(processResult, payment.getId());
        return processResult;
    }

    public void sendErrorEmail(ProcessResult processResult, int paymentId) {
        if (!StatusPaymentEnum.success.equals(processResult.getStatusPayment())) {
            String body = "Liste des erreurs pour le paiement " + paymentId + ": \n " + processResult.getErrors().toString();
            String ERROR_SUBJECT = "[Hellos] Erreur lors du traitement";
            mailService.sendEmail(configurationRepository.findById(MAIL_RECIPIENT).get().getValue(), ERROR_SUBJECT, body);
        }
    }

    @Transactional
    public void creditAll() {
        final List<Payment> paymentsToDo = paymentRepository.getByStatus(StatusPaymentEnum.todo);
        for(Payment payment : paymentsToDo){
            creditCyclosAccount(new ProcessResult(), payment);
        }
    }
}
