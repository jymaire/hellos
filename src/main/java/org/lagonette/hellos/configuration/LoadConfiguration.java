package org.lagonette.hellos.configuration;

import io.github.cdimascio.dotenv.Dotenv;
import org.lagonette.hellos.repository.ConfigurationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadConfiguration {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final Dotenv dotenv;

    public LoadConfiguration(Dotenv dotenv) {
        this.dotenv = dotenv;
    }

    @Bean
    public CommandLineRunner saveConfiguration(ConfigurationRepository repository) {
        return (args) -> {
            final String paymentCyclosEnabled = dotenv.get("PAYMENT_CYCLOS_ENABLED");
            repository.save(new org.lagonette.hellos.entity.Configuration("PAYMENT_CYCLOS_ENABLED", paymentCyclosEnabled));
            final String paymentAutomaticEnabled = dotenv.get("PAYMENT_AUTOMATIC_ENABLED");
            repository.save(new org.lagonette.hellos.entity.Configuration("PAYMENT_AUTOMATIC_ENABLED", paymentAutomaticEnabled));
            final String emailRecipient = dotenv.get("MAIL_RECIPIENT");
            repository.save(new org.lagonette.hellos.entity.Configuration("MAIL_RECIPIENT", emailRecipient));
            var all = repository.findAll();
            LOGGER.info("Configuration loaded : {}", all);
        };
    }
}
