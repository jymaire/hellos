package org.lagonette.hellos.bean;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ProcessResult {

    private StatusPaymentEnum statusPayment;
    private Set<String> errors = new HashSet<>();

    public ProcessResult() {
    }

    public ProcessResult(StatusPaymentEnum statusPayment, Set<String> errors) {
        this.statusPayment = statusPayment;
        this.errors = errors;
    }

    public StatusPaymentEnum getStatusPayment() {
        return statusPayment;
    }

    public void setStatusPayment(StatusPaymentEnum statusPayment) {
        this.statusPayment = statusPayment;
    }

    public Set<String> getErrors() {
        return errors;
    }

    public void setErrors(Set<String> errors) {
        this.errors = errors;
    }

    @Override
    public String toString() {
        String result = "ProcessResult : \n Statut du paiement : " + statusPayment + "\n";
        StringBuilder errBuilder = new StringBuilder();
        errBuilder.append("Liste des erreurs : \n");
        for (String err : errors) {
            errBuilder.append(err);
            errBuilder.append("\n");
        }
        return result + errBuilder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessResult that = (ProcessResult) o;
        return statusPayment == that.statusPayment &&
                Objects.equals(errors, that.errors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statusPayment, errors);
    }
}
