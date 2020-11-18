package org.lagonette.hellos.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.lagonette.hellos.bean.Notification;
import org.lagonette.hellos.bean.StatusPaymentEnum;
import org.lagonette.hellos.bean.helloasso.HelloAssoOrder;
import org.lagonette.hellos.bean.helloasso.notification.HelloAssoPaymentNotification;
import org.lagonette.hellos.bean.helloasso.notification.HelloAssoPaymentNotificationBody;
import org.lagonette.hellos.entity.Payment;
import org.lagonette.hellos.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class HelloAssoService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final MailService mailService;
    private final Dotenv dotenv;
    private final PaymentRepository paymentRepository;

    public HelloAssoService(Dotenv dotenv, MailService mailService, PaymentRepository paymentRepository) {
        this.dotenv = dotenv;
        this.mailService = mailService;
        this.paymentRepository = paymentRepository;
    }

    public Map<Boolean, Notification> isValidPayment(HelloAssoPaymentNotification helloAssoPaymentNotification) throws IOException {
        Map<Boolean, Notification> end = new HashMap<>();
        if (helloAssoPaymentNotification == null || helloAssoPaymentNotification.getData() == null || helloAssoPaymentNotification.getEventType() == null) {
            LOGGER.debug("Empty input (must be scam)");
            end.put(false, null);
            return end;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        HelloAssoPaymentNotificationBody helloAssoPayment;
        HelloAssoOrder order;
        Notification notification;
        try {
            if (helloAssoPaymentNotification.getEventType().equals("Payment")) {
                byte[] json = objectMapper.writeValueAsBytes(helloAssoPaymentNotification.getData());
                helloAssoPayment = objectMapper.readValue(json, HelloAssoPaymentNotificationBody.class);

                if (validatePaymentNotification(helloAssoPaymentNotification, helloAssoPayment)) {
                    end.put(false, null);
                    return end;
                }
                notification = Notification.NotificationBuilder.aNotification()
                        .withAmount(helloAssoPayment.getAmount().getTotal())
                        .withDate(helloAssoPayment.getDate())
                        .withEmail(helloAssoPayment.getPayer().getEmail())
                        .withFirstName(helloAssoPayment.getPayer().getFirstName())
                        .withName(helloAssoPayment.getPayer().getLastName())
                        .withFormSlug(helloAssoPayment.getOrder().getFormSlug())
                        .withId(helloAssoPayment.getId())
                        .build();
            } else if (helloAssoPaymentNotification.getEventType().equals("Order")) {
                byte[] json = objectMapper.writeValueAsBytes(helloAssoPaymentNotification.getData());
                order = objectMapper.readValue(json, HelloAssoOrder.class);
                if (validateOrderNotification(helloAssoPaymentNotification, order)) {
                    end.put(false, null);
                    return end;
                }

                notification = Notification.NotificationBuilder.aNotification()
                        .withAmount(order.getAmount().getTotal())
                        .withId(order.getId())
                        .withFormSlug(order.getFormSlug())
                        .withName(order.getPayer().getLastName())
                        .withFirstName(order.getPayer().getFirstName())
                        .withEmail(order.getPayer().getEmail())
                        .withDate(order.getDate())
                        .build();
            } else {
                LOGGER.error("Error during event type choice : {}", helloAssoPaymentNotification);
                end.put(false, null);
                return end;
            }
        } catch (JsonProcessingException e) {
            LOGGER.error("Error during conversion : {}", e.getMessage());
            end.put(false, null);
            return end;
        }

        if (!dotenv.get("HELLO_ASSO_FORM").equals(notification.getFormSlug())) {
            LOGGER.error("Invalid form slug, the body is : {}", helloAssoPaymentNotification.getData());
            end.put(false, null);
            return end;
        }
        end.put(true, notification);
        return end;
    }

    private boolean validateOrderNotification(HelloAssoPaymentNotification helloAssoPaymentNotification, HelloAssoOrder order) {
        if (order.getAmount() == null || order.getAmount().getTotal() > (Integer.parseInt(dotenv.get("HELLO_ASSO_MAX_AMOUNT")) * 100)) {
            LOGGER.warn("Payment too important : {}", order.getAmount());
            mailService.sendEmail(dotenv.get("MAIL_RECIPIENT"), "[Hellos] Paiement dépassant la limite",
                    "Un paiement a dépassé la limite autorisée, approbation manuelle requise : \n" + helloAssoPaymentNotification.getData().toString());
            return true;
        }
        if (order.getPayer() == null) {
            LOGGER.error("Payer was not set {}", helloAssoPaymentNotification);
            return true;
        }
        return false;
    }

    private boolean validatePaymentNotification(HelloAssoPaymentNotification helloAssoPaymentNotification, HelloAssoPaymentNotificationBody helloAssoPayment) {
        final String hello_asso_max_amount = dotenv.get("HELLO_ASSO_MAX_AMOUNT");
        final int threshold = Integer.parseInt(hello_asso_max_amount);
        final int total = helloAssoPayment.getAmount().getTotal();
        if (helloAssoPayment.getAmount() == null || total > (threshold * 100)) {
            Payment payment = paymentRepository.findById(helloAssoPayment.getId());
            if (payment == null) {
                LOGGER.warn("Payment too important : {}", helloAssoPayment.getAmount());
                mailService.sendEmail(dotenv.get("MAIL_RECIPIENT"), "[Hellos] Paiement dépassant la limite",
                        "Un paiement a dépassé la limite autorisée, approbation manuelle requise : \n" + helloAssoPaymentNotification.getData().toString());
                payment = new Payment(helloAssoPayment.getId(), helloAssoPayment.getDate(), total, helloAssoPayment.getPayer().getFirstName(), helloAssoPayment.getPayer().getLastName(), helloAssoPayment.getPayer().getEmail());
                payment.setStatus(StatusPaymentEnum.tooHigh);
                paymentRepository.save(payment);
            }
            LOGGER.debug("Payment too high {}", helloAssoPaymentNotification);
            return true;
        }
        if (helloAssoPayment.getPayer() == null) {
            LOGGER.error("Payer was not set {}", helloAssoPaymentNotification);
            return true;
        }

        if (helloAssoPayment.getOrder() == null) {
            LOGGER.error("Order was not set {}", helloAssoPaymentNotification);
            return true;
        }
        return false;
    }
}
