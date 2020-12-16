package org.lagonette.hellos.bean.helloasso.notification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.lagonette.hellos.bean.helloasso.HelloAssoOrder;
import org.lagonette.hellos.bean.helloasso.HelloAssoPayer;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HelloAssoPaymentNotificationBody {

    private HelloAssoOrder order;
    private HelloAssoPayer payer;
    private HelloAssoAmount amount;
    private int id;
    private String date;
    private String state;

    public HelloAssoPaymentNotificationBody() {
    }

    public HelloAssoOrder getOrder() {
        return order;
    }

    public void setOrder(HelloAssoOrder order) {
        this.order = order;
    }

    public HelloAssoPayer getPayer() {
        return payer;
    }

    public void setPayer(HelloAssoPayer payer) {
        this.payer = payer;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "HelloAssoPaymentNotificationBody{" +
                "order=" + order +
                ", amount=" + amount +
                ", id='" + id + '\'' +
                ", date='" + date + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}
