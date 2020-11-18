package org.lagonette.hellos.bean.cyclos;

public class CyclosTransaction {

    private String description;

    public CyclosTransaction(String description) {
        this.description = description;
    }

    public CyclosTransaction() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "CyclosTransaction{" +
                "description='" + description + '\'' +
                '}';
    }
}
