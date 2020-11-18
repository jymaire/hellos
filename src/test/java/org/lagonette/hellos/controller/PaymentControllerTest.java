package org.lagonette.hellos.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lagonette.hellos.bean.ProcessResult;
import org.lagonette.hellos.repository.PaymentRepository;
import org.lagonette.hellos.service.CyclosService;
import org.lagonette.hellos.service.HelloAssoService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.ui.ConcurrentModel;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(SpringExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private CyclosService cyclosService;

    @Mock
    private HelloAssoService helloAssoService;

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
    void shouldCreditAccount() {
        // WHEN
        paymentController.creditAccount(2, new ConcurrentModel());
        ProcessResult processResult = new ProcessResult();

        // THEN
        verify(cyclosService).creditAccount(processResult, 2);
        verify(paymentRepository).findAll();
        verifyNoMoreInteractions(paymentRepository);
        verifyNoMoreInteractions(cyclosService);
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