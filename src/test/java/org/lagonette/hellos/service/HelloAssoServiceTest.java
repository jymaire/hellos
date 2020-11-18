package org.lagonette.hellos.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lagonette.hellos.bean.Notification;
import org.lagonette.hellos.bean.helloasso.HelloAssoOrder;
import org.lagonette.hellos.bean.helloasso.notification.HelloAssoAmount;
import org.lagonette.hellos.bean.helloasso.notification.HelloAssoPaymentNotification;
import org.lagonette.hellos.bean.helloasso.notification.HelloAssoPaymentNotificationBody;
import org.lagonette.hellos.entity.Payment;
import org.lagonette.hellos.repository.PaymentRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HelloAssoServiceTest {

    @Mock
    private MailService mailService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private Dotenv dotenv;

    @InjectMocks
    private HelloAssoService helloAssoService;

    private HelloAssoPaymentNotificationBody helloAssoPaymentBody;
    private HelloAssoPaymentNotification helloAssoPaymentNotification;
    private HelloAssoOrder helloAssoOrder;
    private ObjectMapper mapper = new ObjectMapper();


    @BeforeEach
    void before() throws JsonProcessingException {
        helloAssoOrder = new HelloAssoOrder();
        helloAssoOrder.setFormSlug("form-slug");
        helloAssoPaymentBody = new HelloAssoPaymentNotificationBody();
        helloAssoPaymentBody.setId(11);
        helloAssoPaymentBody.setOrder(helloAssoOrder);
        helloAssoPaymentBody.setAmount(new HelloAssoAmount(240));
        helloAssoPaymentNotification = new HelloAssoPaymentNotification("Payment", mapper.writeValueAsString(helloAssoPaymentBody));
    }

    @Test
    void amountTooHigh() throws IOException {
        // GIVEN
        String helloAssoPaymentNotificationWrapper = "{\"data\": {\"payer\": {\"dateOfBirth\": \"1960-02-28T00:00:00+01:00\", \"email\": \"mail@mail.fr\", \"address\": \"28 Rue Republique\", \"city\": \"VILLEURBANNE\", \"zipCode\": \"69100\", \"country\": \"FRA\", \"firstName\": \"Prenom\", \"lastName\": \"Nom\"}, \"order\": {\"id\": 345, \"date\": \"2020-11-14T11:13:08.2972161+00:00\", \"formSlug\": \"form-slug-1\", \"formType\": \"PaymentForm\", \"organizationSlug\": \"orga\"}, \"items\": [{\"shareAmount\": 15000, \"shareItemAmount\": 15000, \"id\": 18124903, \"amount\": 1500000, \"type\": \"Payment\", \"state\": \"Processed\"}], \"cashOutState\": \"Transfered\", \"paymentReceiptUrl\": \"https://www.helloasso.com/associations/orga/paiement\", \"id\": 11, \"amount\": 1500000, \"date\": \"2020-11-14T11:13:11.6878813+00:00\", \"paymentMeans\": \"Card\", \"state\": \"Authorized\"}, \"eventType\": \"Payment\"}";
        final HelloAssoPaymentNotification helloAssoPaymentNotification = mapper.readValue(helloAssoPaymentNotificationWrapper, HelloAssoPaymentNotification.class);

        when(dotenv.get("MAIL_RECIPIENT")).thenReturn("mail@provider");
        when(dotenv.get("HELLO_ASSO_MAX_AMOUNT")).thenReturn("250");
        when(paymentRepository.findById(helloAssoPaymentBody.getId())).thenReturn(null);

        // WHEN
        Map<Boolean, Notification> isValidPayment = helloAssoService.isValidPayment(helloAssoPaymentNotification);

        // THEN
        assertThat(isValidPayment.containsKey(false)).isTrue();
        verify(mailService).sendEmail(eq("mail@provider"), eq("[Hellos] Paiement dépassant la limite"), anyString());
    }


    @Test
    void amountTooHigh_secondTime() throws IOException {
        // GIVEN
        String helloAssoPaymentNotificationWrapper = "{\"data\": {\"payer\": {\"dateOfBirth\": \"1960-02-28T00:00:00+01:00\", \"email\": \"mail@mail.fr\", \"address\": \"28 Rue Republique\", \"city\": \"VILLEURBANNE\", \"zipCode\": \"69100\", \"country\": \"FRA\", \"firstName\": \"Prenom\", \"lastName\": \"Nom\"}, \"order\": {\"id\": 345, \"date\": \"2020-11-14T11:13:08.2972161+00:00\", \"formSlug\": \"form-slug-1\", \"formType\": \"PaymentForm\", \"organizationSlug\": \"orga\"}, \"items\": [{\"shareAmount\": 15000, \"shareItemAmount\": 15000, \"id\": 18124903, \"amount\": 1500000, \"type\": \"Payment\", \"state\": \"Processed\"}], \"cashOutState\": \"Transfered\", \"paymentReceiptUrl\": \"https://www.helloasso.com/associations/orga/paiement\", \"id\": 11, \"amount\": 1500000, \"date\": \"2020-11-14T11:13:11.6878813+00:00\", \"paymentMeans\": \"Card\", \"state\": \"Authorized\"}, \"eventType\": \"Payment\"}";
        final HelloAssoPaymentNotification helloAssoPaymentNotification = mapper.readValue(helloAssoPaymentNotificationWrapper, HelloAssoPaymentNotification.class);
        when(dotenv.get("HELLO_ASSO_MAX_AMOUNT")).thenReturn("250");
        when(paymentRepository.findById(helloAssoPaymentBody.getId())).thenReturn(new Payment());

        // WHEN
        Map<Boolean, Notification> isValidPayment = helloAssoService.isValidPayment(helloAssoPaymentNotification);

        // THEN
        assertThat(isValidPayment.containsKey(false)).isTrue();
        verifyNoInteractions(mailService);
    }


    @Test
    void emptyNotification() throws IOException {
        // GIVEN
        helloAssoPaymentNotification = null;
        // WHEN
        Map<Boolean, Notification> isValidPayment = helloAssoService.isValidPayment(helloAssoPaymentNotification);
        // THEN
        assertThat(isValidPayment.containsKey(false)).isTrue();
    }
}