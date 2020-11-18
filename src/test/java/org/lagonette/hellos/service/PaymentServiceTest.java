package org.lagonette.hellos.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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
        //  when(cyclosService.creditAccount(processResult, "id1")).thenReturn(processResult);

        // WHEN
        paymentService.handleNewPayment(processResult, mapper.writeValueAsString(helloAssoPaymentNotification));

        // THEN
        ArgumentCaptor<Payment> paymentArgumentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository, times(1)).save(paymentArgumentCaptor.capture());
//        verify(paymentRepository, times(2)).save(paymentArgumentCaptor.capture());
        assertThat(paymentArgumentCaptor.getValue())
                .extracting(Payment::getId, Payment::getDate, Payment::getAmount, Payment::getPayerFirstName, Payment::getPayerLastName)
                .containsExactly(1, "2018-01-01T00:00:00", 2.4f, "prenom", "nom");
        //    verify(cyclosService).creditAccount(processResult, "id1");
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
}