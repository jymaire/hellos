package org.lagonette.hellos.bean.collectonline;

import com.opencsv.bean.CsvBindByName;
import org.hibernate.type.StringNVarcharType;

public class Payment {

    private String idCreancier;
    private String numeroCompteEmetteur;
    private String libCompteEmetteur;
    private String rumEcheancier;
    private String typeEcheancier;
    private String motifEcheancier;
    private String refDebitteur;

    @CsvBindByName(column = "Référence de bout en bout")
    private String reference;

    private String montantEuros;

    private String dateEcheance;

    @CsvBindByName(column = "Statut échéance")
    private String statutEcheance;

    private String motifImpaye;
    @CsvBindByName(column = "Date d’opération")
    private String dateOperation;
    private String refComptable;

    @CsvBindByName(column = "Code Catégorie échéancier")
    private String codeCategorieEcheancier;

    private String libCatEcheancier;
    private String typeMontant;
    @CsvBindByName(column = "Montant échéancier")
    private String montant;


    @CsvBindByName(column = "Email")
    private String email;

    @CsvBindByName(column = "Prénom OU SIREN")
    private String prenom;

    @CsvBindByName(column = "Nom OU Raisons Sociale")
    private String nom;

    public Payment() {
    }

    public Payment(String idCreancier, String numeroCompteEmetteur, String libCompteEmetteur, String rumEcheancier, String typeEcheancier, String motifEcheancier, String refDebitteur, String reference, String montantEuros, String dateEcheance, String statutEcheance, String motifImpaye, String dateOperation, String refComptable, String codeCategorieEcheancier, String libCatEcheancier, String typeMontant, String montant, String email) {
        this.idCreancier = idCreancier;
        this.numeroCompteEmetteur = numeroCompteEmetteur;
        this.libCompteEmetteur = libCompteEmetteur;
        this.rumEcheancier = rumEcheancier;
        this.typeEcheancier = typeEcheancier;
        this.motifEcheancier = motifEcheancier;
        this.refDebitteur = refDebitteur;
        this.reference = reference;
        this.montantEuros = montantEuros;
        this.dateEcheance = dateEcheance;
        this.statutEcheance = statutEcheance;
        this.motifImpaye = motifImpaye;
        this.dateOperation = dateOperation;
        this.refComptable = refComptable;
        this.codeCategorieEcheancier = codeCategorieEcheancier;
        this.libCatEcheancier = libCatEcheancier;
        this.typeMontant = typeMontant;
        this.montant = montant;
        this.email = email;
    }

    public String getIdCreancier() {
        return idCreancier;
    }

    public void setIdCreancier(String idCreancier) {
        this.idCreancier = idCreancier;
    }

    public String getNumeroCompteEmetteur() {
        return numeroCompteEmetteur;
    }

    public void setNumeroCompteEmetteur(String numeroCompteEmetteur) {
        this.numeroCompteEmetteur = numeroCompteEmetteur;
    }

    public String getLibCompteEmetteur() {
        return libCompteEmetteur;
    }

    public void setLibCompteEmetteur(String libCompteEmetteur) {
        this.libCompteEmetteur = libCompteEmetteur;
    }

    public String getRumEcheancier() {
        return rumEcheancier;
    }

    public void setRumEcheancier(String rumEcheancier) {
        this.rumEcheancier = rumEcheancier;
    }

    public String getTypeEcheancier() {
        return typeEcheancier;
    }

    public void setTypeEcheancier(String typeEcheancier) {
        this.typeEcheancier = typeEcheancier;
    }

    public String getMotifEcheancier() {
        return motifEcheancier;
    }

    public void setMotifEcheancier(String motifEcheancier) {
        this.motifEcheancier = motifEcheancier;
    }

    public String getRefDebitteur() {
        return refDebitteur;
    }

    public void setRefDebitteur(String refDebitteur) {
        this.refDebitteur = refDebitteur;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getMontantEuros() {
        return montantEuros;
    }

    public void setMontantEuros(String montantEuros) {
        this.montantEuros = montantEuros;
    }

    public String getDateEcheance() {
        return dateEcheance;
    }

    public void setDateEcheance(String dateEcheance) {
        this.dateEcheance = dateEcheance;
    }

    public String getStatutEcheance() {
        return statutEcheance;
    }

    public void setStatutEcheance(String statutEcheance) {
        this.statutEcheance = statutEcheance;
    }

    public String getMotifImpaye() {
        return motifImpaye;
    }

    public void setMotifImpaye(String motifImpaye) {
        this.motifImpaye = motifImpaye;
    }

    public String getDateOperation() {
        return dateOperation;
    }

    public void setDateOperation(String dateOperation) {
        this.dateOperation = dateOperation;
    }

    public String getRefComptable() {
        return refComptable;
    }

    public void setRefComptable(String refComptable) {
        this.refComptable = refComptable;
    }

    public String getCodeCategorieEcheancier() {
        return codeCategorieEcheancier;
    }

    public void setCodeCategorieEcheancier(String codeCategorieEcheancier) {
        this.codeCategorieEcheancier = codeCategorieEcheancier;
    }

    public String getLibCatEcheancier() {
        return libCatEcheancier;
    }

    public void setLibCatEcheancier(String libCatEcheancier) {
        this.libCatEcheancier = libCatEcheancier;
    }

    public String getTypeMontant() {
        return typeMontant;
    }

    public void setTypeMontant(String typeMontant) {
        this.typeMontant = typeMontant;
    }

    public String getMontant() {
        return montant;
    }

    public void setMontant(String montant) {
        this.montant = montant;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }


    public static final class PaymentBuilder {
        private String idCreancier;
        private String numeroCompteEmetteur;
        private String libCompteEmetteur;
        private String rumEcheancier;
        private String typeEcheancier;
        private String motifEcheancier;
        private String refDebitteur;
        private String reference;
        private String montantEuros;
        private String dateEcheance;
        private String statutEcheance;
        private String motifImpaye;
        private String dateOperation;
        private String refComptable;
        private String codeCategorieEcheancier;
        private String libCatEcheancier;
        private String typeMontant;
        private String montant;
        private String email;
        private String prenom;
        private String nom;

        private PaymentBuilder() {
        }

        public static PaymentBuilder aPayment() {
            return new PaymentBuilder();
        }

        public PaymentBuilder withIdCreancier(String idCreancier) {
            this.idCreancier = idCreancier;
            return this;
        }

        public PaymentBuilder withNumeroCompteEmetteur(String numeroCompteEmetteur) {
            this.numeroCompteEmetteur = numeroCompteEmetteur;
            return this;
        }

        public PaymentBuilder withLibCompteEmetteur(String libCompteEmetteur) {
            this.libCompteEmetteur = libCompteEmetteur;
            return this;
        }

        public PaymentBuilder withRumEcheancier(String rumEcheancier) {
            this.rumEcheancier = rumEcheancier;
            return this;
        }

        public PaymentBuilder withTypeEcheancier(String typeEcheancier) {
            this.typeEcheancier = typeEcheancier;
            return this;
        }

        public PaymentBuilder withMotifEcheancier(String motifEcheancier) {
            this.motifEcheancier = motifEcheancier;
            return this;
        }

        public PaymentBuilder withRefDebitteur(String refDebitteur) {
            this.refDebitteur = refDebitteur;
            return this;
        }

        public PaymentBuilder withReference(String reference) {
            this.reference = reference;
            return this;
        }

        public PaymentBuilder withMontantEuros(String montantEuros) {
            this.montantEuros = montantEuros;
            return this;
        }

        public PaymentBuilder withDateEcheance(String dateEcheance) {
            this.dateEcheance = dateEcheance;
            return this;
        }

        public PaymentBuilder withStatutEcheance(String statutEcheance) {
            this.statutEcheance = statutEcheance;
            return this;
        }

        public PaymentBuilder withMotifImpaye(String motifImpaye) {
            this.motifImpaye = motifImpaye;
            return this;
        }

        public PaymentBuilder withDateOperation(String dateOperation) {
            this.dateOperation = dateOperation;
            return this;
        }

        public PaymentBuilder withRefComptable(String refComptable) {
            this.refComptable = refComptable;
            return this;
        }

        public PaymentBuilder withCodeCategorieEcheancier(String codeCategorieEcheancier) {
            this.codeCategorieEcheancier = codeCategorieEcheancier;
            return this;
        }

        public PaymentBuilder withLibCatEcheancier(String libCatEcheancier) {
            this.libCatEcheancier = libCatEcheancier;
            return this;
        }

        public PaymentBuilder withTypeMontant(String typeMontant) {
            this.typeMontant = typeMontant;
            return this;
        }

        public PaymentBuilder withMontant(String montant) {
            this.montant = montant;
            return this;
        }

        public PaymentBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public PaymentBuilder withPrenom(String prenom) {
            this.prenom = prenom;
            return this;
        }

        public PaymentBuilder withNom(String nom) {
            this.nom = nom;
            return this;
        }

        public Payment build() {
            Payment payment = new Payment();
            payment.setIdCreancier(idCreancier);
            payment.setNumeroCompteEmetteur(numeroCompteEmetteur);
            payment.setLibCompteEmetteur(libCompteEmetteur);
            payment.setRumEcheancier(rumEcheancier);
            payment.setTypeEcheancier(typeEcheancier);
            payment.setMotifEcheancier(motifEcheancier);
            payment.setRefDebitteur(refDebitteur);
            payment.setReference(reference);
            payment.setMontantEuros(montantEuros);
            payment.setDateEcheance(dateEcheance);
            payment.setStatutEcheance(statutEcheance);
            payment.setMotifImpaye(motifImpaye);
            payment.setDateOperation(dateOperation);
            payment.setRefComptable(refComptable);
            payment.setCodeCategorieEcheancier(codeCategorieEcheancier);
            payment.setLibCatEcheancier(libCatEcheancier);
            payment.setTypeMontant(typeMontant);
            payment.setMontant(montant);
            payment.setEmail(email);
            payment.setPrenom(prenom);
            payment.setNom(nom);
            return payment;
        }
    }
}
