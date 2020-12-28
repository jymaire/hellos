package org.lagonette.hellos.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Component
public class MailService {
    public static final String MAIL_DELIMITER = ",";
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final MailSender mailSender;
    private final SimpleMailMessage templateMessage;

    public MailService(MailSender mailSender, SimpleMailMessage templateMessage) {
        this.mailSender = mailSender;
        this.templateMessage = templateMessage;
    }

    public void sendEmail(String emailRecipient, String subject, String body) {
        // Create a thread safe "copy" of the template message and customize it
        SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);
        final String[] emails = emailRecipient.split(MAIL_DELIMITER);
        msg.setTo(emails);
        msg.setSubject(subject);
        msg.setText(body);
        try {
            this.mailSender.send(msg);
        } catch (MailException ex) {
            LOGGER.error("Error during email sending : {}", ex.getMessage());
        }
    }
}
