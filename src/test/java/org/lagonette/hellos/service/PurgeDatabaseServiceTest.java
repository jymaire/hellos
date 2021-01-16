package org.lagonette.hellos.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lagonette.hellos.repository.EmailLinkRepository;
import org.lagonette.hellos.repository.PaymentRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class PurgeDatabaseServiceTest {

    @Mock
    private EmailLinkRepository emailLinkRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PurgeDatabaseService purgeDatabaseService;

    @Test
    void purgeDatabase() {
        // GIVEN
        when(emailLinkRepository.findIdsByInsertionDateBefore(Mockito.any())).thenReturn(List.of("email1@email.fr", "email2@email.fr"));
        when(paymentRepository.findIdsByInsertionDateBefore(Mockito.any())).thenReturn(List.of(1, 2));

        // WHEN
        purgeDatabaseService.purgeDatabase();

        // THEN
        verify(emailLinkRepository).deleteById(List.of("email1@email.fr", "email2@email.fr"));
        verify(paymentRepository).deleteById(List.of(1, 2));
    }

    @Test
    void purgeDatabase_noRecord() {
        // GIVEN
        when(emailLinkRepository.findIdsByInsertionDateBefore(Mockito.any())).thenReturn(new ArrayList<>());
        when(paymentRepository.findIdsByInsertionDateBefore(Mockito.any())).thenReturn(new ArrayList<>());

        // WHEN
        purgeDatabaseService.purgeDatabase();

        // THEN
        verify(emailLinkRepository).findIdsByInsertionDateBefore(Mockito.any());
        verify(paymentRepository).findIdsByInsertionDateBefore(Mockito.any());

        verifyNoMoreInteractions(emailLinkRepository);
        verifyNoMoreInteractions(paymentRepository);
    }
}