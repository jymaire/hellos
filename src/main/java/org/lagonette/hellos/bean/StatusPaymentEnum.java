package org.lagonette.hellos.bean;

public enum StatusPaymentEnum {
    todo("À faire"),
    tooHigh("Montant trop haut"),
    success("Succès"),
    fail("Echec");

    private final String label;

    StatusPaymentEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
