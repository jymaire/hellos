package org.lagonette.hellos.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lagonette.hellos.bean.StatusPaymentEnum;
import org.lagonette.hellos.entity.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    void shouldFindPaymentBeforeDate() {
        // GIVEN
        paymentRepository.save(Payment.PaymentBuilder.aPayment().withId(1).withInsertionDate(LocalDateTime.parse("2020-02-03T00:10:22")).build());
        paymentRepository.save(Payment.PaymentBuilder.aPayment().withId(2).withInsertionDate(LocalDateTime.parse("2020-02-05T00:10:22")).build());

        // WHEN
        List<Integer> withDateOlderThan = paymentRepository.findIdsByInsertionDateBefore(LocalDateTime.parse("2020-02-04T00:10:00"));
        // THEN
        assertThat(withDateOlderThan).isNotEmpty();
        assertThat(withDateOlderThan.size()).isEqualTo(1);
        assertThat(withDateOlderThan.get(0)).isEqualTo(1);
    }

    @Test
    void shouldFindPaymentByStatus() {
        // GIVEN
        Payment payment1 = Payment.PaymentBuilder.aPayment().withId(1).withInsertionDate(LocalDateTime.parse("2020-02-03T00:10:22")).build();
        payment1.setStatus(StatusPaymentEnum.success);
        paymentRepository.save(payment1);
        Payment payment2 = Payment.PaymentBuilder.aPayment().withId(2).withInsertionDate(LocalDateTime.parse("2020-02-05T00:10:22")).build();
        payment2.setStatus(StatusPaymentEnum.todo);
        paymentRepository.save(payment2);

        // WHEN
        List<Payment> withDateOlderThan = paymentRepository.getByStatus(StatusPaymentEnum.todo);
        // THEN
        assertThat(withDateOlderThan).isNotEmpty();
        assertThat(withDateOlderThan.size()).isEqualTo(1);
        assertThat(withDateOlderThan.get(0).getId()).isEqualTo(2);
    }

    @Test
    void shouldDeleteSomeRecords() {
        // GIVEN
        paymentRepository.save(Payment.PaymentBuilder.aPayment().withId(1).build());
        paymentRepository.save(Payment.PaymentBuilder.aPayment().withId(2).build());
        paymentRepository.save(Payment.PaymentBuilder.aPayment().withId(3).build());
        paymentRepository.save(Payment.PaymentBuilder.aPayment().withId(4).build());

        // WHEN
        paymentRepository.deleteById(Arrays.asList(1, 2));
        List<Payment> withDateOlderThan = paymentRepository.findAll();

        // THEN
        assertThat(withDateOlderThan).isNotEmpty();
        assertThat(withDateOlderThan.size()).isEqualTo(2);
        assertThat(withDateOlderThan).extracting(Payment::getId)
                .containsExactly(3, 4);
    }

    @Test
    void shouldDeleteOneRecords() {
        // GIVEN
        paymentRepository.save(Payment.PaymentBuilder.aPayment().withId(1).build());
        paymentRepository.save(Payment.PaymentBuilder.aPayment().withId(2).build());
        paymentRepository.save(Payment.PaymentBuilder.aPayment().withId(3).build());
        paymentRepository.save(Payment.PaymentBuilder.aPayment().withId(4).build());

        // WHEN
        paymentRepository.deleteById(1);
        List<Payment> withDateOlderThan = paymentRepository.findAll();

        // THEN
        assertThat(withDateOlderThan).isNotEmpty();
        assertThat(withDateOlderThan.size()).isEqualTo(3);
        assertThat(withDateOlderThan).extracting(Payment::getId)
                .containsExactly(2, 3, 4);
    }

    @Test
    void shouldFindPaymentInOrder() {
        // GIVEN
        paymentRepository.save(Payment.PaymentBuilder.aPayment().withId(1).withDate("2020-02-03T00:10:22").build());
        paymentRepository.save(Payment.PaymentBuilder.aPayment().withId(2).withDate("2020-02-05T00:10:22").build());

        // WHEN
        List<Payment> withDateOlderThan = paymentRepository.findAll();
        // THEN
        assertThat(withDateOlderThan).isNotEmpty();
        assertThat(withDateOlderThan.size()).isEqualTo(2);
        assertThat(withDateOlderThan.get(0).getId()).isEqualTo(2);
    }

    @Test
    void shouldFindAllId() {
        // GIVEN
        paymentRepository.save(Payment.PaymentBuilder.aPayment().withId(1).build());
        paymentRepository.save(Payment.PaymentBuilder.aPayment().withId(2).build());
        paymentRepository.save(Payment.PaymentBuilder.aPayment().withId(3).build());

        // WHEN
        List<Integer> ids = paymentRepository.findAllIds();
        // THEN
        assertThat(ids).isNotEmpty();
        assertThat(ids.size()).isEqualTo(3);
        assertThat(ids).containsAnyOf(1, 2, 3);
    }

}