package org.lagonette.hellos.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PurgeDatabaseService purgeDatabaseService;

    @Test
    void purgeDatabase() {
        // GIVEN
        when(paymentRepository.findIdsByInsertionDateBefore(Mockito.any())).thenReturn(List.of(1, 2));

        // WHEN
        purgeDatabaseService.purgeDatabase();

        // THEN
        verify(paymentRepository).deleteById(List.of(1, 2));
    }

    @Test
    void purgeDatabase_noRecord() {
        // GIVEN
        when(paymentRepository.findIdsByInsertionDateBefore(Mockito.any())).thenReturn(new ArrayList<>());

        // WHEN
        purgeDatabaseService.purgeDatabase();

        // THEN
        verify(paymentRepository).findIdsByInsertionDateBefore(Mockito.any());
        verifyNoMoreInteractions(paymentRepository);
    }
}