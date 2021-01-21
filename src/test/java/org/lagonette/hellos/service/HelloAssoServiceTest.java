package org.lagonette.hellos.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lagonette.hellos.bean.Notification;
import org.lagonette.hellos.bean.helloasso.*;
import org.lagonette.hellos.bean.helloasso.notification.HelloAssoAmount;
import org.lagonette.hellos.bean.helloasso.notification.HelloAssoPaymentNotification;
import org.lagonette.hellos.bean.helloasso.notification.HelloAssoPaymentNotificationBody;
import org.lagonette.hellos.entity.Configuration;
import org.lagonette.hellos.entity.Payment;
import org.lagonette.hellos.repository.ConfigurationRepository;
import org.lagonette.hellos.repository.PaymentRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lagonette.hellos.service.ConfigurationService.MAIL_RECIPIENT;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HelloAssoServiceTest {
    public static MockWebServer mockBackEnd;

    @Mock
    private MailService mailService;

    @Mock
    private ConfigurationRepository configurationRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private Dotenv dotenv;

    @InjectMocks
    private HelloAssoService helloAssoService;

    private HelloAssoPaymentNotificationBody helloAssoPaymentBody;
    private HelloAssoPaymentNotification helloAssoPaymentNotification;
    private final ObjectMapper mapper = new ObjectMapper();
    private String baseUrl;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @BeforeEach
    void before() throws JsonProcessingException {
        baseUrl = String.format("http://localhost:%s", mockBackEnd.getPort());
        HelloAssoOrder helloAssoOrder = new HelloAssoOrder();
        helloAssoOrder.setFormSlug("form-slug");
        helloAssoPaymentBody = new HelloAssoPaymentNotificationBody();
        helloAssoPaymentBody.setId(11);
        helloAssoPaymentBody.setOrder(helloAssoOrder);
        helloAssoPaymentBody.setAmount(new HelloAssoAmount(240));
        helloAssoPaymentNotification = new HelloAssoPaymentNotification("Payment", mapper.writeValueAsString(helloAssoPaymentBody));
    }

    @Test
    void goodPayment() throws IOException {
        // GIVEN
        String helloAssoPaymentNotificationWrapper = "{\"data\": {\"payer\": {\"dateOfBirth\": \"1960-02-28T00:00:00+01:00\", \"email\": \"mail@mail.fr\", \"address\": \"28 Rue Republique\", \"city\": \"VILLEURBANNE\", \"zipCode\": \"69100\", \"country\": \"FRA\", \"firstName\": \"Prenom\", \"lastName\": \"Nom\"}, \"order\": {\"id\": 345, \"date\": \"2020-11-14T11:13:08.2972161+00:00\", \"formSlug\": \"form-slug-1\", \"formType\": \"PaymentForm\", \"organizationSlug\": \"orga\"}, \"items\": [{\"shareAmount\": 1500, \"shareItemAmount\": 15000, \"id\": 18124903, \"amount\": 1500, \"type\": \"Payment\", \"state\": \"Processed\"}], \"cashOutState\": \"Transfered\", \"paymentReceiptUrl\": \"https://www.helloasso.com/associations/orga/paiement\", \"id\": 11, \"amount\": 1500, \"date\": \"2020-11-14T11:13:12.6878813+00:00\", \"paymentMeans\": \"Card\", \"state\": \"Authorized\"}, \"eventType\": \"Payment\"}";
        final HelloAssoPaymentNotification helloAssoPaymentNotification = mapper.readValue(helloAssoPaymentNotificationWrapper, HelloAssoPaymentNotification.class);

        when(dotenv.get("HELLO_ASSO_FORM")).thenReturn("form-slug-1");
        when(dotenv.get("HELLO_ASSO_MAX_AMOUNT")).thenReturn("250");

        // WHEN
        Map<Boolean, Notification> isValidPayment = helloAssoService.isValidPayment(helloAssoPaymentNotification);

        // THEN
        assertThat(isValidPayment.containsKey(true)).isTrue();
        assertThat(isValidPayment.get(true).getDate()).isEqualTo("14/11/2020 11:13:12");
    }

    @Test
    void noDate() throws IOException {
        // GIVEN
        String helloAssoPaymentNotificationWrapper = "{\"data\": {\"payer\": {\"dateOfBirth\": \"1960-02-28T00:00:00+01:00\", \"email\": \"mail@mail.fr\", \"address\": \"28 Rue Republique\", \"city\": \"VILLEURBANNE\", \"zipCode\": \"69100\", \"country\": \"FRA\", \"firstName\": \"Prenom\", \"lastName\": \"Nom\"}, \"order\": {\"id\": 345, \"date\": \"2020-11-14T11:13:08.2972161+00:00\", \"formSlug\": \"form-slug-1\", \"formType\": \"PaymentForm\", \"organizationSlug\": \"orga\"}, \"items\": [{\"shareAmount\": 1500, \"shareItemAmount\": 15000, \"id\": 18124903, \"amount\": 1500, \"type\": \"Payment\", \"state\": \"Processed\"}], \"cashOutState\": \"Transfered\", \"paymentReceiptUrl\": \"https://www.helloasso.com/associations/orga/paiement\", \"id\": 11, \"amount\": 1500, \"date\": \"\", \"paymentMeans\": \"Card\", \"state\": \"Authorized\"}, \"eventType\": \"Payment\"}";
        final HelloAssoPaymentNotification helloAssoPaymentNotification = mapper.readValue(helloAssoPaymentNotificationWrapper, HelloAssoPaymentNotification.class);

        when(dotenv.get("HELLO_ASSO_FORM")).thenReturn("form-slug-1");
        when(dotenv.get("HELLO_ASSO_MAX_AMOUNT")).thenReturn("250");

        // WHEN
        Map<Boolean, Notification> isValidPayment = helloAssoService.isValidPayment(helloAssoPaymentNotification);

        // THEN
        assertThat(isValidPayment.containsKey(false)).isTrue();
    }

    @Test
    void wrongForm() throws IOException {
        // GIVEN
        String helloAssoPaymentNotificationWrapper = "{\"data\": {\"payer\": {\"dateOfBirth\": \"1960-02-28T00:00:00+01:00\", \"email\": \"mail@mail.fr\", \"address\": \"28 Rue Republique\", \"city\": \"VILLEURBANNE\", \"zipCode\": \"69100\", \"country\": \"FRA\", \"firstName\": \"Prenom\", \"lastName\": \"Nom\"}, \"order\": {\"id\": 345, \"date\": \"2020-11-14T11:13:08.2972161+00:00\", \"formSlug\": \"form-slug-1\", \"formType\": \"PaymentForm\", \"organizationSlug\": \"orga\"}, \"items\": [{\"shareAmount\": 1500, \"shareItemAmount\": 15000, \"id\": 18124903, \"amount\": 1500, \"type\": \"Payment\", \"state\": \"Processed\"}], \"cashOutState\": \"Transfered\", \"paymentReceiptUrl\": \"https://www.helloasso.com/associations/orga/paiement\", \"id\": 11, \"amount\": 1500, \"date\": \"2020-11-14T11:13:11.6878813+00:00\", \"paymentMeans\": \"Card\", \"state\": \"Authorized\"}, \"eventType\": \"Payment\"}";
        final HelloAssoPaymentNotification helloAssoPaymentNotification = mapper.readValue(helloAssoPaymentNotificationWrapper, HelloAssoPaymentNotification.class);

        when(dotenv.get("HELLO_ASSO_FORM")).thenReturn("form-slug");
        when(dotenv.get("HELLO_ASSO_MAX_AMOUNT")).thenReturn("250");

        // WHEN
        Map<Boolean, Notification> isValidPayment = helloAssoService.isValidPayment(helloAssoPaymentNotification);

        // THEN
        assertThat(isValidPayment.containsKey(false)).isTrue();
    }

    @Test
    void amountTooHigh() throws IOException {
        // GIVEN
        String helloAssoPaymentNotificationWrapper = "{\"data\": {\"payer\": {\"dateOfBirth\": \"1960-02-28T00:00:00+01:00\", \"email\": \"mail@mail.fr\", \"address\": \"28 Rue Republique\", \"city\": \"VILLEURBANNE\", \"zipCode\": \"69100\", \"country\": \"FRA\", \"firstName\": \"Prenom\", \"lastName\": \"Nom\"}, \"order\": {\"id\": 345, \"date\": \"2020-11-14T11:13:08.2972161+00:00\", \"formSlug\": \"form-slug-1\", \"formType\": \"PaymentForm\", \"organizationSlug\": \"orga\"}, \"items\": [{\"shareAmount\": 15000, \"shareItemAmount\": 15000, \"id\": 18124903, \"amount\": 1500000, \"type\": \"Payment\", \"state\": \"Processed\"}], \"cashOutState\": \"Transfered\", \"paymentReceiptUrl\": \"https://www.helloasso.com/associations/orga/paiement\", \"id\": 11, \"amount\": 1500000, \"date\": \"2020-11-14T11:13:11.6878813+00:00\", \"paymentMeans\": \"Card\", \"state\": \"Authorized\"}, \"eventType\": \"Payment\"}";
        final HelloAssoPaymentNotification helloAssoPaymentNotification = mapper.readValue(helloAssoPaymentNotificationWrapper, HelloAssoPaymentNotification.class);

        when(configurationRepository.findById(MAIL_RECIPIENT)).thenReturn(Optional.of(new Configuration(MAIL_RECIPIENT, "mail@provider")));
        when(dotenv.get("HELLO_ASSO_MAX_AMOUNT")).thenReturn("250");
        when(paymentRepository.findById(helloAssoPaymentBody.getId())).thenReturn(null);

        // WHEN
        Map<Boolean, Notification> isValidPayment = helloAssoService.isValidPayment(helloAssoPaymentNotification);

        // THEN
        assertThat(isValidPayment.containsKey(false)).isTrue();
        verify(mailService).sendEmail(eq("mail@provider"), eq("[Hellos] Paiement dépassant la limite"), anyString());
        ArgumentCaptor<Payment> argumentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getAmount()).isEqualTo(15000);
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
    void wrongNotificationType() throws IOException {
        // GIVEN
        String helloAssoPaymentNotificationWrapper = "{\"data\": {\"payer\": {\"dateOfBirth\": \"1960-02-28T00:00:00+01:00\", \"email\": \"mail@mail.fr\", \"address\": \"28 Rue Republique\", \"city\": \"VILLEURBANNE\", \"zipCode\": \"69100\", \"country\": \"FRA\", \"firstName\": \"Prenom\", \"lastName\": \"Nom\"}, \"order\": {\"id\": 345, \"date\": \"2020-11-14T11:13:08.2972161+00:00\", \"formSlug\": \"form-slug-1\", \"formType\": \"PaymentForm\", \"organizationSlug\": \"orga\"}, \"items\": [{\"shareAmount\": 15000, \"shareItemAmount\": 15000, \"id\": 18124903, \"amount\": 1500000, \"type\": \"Payment\", \"state\": \"Processed\"}], \"cashOutState\": \"Transfered\", \"paymentReceiptUrl\": \"https://www.helloasso.com/associations/orga/paiement\", \"id\": 11, \"amount\": 1500, \"date\": \"2020-11-14T11:13:11.6878813+00:00\", \"paymentMeans\": \"Card\", \"state\": \"Authorized\"}, \"eventType\": \"Order\"}";
        final HelloAssoPaymentNotification helloAssoPaymentNotification = mapper.readValue(helloAssoPaymentNotificationWrapper, HelloAssoPaymentNotification.class);

        // WHEN
        Map<Boolean, Notification> isValidPayment = helloAssoService.isValidPayment(helloAssoPaymentNotification);

        // THEN
        assertThat(isValidPayment.containsKey(false)).isTrue();
        verifyNoInteractions(mailService);
    }

    @Test
    void emptyNotification() throws IOException {
        // WHEN
        Map<Boolean, Notification> isValidPayment = helloAssoService.isValidPayment(null);
        // THEN
        assertThat(isValidPayment.containsKey(false)).isTrue();
    }

    @Test
    void refusedPayment() throws IOException {
        // GIVEN
        String helloAssoPaymentNotificationWrapper = "{\"data\": {\"payer\": {\"dateOfBirth\": \"1960-02-28T00:00:00+01:00\", \"email\": \"mail@mail.fr\", \"address\": \"28 Rue Republique\", \"city\": \"VILLEURBANNE\", \"zipCode\": \"69100\", \"country\": \"FRA\", \"firstName\": \"Prenom\", \"lastName\": \"Nom\"}, \"order\": {\"id\": 345, \"date\": \"2020-11-14T11:13:08.2972161+00:00\", \"formSlug\": \"form-slug-1\", \"formType\": \"PaymentForm\", \"organizationSlug\": \"orga\"}, \"items\": [{\"shareAmount\": 1500, \"shareItemAmount\": 15000, \"id\": 18124903, \"amount\": 1500, \"type\": \"Payment\", \"state\": \"Processed\"}], \"cashOutState\": \"Transfered\", \"paymentReceiptUrl\": \"https://www.helloasso.com/associations/orga/paiement\", \"id\": 11, \"amount\": 1500, \"date\": \"2020-11-14T11:13:11.6878813+00:00\", \"paymentMeans\": \"Card\", \"state\": \"Refused\"}, \"eventType\": \"Payment\"}";
        final HelloAssoPaymentNotification helloAssoPaymentNotification = mapper.readValue(helloAssoPaymentNotificationWrapper, HelloAssoPaymentNotification.class);
        when(dotenv.get("HELLO_ASSO_MAX_AMOUNT")).thenReturn("250");

        // WHEN
        Map<Boolean, Notification> isValidPayment = helloAssoService.isValidPayment(helloAssoPaymentNotification);

        // THEN
        assertThat(isValidPayment.containsKey(false)).isTrue();
    }

    @Test
    void order() throws IOException {
        // GIVEN
        String order = "{\"data\": {\"payer\": {\"dateOfBirth\": \"1970-01-08T00:00:00+01:00\", \"email\": \"helloasso@email.fr\", \"address\": \"1 rue de la republique\", \"city\": \"lyon\", \"zipCode\": \"69001\", \"country\": \"FRA\", \"firstName\": \"Jean\", \"lastName\": \"DUPONT\"}, \"items\": [{\"payments\": [{\"id\": 9628319, \"shareAmount\": 5000}], \"user\": {\"firstName\": \"Jean\", \"lastName\": \"DUPONT\"}, \"priceCategory\": \"Free\", \"customFields\": [{\"name\": \"Adresse courriel compte Cyclos ? (si diff\\u00e9rente de celle du compte Helloasso)\", \"type\": \"TextInput\", \"answer\": \"cyclos@email.fr\"}], \"isCanceled\": false, \"id\": 18801433, \"amount\": 5000, \"type\": \"Payment\", \"initialAmount\": 0, \"state\": \"Processed\"}], \"payments\": [{\"items\": [{\"id\": 18801433, \"shareAmount\": 5000, \"shareItemAmount\": 5000}], \"cashOutState\": \"Transfered\", \"paymentReceiptUrl\": \"https://www.helloasso.com/associations/url/form\", \"id\": 9628319, \"amount\": 5000, \"date\": \"2020-12-08T12:38:44.4010985+00:00\", \"paymentMeans\": \"Card\", \"state\": \"Authorized\"}], \"amount\": {\"total\": 5000, \"vat\": 0, \"discount\": 0}, \"id\": 18801433, \"date\": \"2020-12-08T12:38:44.4010985+00:00\", \"formSlug\": \"crediter-par-cb\", \"formType\": \"PaymentForm\", \"organizationSlug\": \"asso\"}, \"eventType\": \"Order\"}";
        final HelloAssoPaymentNotification helloAssoPaymentNotification = mapper.readValue(order, HelloAssoPaymentNotification.class);

        // WHEN
        Map<Boolean, Notification> isValidPayment = helloAssoService.isValidPayment(helloAssoPaymentNotification);

        // THEN
        assertThat(isValidPayment.containsKey(false)).isTrue();
    }

    @Test
    void getExistingAlternateEmail() throws JsonProcessingException {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        HelloAssoToken accessTokenResponse = new HelloAssoToken("access", "type", "expire", "refresh");
        mockBackEnd.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(accessTokenResponse))
                .addHeader("Content-Type", "application/json"));
        HelloAssoPayment payment = new HelloAssoPayment();
        HelloAssoOrder order = new HelloAssoOrder();
        order.setId(44);
        HelloAssoOrderItem item = new HelloAssoOrderItem();
        item.setName("itemName");
        final HelloAssoItemCustomField alternateEmailField = new HelloAssoItemCustomField();
        alternateEmailField.setName("fieldName");
        alternateEmailField.setAnswer("alternate@email.fr");
        item.setCustomFields(List.of(alternateEmailField));
        order.setItems(List.of(item));
        payment.setOrder(order);
        mockBackEnd.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(payment))
                .addHeader("Content-Type", "application/json"));

        mockBackEnd.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(order))
                .addHeader("Content-Type", "application/json"));

        when(dotenv.get("HELLO_ASSO_CLIENT_ID")).thenReturn("id");
        when(dotenv.get("HELLO_ASSO_CLIENT_SECRET")).thenReturn("such a secret");
        when(dotenv.get("HELLO_ASSO_API_URL")).thenReturn(baseUrl);
        when(dotenv.get("HELLO_ASSO_EXTRA_MAIL_FIELD_NAME")).thenReturn("fieldName");

        // WHEN
        final String alternativeEmailFromPayment = helloAssoService.getAlternativeEmailFromPayment(1);

        // THEN
        assertThat(alternativeEmailFromPayment).isEqualTo("alternate@email.fr");
    }

    @Test
    void getMissingAlternateEmail() throws JsonProcessingException {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        HelloAssoToken accessTokenResponse = new HelloAssoToken("access", "type", "expire", "refresh");
        mockBackEnd.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(accessTokenResponse))
                .addHeader("Content-Type", "application/json"));
        HelloAssoPayment payment = new HelloAssoPayment();
        HelloAssoOrder order = new HelloAssoOrder();
        order.setId(44);
        payment.setOrder(order);
        mockBackEnd.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(payment))
                .addHeader("Content-Type", "application/json"));

        mockBackEnd.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(order))
                .addHeader("Content-Type", "application/json"));

        when(dotenv.get("HELLO_ASSO_CLIENT_ID")).thenReturn("id");
        when(dotenv.get("HELLO_ASSO_CLIENT_SECRET")).thenReturn("such a secret");
        when(dotenv.get("HELLO_ASSO_API_URL")).thenReturn(baseUrl);

        // WHEN
        final String noMail = helloAssoService.getAlternativeEmailFromPayment(2);

        // THEN
        assertThat(noMail).isEqualTo("no-alternative-email-found");
    }

    @Test
    void fetchDataFromHelloAsso_shouldNotErasePreviousPaymentState() throws IllegalAccessException, JsonProcessingException {
        // GIVEN
        when(dotenv.get("HELLO_ASSO_CLIENT_ID")).thenReturn("id");
        when(dotenv.get("HELLO_ASSO_CLIENT_SECRET")).thenReturn("such a secret");
        when(dotenv.get("HELLO_ASSO_API_URL")).thenReturn(baseUrl);
        when(dotenv.get("HELLO_ASSO_ORGANIZATION")).thenReturn("orga");
        when(dotenv.get("HELLO_ASSO_FORM")).thenReturn("form-name");
        when(paymentRepository.findAllIds()).thenReturn(List.of(333, 1));
        HelloAssoToken accessTokenResponse = new HelloAssoToken("access", "type", "expire", "refresh");
        ObjectMapper objectMapper = new ObjectMapper();
        mockBackEnd.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(accessTokenResponse))
                .addHeader("Content-Type", "application/json"));
        HelloAssoFormPayments paymentsReceivedForm = new HelloAssoFormPayments();
        HelloAssoPayment payment1 = HelloAssoPayment.HelloAssoPaymentBuilder.aHelloAssoPayment()
                .withId("1")
                .withAmount(100)
                .withDate("2020-12-08T12:37:46.570896+00:00")
                .withPayer(HelloAssoPayer.HelloAssoPayerBuilder.aHelloAssoPayer()
                        .withFirstName("f1")
                        .withLastName("l1")
                        .withEmail("e1")
                        .build())
                .build();
        HelloAssoPayment payment2 = HelloAssoPayment.HelloAssoPaymentBuilder.aHelloAssoPayment()
                .withId("2")
                .withAmount(200)
                .withDate("2020-12-09T12:37:46.570896+00:00")
                .withPayer(HelloAssoPayer.HelloAssoPayerBuilder.aHelloAssoPayer()
                        .withFirstName("f2")
                        .withLastName("l2")
                        .withEmail("e2")
                        .build())
                .build();
        List<HelloAssoPayment> payments = List.of(payment1, payment2);
        paymentsReceivedForm.setData(payments);
        mockBackEnd.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(paymentsReceivedForm))
                .addHeader("Content-Type", "application/json"));
        // WHEN
        helloAssoService.getPaymentsFor(3);

        // THEN
        Payment paymentInDb2 = Payment.PaymentBuilder.aPayment()
                .withId(2)
                .withAmount(200)
                .build();
        Set<Payment> paymentsInDatabase = Set.of(paymentInDb2);
        verify(paymentRepository).saveAll(paymentsInDatabase);
    }
}