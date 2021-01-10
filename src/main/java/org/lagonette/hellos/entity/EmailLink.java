package org.lagonette.hellos.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity to store email adresses.
 * <p>
 * Because sometimes, people use different email adresses for Hello Asso and Cyclos
 */
@Entity
public class EmailLink {
    @Id
    private String helloAssoEmail;

    private String cyclosEmail;
    // technical field, to handle purge process
    private LocalDateTime insertionDate;

    public EmailLink() {
        this.insertionDate = LocalDateTime.now();
    }

    public EmailLink(String helloAssoEmail, String cyclosEmail) {
        this.helloAssoEmail = helloAssoEmail;
        this.cyclosEmail = cyclosEmail;
        this.insertionDate = LocalDateTime.now();
    }

    public String getHelloAssoEmail() {
        return helloAssoEmail;
    }

    public void setHelloAssoEmail(String helloAssoEmail) {
        this.helloAssoEmail = helloAssoEmail;
    }

    public String getCyclosEmail() {
        return cyclosEmail;
    }

    public void setCyclosEmail(String cyclosEmail) {
        this.cyclosEmail = cyclosEmail;
    }

    public LocalDateTime getInsertionDate() {
        return insertionDate;
    }

    public void setInsertionDate(LocalDateTime insertionDate) {
        this.insertionDate = insertionDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmailLink emailLink = (EmailLink) o;
        return Objects.equals(helloAssoEmail, emailLink.helloAssoEmail) &&
                Objects.equals(cyclosEmail, emailLink.cyclosEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(helloAssoEmail, cyclosEmail);
    }
}
