package org.lagonette.hellos.bean.helloasso.notification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.lagonette.hellos.bean.helloasso.HelloAssoOrderItem;
import org.lagonette.hellos.bean.helloasso.HelloAssoPayer;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HelloAssoOrderNotificationBody {

    private List<HelloAssoOrderItem> items;
    private HelloAssoPayer payer;
    private HelloAssoAmount amount;
    private int id;
    private String date;

    public HelloAssoOrderNotificationBody() {
    }

    public List<HelloAssoOrderItem> getItems() {
        return items;
    }

    public void setItems(List<HelloAssoOrderItem> items) {
        this.items = items;
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

    @Override
    public String toString() {
        return "HelloAssoPaymentNotificationBody{" +
                "items=" + items +
                ", amount=" + amount +
                ", id='" + id + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
