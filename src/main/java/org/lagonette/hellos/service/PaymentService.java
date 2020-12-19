package org.lagonette.hellos.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.lagonette.hellos.bean.Notification;
import org.lagonette.hellos.bean.ProcessResult;
import org.lagonette.hellos.bean.StatusPaymentEnum;
import org.lagonette.hellos.bean.helloasso.notification.HelloAssoPaymentNotification;
import org.lagonette.hellos.entity.Payment;
import org.lagonette.hellos.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class PaymentService {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final String ERROR_SUBJECT = "[Hellos] Erreur lors du traitement";

    private final PaymentRepository paymentRepository;
    private final MailService mailService;
    private final Dotenv dotenv;
    private final HelloAssoService helloAssoService;
    private final CyclosService cyclosService;

    public PaymentService(PaymentRepository paymentRepository, MailService mailService, Dotenv dotenv, HelloAssoService helloAssoService, CyclosService cyclosService) {
        this.paymentRepository = paymentRepository;
        this.mailService = mailService;
        this.dotenv = dotenv;
        this.helloAssoService = helloAssoService;
        this.cyclosService = cyclosService;
    }

    public ProcessResult handleNewPayment(ProcessResult processResult, String helloAssoPaymentNotificationWrapper) throws IOException {

        if (helloAssoPaymentNotificationWrapper == null) {
            LOGGER.error("The payment is empty : {}");
            processResult.getErrors().add("Le paiement est vide ");
            processResult.setStatusPayment(StatusPaymentEnum.fail);
            return processResult;
        }
        ObjectMapper mapper = new ObjectMapper();
        final HelloAssoPaymentNotification helloAssoPaymentNotification = mapper.readValue(helloAssoPaymentNotificationWrapper, HelloAssoPaymentNotification.class);

        // check if payment is real
        Map<Boolean, Notification> validPayment = helloAssoService.isValidPayment(helloAssoPaymentNotification);
        if (validPayment.keySet().contains(false)) {
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
            paymentRepository.save(payment);
        } else {
            LOGGER.debug("Payment already inserted in database : {}", notification.getId());
        }
        return processResult;
    }

    public void creditAccount(ProcessResult processResult, int paymentId) {
        Payment payment = paymentRepository.findById(paymentId);
        if (payment == null) {
            LOGGER.error("Payment not found : {}", paymentId);
            return;
        }

        processResult = cyclosService.creditAccount(processResult, paymentId);
        payment.setStatus(processResult.getStatusPayment());
        if (processResult.getErrors().toString().length() > Payment.ERROR_LENGTH) {
            payment.setError(processResult.getErrors().toString().substring(0, Payment.ERROR_LENGTH - 100));
        }
        paymentRepository.save(payment);
        sendErrorEmail(processResult, paymentId);
    }

    public void sendErrorEmail(ProcessResult processResult, int paymentId) {
        if (!StatusPaymentEnum.success.equals(processResult.getStatusPayment())) {
            String body = "Liste des erreurs pour le paiement " + paymentId + ": \n " + processResult.getErrors().toString();
            mailService.sendEmail(dotenv.get("MAIL_RECIPIENT"), ERROR_SUBJECT, body);
        }
    }
}
