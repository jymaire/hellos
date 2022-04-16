package org.lagonette.hellos.service;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeEach;
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

    @Mock
    private Dotenv dotenv;


    @InjectMocks
    private PurgeDatabaseService purgeDatabaseService;

    @BeforeEach
    void before(){
        when(dotenv.get("DATABASE_RETENTION_PAYMENT_DAY")).thenReturn("7");
    }

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