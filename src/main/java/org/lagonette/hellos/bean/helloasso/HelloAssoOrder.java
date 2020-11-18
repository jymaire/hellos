package org.lagonette.hellos.bean.helloasso;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.lagonette.hellos.bean.helloasso.notification.HelloAssoAmount;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HelloAssoOrder {

    private HelloAssoPayer payer;
    private List<HelloAssoPayment> payments;
    private HelloAssoAmount amount;
    private int id;
    private String date;
    private String formSlug;
    private String formType;
    private String organizationSlug;

    public HelloAssoOrder() {
    }

    public HelloAssoPayer getPayer() {
        return payer;
    }

    public void setPayer(HelloAssoPayer payer) {
        this.payer = payer;
    }

    public List<HelloAssoPayment> getPayments() {
        return payments;
    }

    public void setPayments(List<HelloAssoPayment> payments) {
        this.payments = payments;
    }

    public HelloAssoAmount getAmount() {
        return amount;
    }

    public void setAmount(HelloAssoAmount amount) {
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFormSlug() {
        return formSlug;
    }

    public void setFormSlug(String formSlug) {
        this.formSlug = formSlug;
    }

    public String getFormType() {
        return formType;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

    public String getOrganizationSlug() {
        return organizationSlug;
    }

    public void setOrganizationSlug(String organizationSlug) {
        this.organizationSlug = organizationSlug;
    }

    @Override
    public String toString() {
        return "HelloAssoOrder{" +
                "payer=" + payer +
                ", payments=" + payments +
                ", amount=" + amount +
                ", id='" + id + '\'' +
                ", date='" + date + '\'' +
                ", formSlug='" + formSlug + '\'' +
                ", formType='" + formType + '\'' +
                ", organizationSlug='" + organizationSlug + '\'' +
                '}';
    }
}
