package org.lagonette.hellos.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lagonette.hellos.bean.Notification;
import org.lagonette.hellos.bean.ProcessResult;
import org.lagonette.hellos.bean.StatusPaymentEnum;
import org.lagonette.hellos.bean.helloasso.HelloAssoOrder;
import org.lagonette.hellos.bean.helloasso.HelloAssoPayer;
import org.lagonette.hellos.bean.helloasso.notification.HelloAssoAmount;
import org.lagonette.hellos.bean.helloasso.notification.HelloAssoPaymentNotification;
import org.lagonette.hellos.bean.helloasso.notification.HelloAssoPaymentNotificationBody;
import org.lagonette.hellos.entity.Payment;
import org.lagonette.hellos.repository.PaymentRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private HelloAssoService helloAssoService;

    @Mock
    private CyclosService cyclosService;

    @Mock
    private Dotenv dotenv;

    @Mock
    private MailService mailService;

    @InjectMocks
    private PaymentService paymentService;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    void handleNewPayment_success() throws IOException {
        // GIVEN
        ProcessResult processResult = new ProcessResult(StatusPaymentEnum.success, new HashSet<>());
        HelloAssoPayer helloAssoPayer = new HelloAssoPayer();
        helloAssoPayer.setEmail("email@here");
        HelloAssoPaymentNotificationBody helloAssoPayment = new HelloAssoPaymentNotificationBody();
        helloAssoPayment.setId(1);
        helloAssoPayment.setAmount(new HelloAssoAmount(240));
        helloAssoPayment.setDate("2018-01-01T00:00:00");
        helloAssoPayer.setFirstName("prenom");
        helloAssoPayer.setLastName("nom");
        helloAssoPayment.setPayer(helloAssoPayer);
        HelloAssoOrder order = new HelloAssoOrder();
        order.setFormSlug("formSlug");
        helloAssoPayment.setOrder(order);
        HelloAssoPaymentNotification helloAssoPaymentNotification = new HelloAssoPaymentNotification("Payment", mapper.writeValueAsString(helloAssoPayment));

        Map<Boolean, Notification> result = new HashMap<>();
        Notification notification = Notification.NotificationBuilder.aNotification()
                .withAmount(helloAssoPayment.getAmount().getTotal())
                .withDate(helloAssoPayment.getDate())
                .withEmail(helloAssoPayment.getPayer().getEmail())
                .withFirstName(helloAssoPayment.getPayer().getFirstName())
                .withName(helloAssoPayment.getPayer().getLastName())
                .withFormSlug(helloAssoPayment.getOrder().getFormSlug())
                .withId(helloAssoPayment.getId())
                .build();
        result.put(true, notification);
        when(helloAssoService.isValidPayment(helloAssoPaymentNotification)).thenReturn(result);
        when(paymentRepository.findById(1)).thenReturn(null);

        // WHEN
        paymentService.handleNewPayment(processResult, mapper.writeValueAsString(helloAssoPaymentNotification));

        // THEN
        ArgumentCaptor<Payment> paymentArgumentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository, times(1)).save(paymentArgumentCaptor.capture());
        assertThat(paymentArgumentCaptor.getValue())
                .extracting(Payment::getId, Payment::getDate, Payment::getAmount, Payment::getPayerFirstName, Payment::getPayerLastName)
                .containsExactly(1, "2018-01-01T00:00:00", 2.4f, "prenom", "nom");
    }

    @Test
    void handleNewPayment_fail() throws IOException {
        // GIVEN
        ProcessResult processResult = new ProcessResult();
        HelloAssoPayer helloAssoPayer = new HelloAssoPayer();
        helloAssoPayer.setEmail("email@here");
        helloAssoPayer.setFirstName("prenom");
        helloAssoPayer.setLastName("nom");
        HelloAssoPaymentNotificationBody helloAssoPayment = new HelloAssoPaymentNotificationBody();
        helloAssoPayment.setId(2);
        helloAssoPayment.setAmount(new HelloAssoAmount(240));
        helloAssoPayment.setDate("2018-01-01T00:00:00");
        helloAssoPayment.setPayer(helloAssoPayer);
        HelloAssoPaymentNotification helloAssoPaymentNotification = new HelloAssoPaymentNotification("Payment", mapper.writeValueAsString(helloAssoPayment));
        Map<Boolean, Notification> result = new HashMap<>();
        result.put(false, null);

        when(helloAssoService.isValidPayment(helloAssoPaymentNotification)).thenReturn(result);

        // WHEN
        paymentService.handleNewPayment(processResult, mapper.writeValueAsString(helloAssoPaymentNotification));

        // THEN
        verifyNoInteractions(paymentRepository);
    }

    @Test
    void shouldNotSaveSecondPayment() throws IOException {
        // GIVEN
        ProcessResult processResult = new ProcessResult(StatusPaymentEnum.success, new HashSet<>());
        HelloAssoPayer helloAssoPayer = new HelloAssoPayer();
        helloAssoPayer.setEmail("email@here");
        HelloAssoPaymentNotificationBody helloAssoPayment = new HelloAssoPaymentNotificationBody();
        helloAssoPayment.setId(1);
        helloAssoPayment.setAmount(new HelloAssoAmount(240));
        helloAssoPayment.setDate("2018-01-01T00:00:00");
        helloAssoPayer.setFirstName("prenom");
        helloAssoPayer.setLastName("nom");
        helloAssoPayment.setPayer(helloAssoPayer);
        HelloAssoOrder order = new HelloAssoOrder();
        order.setFormSlug("formSlug");
        helloAssoPayment.setOrder(order);
        HelloAssoPaymentNotification helloAssoPaymentNotification = new HelloAssoPaymentNotification("Payment", mapper.writeValueAsString(helloAssoPayment));

        Map<Boolean, Notification> result = new HashMap<>();
        Notification notification = Notification.NotificationBuilder.aNotification()
                .withAmount(helloAssoPayment.getAmount().getTotal())
                .withDate(helloAssoPayment.getDate())
                .withEmail(helloAssoPayment.getPayer().getEmail())
                .withFirstName(helloAssoPayment.getPayer().getFirstName())
                .withName(helloAssoPayment.getPayer().getLastName())
                .withFormSlug(helloAssoPayment.getOrder().getFormSlug())
                .withId(helloAssoPayment.getId())
                .build();
        result.put(true, notification);
        when(helloAssoService.isValidPayment(helloAssoPaymentNotification)).thenReturn(result);
        when(paymentRepository.findById(1)).thenReturn(new Payment());

        // WHEN
        paymentService.handleNewPayment(processResult, mapper.writeValueAsString(helloAssoPaymentNotification));

        // THEN
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void credit_ok() {
        // GIVEN
        ProcessResult processResult = new ProcessResult();
        ProcessResult processSuccess = new ProcessResult();
        processSuccess.setStatusPayment(StatusPaymentEnum.success);
        Payment payment = new Payment();
        when(paymentRepository.findById(2)).thenReturn(payment);
        when(cyclosService.creditAccount(processResult, 2)).thenReturn(processSuccess);

        // WHEN
        paymentService.creditAccount(processResult, 2);

        // THEN
        verify(cyclosService, times(1)).creditAccount(processResult, 2);
        verifyNoInteractions(mailService);
    }

    @Test
    void credit_ko() {
        // GIVEN
        ProcessResult processResult = new ProcessResult();
        ProcessResult processFail = new ProcessResult();
        processFail.setStatusPayment(StatusPaymentEnum.fail);
        processFail.setErrors(Set.of("cyclos error", "an error occured"));
        Payment payment = new Payment();
        when(paymentRepository.findById(2)).thenReturn(payment);
        when(cyclosService.creditAccount(processResult, 2)).thenReturn(processFail);
        when(dotenv.get("MAIL_RECIPIENT")).thenReturn("mail@mail.com");

        // WHEN
        paymentService.creditAccount(processResult, 2);

        // THEN
        verify(cyclosService, times(1)).creditAccount(processResult, 2);
        verify(mailService).sendEmail(eq("mail@mail.com"), eq("[Hellos] Erreur lors du traitement"), anyString());
    }

    @Test
    void error_message_too_long() {
        // GIVEN
        ProcessResult processResult = new ProcessResult();
        ProcessResult processFail = new ProcessResult();
        processFail.setStatusPayment(StatusPaymentEnum.fail);
        processFail.setErrors(Set.of("cyclos error", "an error occuredaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"));
        Payment payment = new Payment();
        when(paymentRepository.findById(2)).thenReturn(payment);
        when(cyclosService.creditAccount(processResult, 2)).thenReturn(processFail);
        when(dotenv.get("MAIL_RECIPIENT")).thenReturn("mail@mail.com");

        // WHEN
        paymentService.creditAccount(processResult, 2);

        // THEN
        assertThat(processFail.getErrors().toString().length()).isGreaterThan(Payment.ERROR_LENGTH);
        ArgumentCaptor<Payment> argumentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getError().length()).isLessThan(Payment.ERROR_LENGTH);
        verify(cyclosService, times(1)).creditAccount(processResult, 2);
        verify(mailService).sendEmail(eq("mail@mail.com"), eq("[Hellos] Erreur lors du traitement"), anyString());
    }
}