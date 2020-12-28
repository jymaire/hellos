package org.lagonette.hellos.bean;

public class AdminConfig {

    private boolean paymentCyclosEnabled;
    private boolean paymentAutomaticEnabled;
    private String mailRecipient;

    public AdminConfig(boolean paymentCyclosEnabled, boolean paymentAutomaticEnabled, String mailRecipient) {
        this.paymentCyclosEnabled = paymentCyclosEnabled;
        this.paymentAutomaticEnabled = paymentAutomaticEnabled;
        this.mailRecipient = mailRecipient;
    }

    public boolean isPaymentCyclosEnabled() {
        return paymentCyclosEnabled;
    }

    public void setPaymentCyclosEnabled(boolean paymentCyclosEnabled) {
        this.paymentCyclosEnabled = paymentCyclosEnabled;
    }

    public boolean isPaymentAutomaticEnabled() {
        return paymentAutomaticEnabled;
    }

    public void setPaymentAutomaticEnabled(boolean paymentAutomaticEnabled) {
        this.paymentAutomaticEnabled = paymentAutomaticEnabled;
    }

    public String getMailRecipient() {
        return mailRecipient;
    }

    public void setMailRecipient(String mailRecipient) {
        this.mailRecipient = mailRecipient;
    }
}
