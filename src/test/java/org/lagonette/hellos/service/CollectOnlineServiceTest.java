package org.lagonette.hellos.service;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lagonette.hellos.bean.collectonline.Payment;
import org.lagonette.hellos.repository.PaymentRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CollectOnlineServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private Dotenv dotenv;

    @Captor
    ArgumentCaptor<ArrayList<org.lagonette.hellos.entity.Payment>> argument;

    @InjectMocks
    private CollectOnlineService collectOnlineService;

    @Test
    void importPaymentsFromCSV() {
        // GIVEN
        List<Payment> list = new ArrayList<>();
        when(dotenv.get("COL_CHANGE_KEYWORD")).thenReturn("Cha");
        // No exe, no cha -> ko
        list.add(Payment.PaymentBuilder.aPayment()
                .withReference("110")
                .withDateOperation("15/02/21")
                .withMontant("4,5")
                .withPrenom("prénom")
                .withNom("Nom")
                .withEmail("test@test.fr")
                .build());
        // exe, cha -> ok
        list.add(Payment.PaymentBuilder.aPayment()
                .withReference("111")
                .withDateOperation("15/02/21")
                .withStatutEcheance("Exécutée")
                .withCodeCategorieEcheancier("Cha")
                .withMontant("4,5")
                .withPrenom("prénom")
                .withNom("Nom")
                .withEmail("test@test.fr")
                .build());
        // no exe, cha -> ko
        list.add(Payment.PaymentBuilder.aPayment()
                .withReference("112")
                .withDateOperation("15/02/21")
                .withStatutEcheance("aa")
                .withCodeCategorieEcheancier("Cha")
                .withMontant("4,5")
                .withPrenom("prénom")
                .withNom("Nom")
                .withEmail("test@test.fr")
                .build());
        // exe, no cha -> ko
        list.add(Payment.PaymentBuilder.aPayment()
                .withReference("113")
                .withDateOperation("15/02/21")
                .withStatutEcheance("Exécutée")
                .withCodeCategorieEcheancier("autre")
                .withMontant("4,5")
                .withPrenom("prénom")
                .withNom("Nom")
                .withEmail("test@test.fr")
                .build());

        // WHEN
        collectOnlineService.importPaymentsFromCSV(list);

        // THEN
        verify(paymentRepository).saveAll(argument.capture());
        List<org.lagonette.hellos.entity.Payment> saved = argument.getValue();
        assertThat(saved).isNotEmpty();
        assertThat(saved.size()).isEqualTo(1);
        assertThat(saved.get(0))
                .extracting(org.lagonette.hellos.entity.Payment::getId, org.lagonette.hellos.entity.Payment::getDate, org.lagonette.hellos.entity.Payment::getAmount, org.lagonette.hellos.entity.Payment::getPayerFirstName, org.lagonette.hellos.entity.Payment::getPayerLastName, org.lagonette.hellos.entity.Payment::getEmail)
                .containsExactly(111, "15/02/21", 4.5f, "prénom", "Nom", "test@test.fr");
    }


    @Test
    void importPaymentsFromCSV_withoutKeyword() {
        // GIVEN
        List<Payment> list = new ArrayList<>();

        // No exe, no cha -> ok because we don't filter with keyword here (no Dotenv mock call in this test)
        list.add(Payment.PaymentBuilder.aPayment()
                .withReference("110")
                .withDateOperation("15/02/21")
                .withMontant("4,5")
                .withPrenom("prénom")
                .withNom("Nom")
                .withEmail("test@test.fr")
                .build());
        // exe, cha -> ok
        list.add(Payment.PaymentBuilder.aPayment()
                .withReference("111")
                .withDateOperation("15/02/21")
                .withStatutEcheance("Exécutée")
                .withCodeCategorieEcheancier("Cha")
                .withMontant("4,5")
                .withPrenom("prénom")
                .withNom("Nom")
                .withEmail("test@test.fr")
                .build());
        // no exe, cha -> ko
        list.add(Payment.PaymentBuilder.aPayment()
                .withReference("112")
                .withDateOperation("15/02/21")
                .withStatutEcheance("aa")
                .withCodeCategorieEcheancier("Cha")
                .withMontant("4,5")
                .withPrenom("prénom")
                .withNom("Nom")
                .withEmail("test@test.fr")
                .build());
        // exe, no cha -> ko
        list.add(Payment.PaymentBuilder.aPayment()
                .withReference("113")
                .withDateOperation("15/02/21")
                .withStatutEcheance("Exécutée")
                .withCodeCategorieEcheancier("autre")
                .withMontant("4,5")
                .withPrenom("prénom")
                .withNom("Nom")
                .withEmail("test@test.fr")
                .build());

        // WHEN
        collectOnlineService.importPaymentsFromCSV(list);

        // THEN
        verify(paymentRepository).saveAll(argument.capture());
        List<org.lagonette.hellos.entity.Payment> saved = argument.getValue();
        assertThat(saved).isNotEmpty();
        assertThat(saved.size()).isEqualTo(2);
    }
}