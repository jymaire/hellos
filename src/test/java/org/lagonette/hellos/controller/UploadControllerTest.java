package org.lagonette.hellos.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
class UploadControllerTest {

    @InjectMocks
    private UploadController uploadController;

    @Test
    void fixCsvFile() {
        // GIVEN
        List<String> lines = new ArrayList<>(2);
        lines.add("Identifiant créancier;Numéro du compte émetteur;Libellé compte émetteur;RUM échéancier;Type d'échéancier;Motif échéancier;Référence débiteur;Référence de bout en bout;Montant en €;Date d’échéance;Statut échéance;Motif impayé;Date d’opération;Référence comptable;Code Catégorie échéancier;Libellé Catégorie échéancier;Type de montant;Montant échéancier;Détail du tarif;Périodicité;Date de fin;Statut échéancier;Date de signature;Date de création échéancier;Date de suppression échéancier;Type débiteur (PP ou PM);Civilité;Nom OU Raisons Sociale;Prénom OU SIREN;N° TVA Intracommunautaire;Code Catégorie;Libellé Catégorie;Commentaire catégorie;BIC;IBAN;Banque/Guichet;Numéro d’adresse;Type Numéro;Nom de voie;Complément d’adresse;Code postal;Ville;Pays;Email;Numéro de tel fixe;Numéro de tel portable;Date de naissance;Info complémentaires;Zone personnalisable 1;Zone personnalisable 2;Zone personnalisable 3;Zone personnalisable 4;Zone personnalisable 5;Zone personnalisable 6;Statut;Date de création;Date de suppression");
        lines.add("43243;\"=\"\"FR242342\"\"\";\"=\"\"MONNAIE LOCALE CITOYE\"\"\";\"=\"\"COL\"\"\";Répétitif;\"=\"\"CAM pour 20 euros\"\"\";\"=\"\"U0000\"\"\";\"=\"\"1222\"\"\";20,00;09/05/22;Exécutée;;10/05/22;;ADHER;\"=\"\"Adherent-e\"\"\";Fixe;20,00;;Mensuel;;Actif;22/11/19;19/01/20;;personne physique;Mme;\"=\"\"NOM\"\"\";\"=\"\"Prenom\"\"\";;ADH;\"=\"\"ADHERENT-E\"\"\";;PSSTFRO;FR823;\"=\"\"LA BANQUE      \"\"\";\"=\"\"1\"\"\";;\"=\"\"Chemin\"\"\";;69370;Saint Didier au Mont d'Or;France;\"=\"\"mail@mail.com\"\"\";;\"=\"\"615666175\"\"\";01/01/66;;;;;;;;Actif;22/11/19;;");

        // WHEN
        final String result = uploadController.fixCsvFile(lines);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo("Identifiant créancier;Numéro du compte émetteur;Libellé compte émetteur;RUM échéancier;Type d'échéancier;Motif échéancier;Référence débiteur;Référence de bout en bout;Montant en €;Date d’échéance;Statut échéance;Motif impayé;Date d’opération;Référence comptable;Code Catégorie échéancier;Libellé Catégorie échéancier;Type de montant;Montant échéancier;Détail du tarif;Périodicité;Date de fin;Statut échéancier;Date de signature;Date de création échéancier;Date de suppression échéancier;Type débiteur (PP ou PM);Civilité;Nom OU Raisons Sociale;Prénom OU SIREN;N° TVA Intracommunautaire;Code Catégorie;Libellé Catégorie;Commentaire catégorie;BIC;IBAN;Banque/Guichet;Numéro d’adresse;Type Numéro;Nom de voie;Complément d’adresse;Code postal;Ville;Pays;Email;Numéro de tel fixe;Numéro de tel portable;Date de naissance;Info complémentaires;Zone personnalisable 1;Zone personnalisable 2;Zone personnalisable 3;Zone personnalisable 4;Zone personnalisable 5;Zone personnalisable 6;Statut;Date de création;Date de suppression;\n" +
                "43243;\"FR242342\";\"MONNAIE LOCALE CITOYE\";\"COL\";Répétitif;\"CAM pour 20 euros\";\"U0000\";\"1222\";20,00;09/05/22;Exécutée;;10/05/22;;ADHER;\"Adherent-e\";Fixe;20,00;;Mensuel;;Actif;22/11/19;19/01/20;;personne physique;Mme;\"NOM\";\"Prenom\";;ADH;\"ADHERENT-E\";;PSSTFRO;FR823;\"LA BANQUE      \";\"1\";;\"Chemin\";;69370;Saint Didier au Mont d'Or;France;\"mail@mail.com\";;\"615666175\";01/01/66;;;;;;;;Actif;22/11/19;;\n");
    }
}