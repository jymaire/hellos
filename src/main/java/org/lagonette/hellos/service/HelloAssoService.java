package org.lagonette.hellos.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.lagonette.hellos.bean.Notification;
import org.lagonette.hellos.bean.StatusPaymentEnum;
import org.lagonette.hellos.bean.helloasso.HelloAssoItemCustomField;
import org.lagonette.hellos.bean.helloasso.HelloAssoOrderItem;
import org.lagonette.hellos.bean.helloasso.HelloAssoPaymentStateEnum;
import org.lagonette.hellos.bean.helloasso.notification.HelloAssoOrderNotificationBody;
import org.lagonette.hellos.bean.helloasso.notification.HelloAssoPaymentNotification;
import org.lagonette.hellos.bean.helloasso.notification.HelloAssoPaymentNotificationBody;
import org.lagonette.hellos.entity.EmailLink;
import org.lagonette.hellos.entity.Payment;
import org.lagonette.hellos.repository.ConfigurationRepository;
import org.lagonette.hellos.repository.EmailLinkRepository;
import org.lagonette.hellos.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static org.lagonette.hellos.service.ConfigurationService.MAIL_RECIPIENT;

@Service
public class HelloAssoService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final MailService mailService;
    private final Dotenv dotenv;
    private final EmailLinkRepository emailLinkRepository;
    private final PaymentRepository paymentRepository;
    private final ConfigurationRepository configurationRepository;

    public HelloAssoService(Dotenv dotenv, MailService mailService, EmailLinkRepository emailLinkRepository, PaymentRepository paymentRepository, ConfigurationRepository configurationRepository) {
        this.dotenv = dotenv;
        this.mailService = mailService;
        this.emailLinkRepository = emailLinkRepository;
        this.paymentRepository = paymentRepository;
        this.configurationRepository = configurationRepository;
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
                return processOrder(helloAssoPaymentNotification, end, objectMapper);
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
        if (StringUtils.isEmpty(notification.getDate())) {
            LOGGER.error("Date not set : {}", helloAssoPaymentNotification);
            end.put(false, null);
            return end;
        }

        // convert date to an easier format to read for human
        final LocalDateTime dateTime = LocalDateTime.parse(notification.getDate(), DateTimeFormatter.ISO_DATE_TIME);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss");
        String dateWithEasyToReadFormat = dateTime.format(dateTimeFormatter);
        notification.setDate(dateWithEasyToReadFormat);

        end.put(true, notification);
        return end;
    }

    private Map<Boolean, Notification> processOrder(HelloAssoPaymentNotification helloAssoPaymentNotification, Map<Boolean, Notification> end, ObjectMapper objectMapper) throws IOException {
        // both a payment and an order notifications are sent, only process one
        LOGGER.debug("do not prcess order, in order to avoid double credit");
        // but record, if there is one, the email linked to the Cyclos account
        byte[] json = objectMapper.writeValueAsBytes(helloAssoPaymentNotification.getData());
        HelloAssoOrderNotificationBody helloAssoOrder = objectMapper.readValue(json, HelloAssoOrderNotificationBody.class);
        if (helloAssoOrder != null && !CollectionUtils.isEmpty(helloAssoOrder.getItems())) {
            final String fieldName = dotenv.get("HELLO_ASSO_EXTRA_MAIL_FIELD_NAME");
            if (fieldName == null) {
                LOGGER.debug("Value for HELLO_ASSO_EXTRA_MAIL_FIELD_NAME is not set");
                end.put(false, null);
                return end;
            }
            for (HelloAssoOrderItem item : helloAssoOrder.getItems()) {
                if (!CollectionUtils.isEmpty(item.getCustomFields())) {
                    for (HelloAssoItemCustomField field : item.getCustomFields()) {
                        if (fieldName.equals(field.getName())) {
                            emailLinkRepository.save(new EmailLink(helloAssoOrder.getPayer().getEmail(), field.getAnswer()));
                        }
                    }
                }
            }
        }
        end.put(false, null);
        return end;
    }

    private boolean validatePaymentNotification(HelloAssoPaymentNotification helloAssoPaymentNotification, HelloAssoPaymentNotificationBody helloAssoPayment) {
        final String hello_asso_max_amount = dotenv.get("HELLO_ASSO_MAX_AMOUNT");
        final int threshold = Integer.parseInt(hello_asso_max_amount);
        final int total = helloAssoPayment.getAmount().getTotal();
        if (helloAssoPayment.getAmount() == null || total > (threshold * 100)) {
            Payment payment = paymentRepository.findById(helloAssoPayment.getId());
            if (payment == null) {
                LOGGER.warn("Payment too important : {}", helloAssoPayment.getAmount());
                mailService.sendEmail(configurationRepository.findById(MAIL_RECIPIENT).get().getValue(), "[Hellos] Paiement dépassant la limite",
                        "Un paiement a dépassé la limite autorisée, approbation manuelle requise : \n" + helloAssoPaymentNotification.getData().toString());
                payment = new Payment(helloAssoPayment.getId(), helloAssoPayment.getDate(), total / 100f, helloAssoPayment.getPayer().getFirstName(), helloAssoPayment.getPayer().getLastName(), helloAssoPayment.getPayer().getEmail());
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
        if (!HelloAssoPaymentStateEnum.Authorized.name().equals(helloAssoPayment.getState())) {
            LOGGER.error("Wrong state {}", helloAssoPayment);
            return true;
        }
        return false;
    }
}
