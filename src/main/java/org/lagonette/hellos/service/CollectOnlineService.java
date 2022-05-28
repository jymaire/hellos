package org.lagonette.hellos.service;

import io.github.cdimascio.dotenv.Dotenv;
import org.lagonette.hellos.bean.collectonline.Payment;
import org.lagonette.hellos.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class CollectOnlineService {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final String EXEC = "Exécutée";
    private final PaymentRepository paymentRepository;
    private final Dotenv dotenv;

    public CollectOnlineService(PaymentRepository paymentRepository, Dotenv dotenv) {
        this.paymentRepository = paymentRepository;
        this.dotenv = dotenv;
    }

    public void importPaymentsFromCSV(List<Payment> paymentList) {
        NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
        List<org.lagonette.hellos.entity.Payment> paymentsToSave = new ArrayList<>();

        final String colChangeKeyword = dotenv.get("COL_CHANGE_KEYWORD");
        for (Payment payment : paymentList) {
            try {
                float montant = format.parse(payment.getMontant()).floatValue();
                if (EXEC.equals(payment.getStatutEcheance()) && payment.getCodeCategorieEcheancier() != null && (colChangeKeyword == null || payment.getCodeCategorieEcheancier().contains(colChangeKeyword))) {
                    paymentsToSave.add(new org.lagonette.hellos.entity.Payment(
                            Integer.parseInt(payment.getReference()),
                            payment.getDateOperation(),
                            montant,
                            payment.getPrenom(),
                            payment.getNom(),
                            payment.getEmail()));
                }
            } catch (ParseException e) {
                LOGGER.error(e.toString());
            }
        }
        paymentRepository.saveAll(paymentsToSave);
    }
}
