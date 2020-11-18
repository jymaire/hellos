package org.lagonette.hellos.bean.cyclos;

public class CyclosPerformPaymentResponse {

    private String amount;
    private String subject;
    private String description;

    public CyclosPerformPaymentResponse() {
    }

    public CyclosPerformPaymentResponse(String amount, String subject, String description) {
        this.amount = amount;
        this.subject = subject;
        this.description = description;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public String toString() {
        return "CyclosPerformPayment{" +
                "amount='" + amount + '\'' +
                ", subject='" + subject + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
