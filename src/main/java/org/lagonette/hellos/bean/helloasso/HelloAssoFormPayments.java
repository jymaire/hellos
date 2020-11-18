package org.lagonette.hellos.bean.helloasso;

import java.util.List;

public class HelloAssoFormPayments {

    private List<HelloAssoPayment> data;

    public HelloAssoFormPayments() {
    }

    public HelloAssoFormPayments(List<HelloAssoPayment> data) {
        this.data = data;
    }

    public List<HelloAssoPayment> getData() {
        return data;
    }

    public void setData(List<HelloAssoPayment> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "HelloAssoFormPayments{" +
                "data=" + data +
                '}';
    }
}
