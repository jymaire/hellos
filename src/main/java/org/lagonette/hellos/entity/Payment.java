package org.lagonette.hellos.entity;

import org.lagonette.hellos.bean.StatusPaymentEnum;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Objects;

import static org.lagonette.hellos.bean.StatusPaymentEnum.todo;

@Entity
public class Payment {
    public static final int ERROR_LENGTH = 700;
    @Id
    private int id;
    private String date;
    // in euro
    private float amount;
    private String payerFirstName;
    private String payerLastName;
    // technical field, to handle purge process
    private LocalDateTime insertionDate;
    private StatusPaymentEnum status;
    @Column(length = ERROR_LENGTH)
    private String error;
    private String email;

    public Payment() {
    }

    public Payment(int id, String date, float amount, String payerFirstName, String payerLastName, String email) {
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.payerFirstName = payerFirstName;
        this.payerLastName = payerLastName;
        this.insertionDate = LocalDateTime.now();
        this.status = todo;
        this.error = "";
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public float getAmount() {
        return amount;
    }

    public String getPayerFirstName() {
        return payerFirstName;
    }

    public String getPayerLastName() {
        return payerLastName;
    }

    public StatusPaymentEnum getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getInsertionDate() {
        return insertionDate;
    }

    public void setInsertionDate(LocalDateTime insertionDate) {
        this.insertionDate = insertionDate;
    }

    public void setStatus(StatusPaymentEnum status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return id == payment.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id='" + id + '\'' +
                ", date='" + date + '\'' +
                ", amount=" + amount +
                '}';
    }


    public static final class PaymentBuilder {
        private int id;
        private String date;
        private float amount;
        private String payerFirstName;
        private String payerLastName;
        // technical field, to handle purge process
        private LocalDateTime insertionDate;
        private String email;

        private PaymentBuilder() {
        }

        public static PaymentBuilder aPayment() {
            return new PaymentBuilder();
        }

        public PaymentBuilder withId(int id) {
            this.id = id;
            return this;
        }

        public PaymentBuilder withDate(String date) {
            this.date = date;
            return this;
        }

        public PaymentBuilder withAmount(float amount) {
            this.amount = amount;
            return this;
        }

        public PaymentBuilder withPayerFirstName(String payerFirstName) {
            this.payerFirstName = payerFirstName;
            return this;
        }

        public PaymentBuilder withPayerLastName(String payerLastName) {
            this.payerLastName = payerLastName;
            return this;
        }

        public PaymentBuilder withInsertionDate(LocalDateTime insertionDate) {
            this.insertionDate = insertionDate;
            return this;
        }

        public PaymentBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public Payment build() {
            Payment payment = new Payment(id, date, amount, payerFirstName, payerLastName, email);
            payment.insertionDate = this.insertionDate;
            return payment;
        }
    }
}
