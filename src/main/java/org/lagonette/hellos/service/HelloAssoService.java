package org.lagonette.hellos.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.lagonette.hellos.bean.Notification;
import org.lagonette.hellos.bean.StatusPaymentEnum;
import org.lagonette.hellos.bean.helloasso.*;
import org.lagonette.hellos.bean.helloasso.notification.HelloAssoPaymentNotification;
import org.lagonette.hellos.bean.helloasso.notification.HelloAssoPaymentNotificationBody;
import org.lagonette.hellos.entity.Payment;
import org.lagonette.hellos.repository.ConfigurationRepository;
import org.lagonette.hellos.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.netty.http.client.HttpClient;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.lagonette.hellos.service.ConfigurationService.MAIL_RECIPIENT;

@Service
public class HelloAssoService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final MailService mailService;
    private final Dotenv dotenv;
    private final PaymentRepository paymentRepository;
    private final ConfigurationRepository configurationRepository;

    public HelloAssoService(Dotenv dotenv, MailService mailService, PaymentRepository paymentRepository, ConfigurationRepository configurationRepository) {
        this.dotenv = dotenv;
        this.mailService = mailService;
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
                // both a payment and an order notifications are sent, only process one
                LOGGER.debug("do not prcess order, in order to avoid double credit");
                end.put(false, null);
                return end;
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
        String dateWithEasyToReadFormat = formatDate(notification.getDate());
        notification.setDate(dateWithEasyToReadFormat);
        notification.setState(helloAssoPayment.getState());
        end.put(true, notification);
        return end;
    }

    private String formatDate(String date) {
        final LocalDateTime dateTime = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return dateTime.format(dateTimeFormatter);
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
            if (HelloAssoPaymentStateEnum.Waiting.name().contains(helloAssoPayment.getState())) {
                LOGGER.warn("Late payment : {}", helloAssoPayment.getId());
                return false;
            }
            LOGGER.error("Wrong state {}", helloAssoPayment);
            return true;
        }
        return false;
    }

    public void getPaymentsFor(int nbDays) throws IllegalAccessException {
        String token = getHelloAssoAccessToken();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime beginDate = now.minusDays(nbDays);

        ResponseEntity<HelloAssoFormPayments> formResponse;
        try {
            formResponse = callPaymentFormHistory(token, now, beginDate);
        } catch (WebClientException exception) {
            LOGGER.error("error during data fetch : {}", exception.getCause().getMessage());
            return;
        } finally {
            disconnect(token);
        }

        Set<Payment> paymentsToSave = new HashSet<>();
        final List<Integer> allPayments = paymentRepository.findAllIds();
        if (formResponse != null && formResponse.getBody() != null && formResponse.getBody().getData() != null) {
            formResponse.getBody().getData()
                    .stream()
                    .filter(o -> !allPayments.contains(Integer.parseInt(o.getId())))
                    .forEach(helloAssoPayment -> paymentsToSave
                            .add(new Payment(Integer.parseInt(helloAssoPayment.getId()), formatDate(helloAssoPayment.getDate()), (float) helloAssoPayment.getAmount() / (float) 100,
                                    helloAssoPayment.getPayer().getFirstName(), helloAssoPayment.getPayer().getLastName(), helloAssoPayment.getPayer().getEmail())));
            paymentRepository.saveAll(paymentsToSave);
        }
    }

    public ResponseEntity<HelloAssoFormPayments> callPaymentFormHistory(String accessToken, LocalDateTime now, LocalDateTime beginDate) {
        return WebClient.builder().build().get()
                .uri(dotenv.get("HELLO_ASSO_API_URL") + "v5/organizations/" + dotenv.get("HELLO_ASSO_ORGANIZATION") +
                        "/forms/PaymentForm/" + dotenv.get("HELLO_ASSO_FORM") + "/payments?from=" + beginDate + "&to=" + now + "&states=Authorized")
                .header("authorization", "Bearer " + accessToken)
                .retrieve()
                .toEntity(HelloAssoFormPayments.class)
                .block();
    }

    private String getHelloAssoAccessToken() throws IllegalAccessException {
        LOGGER.info("get access token");

        MultiValueMap accessTokenBody = new LinkedMultiValueMap();
        accessTokenBody.add("client_id", dotenv.get("HELLO_ASSO_CLIENT_ID"));
        accessTokenBody.add("client_secret", dotenv.get("HELLO_ASSO_CLIENT_SECRET"));
        accessTokenBody.add("grant_type", "client_credentials");
        HttpClient httpClient = HttpClient
                .create()
                .wiretap(true);
        HelloAssoToken accessTokenResponse = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient)).build().post()
                .uri(dotenv.get("HELLO_ASSO_API_URL") + "oauth2/token")
                .accept(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(accessTokenBody))
                .accept(MediaType.APPLICATION_JSON)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .retrieve()
                .bodyToMono(HelloAssoToken.class)
                .block();

        LOGGER.info("Token fetched");
        if (accessTokenResponse == null || accessTokenResponse.getAccess_token() == null) {
            LOGGER.error("Erreur lors de la récupération du token : \n{}", accessTokenResponse);
            throw new IllegalAccessException("First access token error");
        }
        return accessTokenResponse.getAccess_token();
    }

    public String getAlternativeEmailFromPayment(int paymentId) {
        String token = "";
        try {
            token = getHelloAssoAccessToken();
            LOGGER.info("about to ask payment to Hello Asso for payment : {}", paymentId);
            if (token == null || token.equals("")) {
                LOGGER.error("Hello Asso Token is empty !");
            }
            final ResponseEntity<HelloAssoPayment> paymentResponse = WebClient.builder().build().get()
                    .uri(dotenv.get("HELLO_ASSO_API_URL") + "v5/payments/" + paymentId)
                    .header("authorization", "Bearer " + token)
                    .retrieve()
                    .toEntity(HelloAssoPayment.class)
                    .block();

            if (paymentResponse != null && paymentResponse.getStatusCode().is2xxSuccessful()
                    && paymentResponse.getBody() != null && paymentResponse.getBody().getOrder() != null) {
                final int orderId = paymentResponse.getBody().getOrder().getId();
                LOGGER.info(" Order ID : {}", orderId);
                final ResponseEntity<HelloAssoOrder> orderResponse = WebClient.builder().build().get()
                        .uri(dotenv.get("HELLO_ASSO_API_URL") + "v5/orders/" + orderId)
                        .header("authorization", "Bearer " + token)
                        .retrieve()
                        .toEntity(HelloAssoOrder.class)
                        .block();
                if (orderResponse != null && orderResponse.getStatusCode().is2xxSuccessful() && orderResponse.getBody() != null) {
                    final List<HelloAssoOrderItem> items = orderResponse.getBody().getItems();
                    if (!CollectionUtils.isEmpty(items)) {
                        LOGGER.info("Item list : {}", items.toString());
                        final String fieldName = dotenv.get("HELLO_ASSO_EXTRA_MAIL_FIELD_NAME");
                        if (fieldName == null) {
                            LOGGER.debug("Value for HELLO_ASSO_EXTRA_MAIL_FIELD_NAME is not set");
                            LOGGER.error("Error during HELLO_ASSO_EXTRA_MAIL_FIELD_NAME fetch: fieldName");
                            return "field-error";
                        }
                        for (HelloAssoOrderItem item : items) {
                            if (!CollectionUtils.isEmpty(item.getCustomFields())) {
                                for (HelloAssoItemCustomField field : item.getCustomFields()) {
                                    if (fieldName.equals(field.getName()) || (field.getAnswer() != null && field.getAnswer().contains("@"))) {
                                        LOGGER.info("alternative email found");
                                        return field.getAnswer();
                                    }
                                }
                            }
                        }
                    }
                } else {
                    LOGGER.error("Error during order fetch: {}", orderResponse);
                    return "order-error";
                }
            } else {
                LOGGER.error("Error during payment fetch: {}", paymentResponse);
                return "payment-error";
            }
        } catch (IllegalAccessException e) {
            LOGGER.error("Error during token fetch: {}", e.getMessage());
            return "token-error";
        } finally {
            if (!StringUtils.isEmpty(token)) {
                disconnect(token);
            }
        }

        return "no-alternative-email-found";
    }

    public void disconnect(String token) {
        final WebClient.ResponseSpec retrieve = WebClient.builder().build().get()
                .uri(dotenv.get("HELLO_ASSO_API_URL") + "oauth2/disconnect")
                .header("authorization", "Bearer " + token)
                .retrieve();
    }
}
