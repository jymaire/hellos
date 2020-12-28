package org.lagonette.hellos.service;

import io.github.cdimascio.dotenv.Dotenv;
import org.lagonette.hellos.bean.ProcessResult;
import org.lagonette.hellos.bean.StatusPaymentEnum;
import org.lagonette.hellos.bean.cyclos.CyclosPerformPayment;
import org.lagonette.hellos.bean.cyclos.CyclosPerformPaymentResponse;
import org.lagonette.hellos.bean.cyclos.CyclosTransaction;
import org.lagonette.hellos.bean.cyclos.CyclosUser;
import org.lagonette.hellos.entity.Configuration;
import org.lagonette.hellos.entity.Payment;
import org.lagonette.hellos.repository.ConfigurationRepository;
import org.lagonette.hellos.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static org.lagonette.hellos.service.ConfigurationService.PAYMENT_CYCLOS_ENABLED;

@Component
public class CyclosService {
    public static final String DESCRIPTION = "Paiement automatique, id technique ";
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final ConfigurationRepository configurationRepository;
    private final PaymentRepository paymentRepository;
    private final MailService mailService;
    private final Dotenv dotenv;

    public CyclosService(ConfigurationRepository configurationRepository, PaymentRepository paymentRepository, MailService mailService, Dotenv dotenv) {
        this.configurationRepository = configurationRepository;
        this.paymentRepository = paymentRepository;
        this.mailService = mailService;
        this.dotenv = dotenv;
    }

    public ProcessResult creditAccount(ProcessResult processResult, int id) {
        Payment payment = paymentRepository.findById(id);
        LOGGER.info("payment found in data base: {}", payment);
        if (StatusPaymentEnum.success.equals(payment.getStatus())) {
            LOGGER.error("Payment already proceeded");
            processResult.getErrors().add("Paiement déjà effectué dans Cyclos");
            processResult.setStatusPayment(StatusPaymentEnum.success);
            return processResult;
        }
        String cyclosUrl = dotenv.get("CYCLOS_URL");
        WebClient webClient = WebClient.builder()
                .baseUrl(cyclosUrl)
                .filter(ExchangeFilterFunctions
                        .basicAuthentication(dotenv.get("CYCLOS_USER"), dotenv.get("CYCLOS_PWD")))
                .build();

        ResponseEntity<List<CyclosUser>> getUserResponse = webClient.get()
                .uri("/users?fields=&keywords=" + payment.getEmail() + "&roles=member&statuses=active&includeGroup=true")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntityList(CyclosUser.class)
                .block();

        // TODO create a testable method to validate API return
        if (getUserResponse == null || getUserResponse.getBody() == null || !getUserResponse.getStatusCode().is2xxSuccessful() || getUserResponse.getBody().size() != 1 || getUserResponse.getBody().get(0).getGroup() == null) {
            processResult.setStatusPayment(StatusPaymentEnum.fail);
            processResult.getErrors().add("Erreur pendant la récupération de l'utilisateur dans Cyclos, détails de la réponse : {} " + getUserResponse);
            if (getUserResponse == null) {
                processResult.setStatusPayment(StatusPaymentEnum.fail);
                return processResult;
            }
            LOGGER.info("Status code is successful : {}", getUserResponse.getStatusCode().is2xxSuccessful());
            if (getUserResponse.getBody() != null) {
                LOGGER.info("Body size : {}", getUserResponse.getBody().size());
            }
            LOGGER.debug("Body content : {}", getUserResponse.getBody());
            if (!CollectionUtils.isEmpty(getUserResponse.getBody())) {
                LOGGER.info("User group : {}", getUserResponse.getBody().get(0).getGroup());
            } else {
                LOGGER.info("Body empty, can't debug group");
            }
            processResult.setStatusPayment(StatusPaymentEnum.fail);
            return processResult;
        }
        CyclosUser cyclosUser = getUserResponse.getBody().get(0);
        String type;

        if (cyclosUser.getGroup().getInternalName().equals(dotenv.get("CYCLOS_GROUP_PRO_INTERNAL"))) {
            type = dotenv.get("CYCLOS_EMISSION_PRO_INTERNAL");
        } else if (cyclosUser.getGroup().getInternalName().equals(dotenv.get("CYCLOS_GROUP_PART_INTERNAL"))) {
            type = dotenv.get("CYCLOS_EMISSION_PART_INTERNAL");
        } else {
            processResult.setStatusPayment(StatusPaymentEnum.fail);
            return processResult;
        }
        // get last payment and check description

        if (checkPaymentIsAlreadyDone(id, payment.getEmail(), webClient)) {
            processResult.setStatusPayment(StatusPaymentEnum.fail);
            processResult.getErrors().add("Paiement déjà réalisé dans Cyclos");
            return processResult;
        }
        String paymentUrl;
        if ("true".equals(configurationRepository.findById(PAYMENT_CYCLOS_ENABLED).orElse(new Configuration(PAYMENT_CYCLOS_ENABLED, "false")).getValue())) {
            paymentUrl = "/system/payments";
        } else {
            paymentUrl = "/system/payments/preview";
        }

        ResponseEntity<List<CyclosPerformPaymentResponse>> paymentResponse = webClient.post()
                .uri(paymentUrl)
                .body(BodyInserters.fromValue(new CyclosPerformPayment(Float.toString(payment.getAmount()), payment.getEmail(), DESCRIPTION + payment.getId(), type)))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntityList(CyclosPerformPaymentResponse.class)
                .block();
        LOGGER.debug("response : {}", paymentResponse);
        if (paymentResponse == null || !paymentResponse.getStatusCode().is2xxSuccessful()) {
            processResult.getErrors().add("Erreur technique lors du paiement dans Cyclos");
            processResult.setStatusPayment(StatusPaymentEnum.fail);
            return processResult;
        }
        if ("true".equals(configurationRepository.findById(PAYMENT_CYCLOS_ENABLED).orElse(new Configuration(PAYMENT_CYCLOS_ENABLED, "false")).getValue())) {
            LOGGER.info("Payment successfully ended for payment {}", id);
            processResult.setStatusPayment(StatusPaymentEnum.success);
        } else {
            LOGGER.info("Preview of payment successfully ended for payment {}", id);
            processResult.setStatusPayment(StatusPaymentEnum.previewOK);
        }
        return processResult;
    }

    private boolean checkPaymentIsAlreadyDone(int id, String email, WebClient webClient) {
        ResponseEntity<List<CyclosTransaction>> getLastTransaction = webClient.get()
                .uri("/" + email + "/transactions?fields=description&authorizationStatuses=authorized&direction=credit&kinds=&orderBy=dateDesc&page=1&pageSize=1")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntityList(CyclosTransaction.class)
                .block();

        if (getLastTransaction == null || getLastTransaction.getBody() == null || getLastTransaction.getBody().isEmpty()) {
            return false;
        }
        LOGGER.debug("Last description : {}", getLastTransaction.getBody());
        String expectedMessage = DESCRIPTION + id;
        if (!expectedMessage.equals(getLastTransaction.getBody().get(0).getDescription())) {
            return false;
        }
        return true;
    }
}
