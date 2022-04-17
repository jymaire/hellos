package org.lagonette.hellos.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lagonette.hellos.bean.ProcessResult;
import org.lagonette.hellos.bean.StatusPaymentEnum;
import org.lagonette.hellos.bean.cyclos.CyclosGroup;
import org.lagonette.hellos.bean.cyclos.CyclosPerformPaymentResponse;
import org.lagonette.hellos.bean.cyclos.CyclosTransaction;
import org.lagonette.hellos.bean.cyclos.CyclosUser;
import org.lagonette.hellos.entity.Configuration;
import org.lagonette.hellos.entity.Payment;
import org.lagonette.hellos.repository.ConfigurationRepository;
import org.lagonette.hellos.repository.PaymentRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lagonette.hellos.service.ConfigurationService.PAYMENT_CYCLOS_ENABLED;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CyclosServiceTest {

    public static MockWebServer mockBackEnd;

    @Mock
    private Dotenv dotenv;

    @Mock
    private ConfigurationRepository configurationRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private HelloAssoService helloAssoService;

    @InjectMocks
    private CyclosService cyclosService;

    private Payment payment;
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
    void initialize() {
        baseUrl = String.format("http://localhost:%s", mockBackEnd.getPort());
    }

    @Test
    void creditAccount_shouldBeSuccessful() throws JsonProcessingException {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        CyclosUser cyclosUser = new CyclosUser();
        CyclosGroup group = new CyclosGroup();
        group.setInternalName("group-name");
        cyclosUser.setGroup(group);

        // Mock all HTTP calls
        // first call, get user
        mockBackEnd.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(cyclosUser))
                .addHeader("Content-Type", "application/json"));
        // second call, get previous transactions
        List<CyclosTransaction> transactions = new ArrayList<>();
        mockBackEnd.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(transactions))
                .addHeader("Content-Type", "application/json"));
        // third call, post payment
        CyclosPerformPaymentResponse paymentDone = new CyclosPerformPaymentResponse();
        List<CyclosPerformPaymentResponse> cyclosPerformPaymentResponse = Collections.singletonList(paymentDone);
        mockBackEnd.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(cyclosPerformPaymentResponse))
                .addHeader("Content-Type", "application/json"));
        payment = Payment.PaymentBuilder.aPayment().build();
        when(paymentRepository.findById(1)).thenReturn(payment);
        when(dotenv.get("CYCLOS_EMISSION_PART_INTERNAL")).thenReturn("emission");
        when(dotenv.get("CYCLOS_GROUP_PART_INTERNAL")).thenReturn("group-name");
        when(dotenv.get("CYCLOS_GROUP_PRO_INTERNAL")).thenReturn("pro-group-name");

        when(dotenv.get("CYCLOS_URL")).thenReturn(baseUrl);
        when(dotenv.get("CYCLOS_USER")).thenReturn("user");
        when(dotenv.get("CYCLOS_PWD")).thenReturn("pwd");

        when(configurationRepository.findById(PAYMENT_CYCLOS_ENABLED)).thenReturn(Optional.of(new Configuration(PAYMENT_CYCLOS_ENABLED, "true")));
        ProcessResult processResult = new ProcessResult();

        // WHEN
        final ProcessResult finalProcessResult = cyclosService.creditAccount(processResult, 1);

        // THEN
        assertThat(finalProcessResult.getStatusPayment()).isEqualTo(StatusPaymentEnum.success);
        assertThat(finalProcessResult.getErrors()).isEmpty();
    }

    @Test
    void creditAccountWithTwoParticulierGroup_shouldBeSuccessful() throws JsonProcessingException {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        CyclosUser cyclosUser = new CyclosUser();
        CyclosGroup group = new CyclosGroup();
        group.setInternalName("group-name");
        cyclosUser.setGroup(group);

        // Mock all HTTP calls
        // first call, get user
        mockBackEnd.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(cyclosUser))
                .addHeader("Content-Type", "application/json"));
        // second call, get previous transactions
        List<CyclosTransaction> transactions = new ArrayList<>();
        mockBackEnd.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(transactions))
                .addHeader("Content-Type", "application/json"));
        // third call, post payment
        CyclosPerformPaymentResponse paymentDone = new CyclosPerformPaymentResponse();
        List<CyclosPerformPaymentResponse> cyclosPerformPaymentResponse = Collections.singletonList(paymentDone);
        mockBackEnd.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(cyclosPerformPaymentResponse))
                .addHeader("Content-Type", "application/json"));
        payment = Payment.PaymentBuilder.aPayment().build();
        when(paymentRepository.findById(1)).thenReturn(payment);
        when(dotenv.get("CYCLOS_EMISSION_PART_INTERNAL")).thenReturn("emission");
        when(dotenv.get("CYCLOS_GROUP_PART_INTERNAL")).thenReturn("group-name,anothergroup");
        when(dotenv.get("CYCLOS_GROUP_PRO_INTERNAL")).thenReturn("pro-group-name");

        when(dotenv.get("CYCLOS_URL")).thenReturn(baseUrl);
        when(dotenv.get("CYCLOS_USER")).thenReturn("user");
        when(dotenv.get("CYCLOS_PWD")).thenReturn("pwd");

        when(configurationRepository.findById(PAYMENT_CYCLOS_ENABLED)).thenReturn(Optional.of(new Configuration(PAYMENT_CYCLOS_ENABLED, "true")));
        ProcessResult processResult = new ProcessResult();

        // WHEN
        final ProcessResult finalProcessResult = cyclosService.creditAccount(processResult, 1);
        RecordedRequest postPaymentRequest;
        String body = "";
        try {
            // unstack first requests to get the POST Payment request
            mockBackEnd.takeRequest();
            mockBackEnd.takeRequest();
            postPaymentRequest = mockBackEnd.takeRequest();
            body = postPaymentRequest.getBody().readUtf8();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // THEN
        assertThat(finalProcessResult.getStatusPayment()).isEqualTo(StatusPaymentEnum.success);
        assertThat(body).contains("emission");
        assertThat(finalProcessResult.getErrors()).isEmpty();
    }

    @Test
    void creditAccount_shouldBePreviewed() throws JsonProcessingException {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        CyclosUser cyclosUser = new CyclosUser();
        CyclosGroup group = new CyclosGroup();
        group.setInternalName("group-name");
        cyclosUser.setGroup(group);

        // Mock all HTTP calls
        // first call, get user
        mockBackEnd.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(cyclosUser))
                .addHeader("Content-Type", "application/json"));
        // second call, get previous transactions
        List<CyclosTransaction> transactions = new ArrayList<>();
        mockBackEnd.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(transactions))
                .addHeader("Content-Type", "application/json"));
        // third call, post payment
        CyclosPerformPaymentResponse paymentDone = new CyclosPerformPaymentResponse();
        List<CyclosPerformPaymentResponse> cyclosPerformPaymentResponse = Collections.singletonList(paymentDone);
        mockBackEnd.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(cyclosPerformPaymentResponse))
                .addHeader("Content-Type", "application/json"));
        payment = Payment.PaymentBuilder.aPayment().build();
        when(paymentRepository.findById(1)).thenReturn(payment);
        when(dotenv.get("CYCLOS_EMISSION_PART_INTERNAL")).thenReturn("emission");
        when(dotenv.get("CYCLOS_GROUP_PART_INTERNAL")).thenReturn("group-name");
        when(dotenv.get("CYCLOS_GROUP_PRO_INTERNAL")).thenReturn("pro-group-name");

        when(dotenv.get("CYCLOS_URL")).thenReturn(baseUrl);
        when(dotenv.get("CYCLOS_USER")).thenReturn("user");
        when(dotenv.get("CYCLOS_PWD")).thenReturn("pwd");

        when(configurationRepository.findById(PAYMENT_CYCLOS_ENABLED)).thenReturn(Optional.of(new Configuration(PAYMENT_CYCLOS_ENABLED, "false")));
        ProcessResult processResult = new ProcessResult();

        // WHEN
        final ProcessResult finalProcessResult = cyclosService.creditAccount(processResult, 1);

        // THEN
        assertThat(finalProcessResult.getStatusPayment()).isEqualTo(StatusPaymentEnum.previewOK);
        assertThat(finalProcessResult.getErrors()).isEmpty();
    }

    @Test
    void creditAccount_alreadyDone() {
        // GIVEN
        ProcessResult processResult = new ProcessResult();
        payment = Payment.PaymentBuilder.aPayment().build();
        payment.setStatus(StatusPaymentEnum.success);
        when(paymentRepository.findById(1)).thenReturn(payment);

        // WHEN
        final ProcessResult finalProcessResult = cyclosService.creditAccount(processResult, 1);

        // THEN
        assertThat(finalProcessResult.getStatusPayment()).isEqualTo(StatusPaymentEnum.success);
        assertThat(finalProcessResult.getErrors()).isNotEmpty();
        assertThat(finalProcessResult.getErrors()).contains("Paiement déjà effectué dans Cyclos");
    }

    @Test
    void creditAccount_accountNotFound() {
        // GIVEN
        CyclosUser cyclosUser = new CyclosUser();
        CyclosGroup group = new CyclosGroup();
        group.setInternalName("group-name");
        cyclosUser.setGroup(group);

        // Mock all HTTP calls
        // first call, get user
        mockBackEnd.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json"));
        mockBackEnd.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json"));

        payment = Payment.PaymentBuilder.aPayment().withId(1).withEmail("email@email.fr").build();
        when(paymentRepository.findById(1)).thenReturn(payment);
        when(dotenv.get("CYCLOS_URL")).thenReturn(baseUrl);
        when(dotenv.get("CYCLOS_USER")).thenReturn("user");
        when(dotenv.get("CYCLOS_PWD")).thenReturn("pwd");
        when(dotenv.get("CYCLOS_GROUP_PART_INTERNAL")).thenReturn("group-name");
        when(helloAssoService.getAlternativeEmailFromPayment(1)).thenReturn("erreur");

        ProcessResult processResult = new ProcessResult();

        // WHEN
        final ProcessResult finalProcessResult = cyclosService.creditAccount(processResult, 1);

        // THEN
        assertThat(finalProcessResult.getStatusPayment()).isEqualTo(StatusPaymentEnum.fail);
        assertThat(finalProcessResult.getErrors()).isNotEmpty();
        verify(paymentRepository).findById(1);
    }

    @Test
    void creditAccount_accountNotFoundAtFirstTime() throws JsonProcessingException {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        CyclosUser cyclosUser = new CyclosUser();
        CyclosGroup group = new CyclosGroup();
        group.setInternalName("group-name");
        cyclosUser.setGroup(group);

        // Mock all HTTP calls
        // first call, get user, but not found
        mockBackEnd.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json"));
        // then get user with the registered email
        mockBackEnd.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(cyclosUser))
                .addHeader("Content-Type", "application/json"));
        // get previous transactions
        List<CyclosTransaction> transactions = new ArrayList<>();
        mockBackEnd.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(transactions))
                .addHeader("Content-Type", "application/json"));
        // post payment
        CyclosPerformPaymentResponse paymentDone = new CyclosPerformPaymentResponse();
        List<CyclosPerformPaymentResponse> cyclosPerformPaymentResponse = Collections.singletonList(paymentDone);
        mockBackEnd.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(cyclosPerformPaymentResponse))
                .addHeader("Content-Type", "application/json"));
        payment = Payment.PaymentBuilder.aPayment().withId(1).withEmail("email@email.fr").build();
        when(paymentRepository.findById(1)).thenReturn(payment);
        when(dotenv.get("CYCLOS_EMISSION_PART_INTERNAL")).thenReturn("emission");
        when(dotenv.get("CYCLOS_GROUP_PART_INTERNAL")).thenReturn("group-name");
        when(dotenv.get("CYCLOS_GROUP_PRO_INTERNAL")).thenReturn("pro-group-name");

        when(dotenv.get("CYCLOS_URL")).thenReturn(baseUrl);
        when(dotenv.get("CYCLOS_USER")).thenReturn("user");
        when(dotenv.get("CYCLOS_PWD")).thenReturn("pwd");
        when(helloAssoService.getAlternativeEmailFromPayment(1)).thenReturn("altenate@email.fr");

        when(configurationRepository.findById(PAYMENT_CYCLOS_ENABLED)).thenReturn(Optional.of(new Configuration(PAYMENT_CYCLOS_ENABLED, "true")));
        ProcessResult processResult = new ProcessResult();

        // WHEN
        final ProcessResult finalProcessResult = cyclosService.creditAccount(processResult, 1);

        // THEN
        assertThat(finalProcessResult.getStatusPayment()).isEqualTo(StatusPaymentEnum.success);
        assertThat(finalProcessResult.getErrors()).isEmpty();
        verify(paymentRepository).findById(1);
    }
}