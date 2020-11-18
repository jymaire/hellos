package org.lagonette.hellos.bean.helloasso.notification;

import java.util.Objects;

public class HelloAssoPaymentNotification {

    private String eventType;
    private Object data;

    public HelloAssoPaymentNotification() {
    }

    public HelloAssoPaymentNotification(String eventType, String data) {
        this.eventType = eventType;
        this.data = data;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "HelloAssoPaymentNotification{" +
                "eventType='" + eventType + '\'' +
                ", data=" + data +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HelloAssoPaymentNotification that = (HelloAssoPaymentNotification) o;
        return Objects.equals(eventType, that.eventType) &&
                Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventType, data);
    }
}
