package org.lagonette.hellos.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lagonette.hellos.entity.Configuration;
import org.lagonette.hellos.repository.ConfigurationRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfigurationServiceTest {

    @Mock
    private ConfigurationRepository configurationRepository;

    @InjectMocks
    private ConfigurationService configurationService;

    @Test
    void save() {
        // WHEN
        configurationService.save("true", "false", "test@mail.org");

        // THEN
        List<Configuration> configs = new ArrayList<>();
        configs.add(new Configuration("PAYMENT_CYCLOS_ENABLED", "true"));
        configs.add(new Configuration("PAYMENT_AUTOMATIC_ENABLED", "false"));
        configs.add(new Configuration("MAIL_RECIPIENT", "test@mail.org"));

        verify(configurationRepository).saveAll(configs);
    }

    @Test
    void findAll() {
        // GIVEN
        when(configurationRepository.findAll()).thenReturn(List.of(new Configuration("Key1", "Value1"), new Configuration("Key2", "Value2")));

        // WHEN
        final Map<String, String> all = configurationService.findAll();

        // THEN
        assertThat(all).isNotEmpty();
        assertThat(all.get("Key1")).isEqualTo("Value1");
        assertThat(all.get("Key2")).isEqualTo("Value2");

    }
}