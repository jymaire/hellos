package org.lagonette.hellos.bean.helloasso.notification;

import org.lagonette.hellos.bean.PaymentStateEnum;

public class HelloAssoPayment {

    private String id;
    // amount is in cents
    private int amount;
    private PaymentStateEnum state;
    private String paymentReceiptUrl;

    public HelloAssoPayment() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public PaymentStateEnum getState() {
        return state;
    }

    public void setState(PaymentStateEnum state) {
        this.state = state;
    }

    public String getPaymentReceiptUrl() {
        return paymentReceiptUrl;
    }

    public void setPaymentReceiptUrl(String paymentReceiptUrl) {
        this.paymentReceiptUrl = paymentReceiptUrl;
    }

    @Override
    public String toString() {
        return "HelloAssoPayment{" +
                "id='" + id + '\'' +
                ", amount=" + amount +
                ", state=" + state +
                ", paymentReceiptUrl='" + paymentReceiptUrl + '\'' +
                '}';
    }
}
