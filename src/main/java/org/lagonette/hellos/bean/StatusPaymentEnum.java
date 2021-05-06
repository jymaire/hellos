package org.lagonette.hellos.bean;

public enum StatusPaymentEnum {
    todo("À faire"),
    tooHigh("Montant trop haut"),
    tooLate("En retard"),
    previewOK("Reactiver les paiements pour effectuer le paiement"),
    success("Succès"),
    successAuto("Succès (automatique)"),
    fail("Echec"),
    waiting("En attente");

    private final String label;

    StatusPaymentEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
