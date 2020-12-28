package org.lagonette.hellos.bean;

public enum StatusPaymentEnum {
    todo("À faire"),
    tooHigh("Montant trop haut"),
    previewOK("Reactiver les paiements pour effectuer le paiement"),
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
