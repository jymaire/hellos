package org.lagonette.hellos.bean.helloasso.notification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HelloAssoAmount {
    // in cent
    private int total;

    public HelloAssoAmount() {
    }

    public HelloAssoAmount(int total) {
        this.total = total;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "HelloAssoAmount{" +
                "total=" + total +
                '}';
    }
}
