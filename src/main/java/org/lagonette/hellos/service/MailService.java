package org.lagonette.hellos.service;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Component
public class MailService {
    private final MailSender mailSender;
    private final SimpleMailMessage templateMessage;

    public MailService(MailSender mailSender, SimpleMailMessage templateMessage) {
        this.mailSender = mailSender;
        this.templateMessage = templateMessage;
    }

    public void sendEmail(String emailRecipient, String subject, String body) {
        // Create a thread safe "copy" of the template message and customize it
        SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);
        msg.setTo(emailRecipient);
        msg.setSubject(subject);
        msg.setText(body);
        try {
            this.mailSender.send(msg);
        } catch (MailException ex) {
            // simply log it and go on...
            //TODO log error
            System.err.println(ex.getMessage());
        }
    }
}
