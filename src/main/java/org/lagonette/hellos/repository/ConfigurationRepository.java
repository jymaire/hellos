package org.lagonette.hellos.repository;

import com.sun.istack.NotNull;
import org.lagonette.hellos.entity.Configuration;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ConfigurationRepository extends CrudRepository<Configuration, String> {

    List<Configuration> findAll();

    Configuration save(@NotNull Configuration configuration);

    @Override
    Optional<Configuration> findById(@NotNull String key);
}
