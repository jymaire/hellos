package org.lagonette.hellos.repository;

import com.sun.istack.NotNull;
import org.lagonette.hellos.entity.EmailLink;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EmailLinkRepository extends CrudRepository<EmailLink, String> {

    EmailLink save(@NotNull EmailLink email);

    @Query("SELECT id " +
            "FROM EmailLink e " +
            "WHERE e.insertionDate < ?1")
    List<String> findIdsByInsertionDateBefore(@NotNull LocalDateTime date);

    @Modifying
    @Query("DELETE FROM EmailLink e WHERE e.id IN ?1")
    void deleteById(List<String> emails);
}
