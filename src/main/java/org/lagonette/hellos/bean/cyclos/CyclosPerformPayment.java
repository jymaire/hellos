package org.lagonette.hellos.bean.cyclos;

public class CyclosPerformPayment {

    private String amount;
    private String subject;
    private String description;
    private String type;

    public CyclosPerformPayment() {
    }

    public CyclosPerformPayment(String amount, String subject, String description, String type) {
        this.amount = amount;
        this.subject = subject;
        this.description = description;
        this.type = type;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "CyclosPerformPayment{" +
                "amount='" + amount + '\'' +
                ", subject='" + subject + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
