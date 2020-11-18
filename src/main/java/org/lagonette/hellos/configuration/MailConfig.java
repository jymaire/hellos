package org.lagonette.hellos.configuration;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {
    private final Dotenv dotenv;

    public MailConfig(Dotenv dotenv) {
        this.dotenv = dotenv;
    }

    @Bean
    public JavaMailSenderImpl mailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(dotenv.get("GONETTE_SMTP_HOST"));
        javaMailSender.setPort(465);
        javaMailSender.setUsername(dotenv.get("GONETTE_SMTP_USERNAME"));
        javaMailSender.setPassword(dotenv.get("GONETTE_SMTP_PASSWORD"));
        javaMailSender.setProtocol("smtps");
        return javaMailSender;
    }

    @Bean
    public SimpleMailMessage templateMessage() {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(dotenv.get("GONETTE_SMTP_USERNAME"));
        simpleMailMessage.setSubject("Notification");
        return simpleMailMessage;
    }
}
