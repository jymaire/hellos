package org.lagonette.hellos.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lagonette.hellos.bean.ProcessResult;
import org.lagonette.hellos.entity.Payment;
import org.lagonette.hellos.repository.PaymentRepository;
import org.lagonette.hellos.service.CyclosService;
import org.lagonette.hellos.service.HelloAssoService;
import org.lagonette.hellos.service.PaymentService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.ui.ConcurrentModel;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private CyclosService cyclosService;

    @Mock
    private HelloAssoService helloAssoService;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    @Test
    void shouldFindAllPayment() {
        // WHEN
        paymentController.payments(new ConcurrentModel());

        // THEN
        verify(paymentRepository).findAll();
        verifyNoMoreInteractions(paymentRepository);
    }

    @Test
    void shouldFindAllPaymentAndOrderThem() {
        // GIVEN
        final ConcurrentModel model = new ConcurrentModel();
        when(paymentRepository.findAll()).thenReturn(List.of(Payment.PaymentBuilder.aPayment().withId(2).build(),
                Payment.PaymentBuilder.aPayment().withId(1).build(),
                Payment.PaymentBuilder.aPayment().withId(3).build()
        ));
        // WHEN
        paymentController.payments(model);

        // THEN
        verify(paymentRepository).findAll();
        verifyNoMoreInteractions(paymentRepository);
        assertThat(((List<Payment>) model.get("payments")).get(0).getId()).isEqualTo(3);
        assertThat(((List<Payment>) model.get("payments")).get(1).getId()).isEqualTo(2);
        assertThat(((List<Payment>) model.get("payments")).get(2).getId()).isEqualTo(1);
    }


    @Test
    void shouldCreditAccount() {
        // WHEN
        paymentController.credit(2, new ConcurrentModel());
        ProcessResult processResult = new ProcessResult();

        // THEN
        verify(paymentService).creditAccount(processResult, 2);
    }

    @Test
    void shouldDeleteLine() {
        // WHEN
        paymentController.delete(1, new ConcurrentModel());

        // THEN
        verify(paymentRepository).deleteById(1);
        verifyNoMoreInteractions(paymentRepository);
    }
}