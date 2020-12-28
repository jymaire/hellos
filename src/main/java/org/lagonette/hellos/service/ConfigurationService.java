package org.lagonette.hellos.service;

import org.lagonette.hellos.entity.Configuration;
import org.lagonette.hellos.repository.ConfigurationRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ConfigurationService {

    public static final String PAYMENT_CYCLOS_ENABLED = "PAYMENT_CYCLOS_ENABLED";
    public static final String PAYMENT_AUTOMATIC_ENABLED = "PAYMENT_AUTOMATIC_ENABLED";
    public static final String MAIL_RECIPIENT = "MAIL_RECIPIENT";

    private final ConfigurationRepository configurationRepository;

    public ConfigurationService(ConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;
    }

    public Map<String, String> findAll() {
        return configurationRepository.findAll().stream().collect(Collectors.toMap(Configuration::getKey, Configuration::getValue));
    }

    public void save(String enableCyclos, String enableAutomatic, String emailRecipient) {
        var configs = new ArrayList<Configuration>();
        configs.add(new Configuration(PAYMENT_CYCLOS_ENABLED, enableCyclos));
        configs.add(new Configuration(PAYMENT_AUTOMATIC_ENABLED, enableAutomatic));
        configs.add(new Configuration(MAIL_RECIPIENT, emailRecipient));
        configurationRepository.saveAll(configs);
    }
}
