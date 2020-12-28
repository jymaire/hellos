package org.lagonette.hellos.repository;

import com.sun.istack.NotNull;
import org.lagonette.hellos.entity.Payment;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRepository extends CrudRepository<Payment, Long> {

    @Query("SELECT p FROM Payment p ORDER BY p.date DESC")
    List<Payment> findAll();

    Payment findById(int id);

    Payment save(@NotNull Payment payment);

    @Query("SELECT id " +
            "FROM Payment p " +
            "WHERE p.insertionDate < ?1")
    List<Integer> findIdsByInsertionDateBefore(@NotNull LocalDateTime date);

    @Modifying
    @Query("DELETE FROM Payment p WHERE p.id IN ?1")
    void deleteById(List<Integer> paymentIds);

    @Modifying
    @Query("DELETE FROM Payment p WHERE p.id = ?1")
    void deleteById(int paymentIds);
}
