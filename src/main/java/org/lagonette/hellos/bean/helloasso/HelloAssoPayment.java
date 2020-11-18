package org.lagonette.hellos.bean.helloasso;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.lagonette.hellos.bean.PaymentStateEnum;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HelloAssoPayment {

    private HelloAssoPayer payer;
    private HelloAssoOrder order;
    private String id;
    private String date;
    // amount is in cents
    private int amount;
    private PaymentStateEnum state;
    private String url;
    private String payer_first_name;
    private String payer_last_name;
    private String actionId;

    public HelloAssoPayment() {
    }

    public HelloAssoPayer getPayer() {
        return payer;
    }

    public void setPayer(HelloAssoPayer payer) {
        this.payer = payer;
    }

    public HelloAssoOrder getOrder() {
        return order;
    }

    public void setOrder(HelloAssoOrder order) {
        this.order = order;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPayer_first_name() {
        return payer_first_name;
    }

    public void setPayer_first_name(String payer_first_name) {
        this.payer_first_name = payer_first_name;
    }

    public String getPayer_last_name() {
        return payer_last_name;
    }

    public void setPayer_last_name(String payer_last_name) {
        this.payer_last_name = payer_last_name;
    }

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HelloAssoPayment that = (HelloAssoPayment) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "HelloAssoPayment{" +
                "id='" + id + '\'' +
                ", date='" + date + '\'' +
                ", amount=" + amount +
                '}';
    }
}
