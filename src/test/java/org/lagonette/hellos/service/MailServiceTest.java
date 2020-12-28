package org.lagonette.hellos.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @Mock
    private SimpleMailMessage templateMessage;

    @Mock
    private MailSender mailSender;

    @InjectMocks
    private MailService mailService;


    @Test
    void sendEmail() {
        // WHEN
        mailService.sendEmail("test@test.fr", "subject", "body");
        // THEN
        ArgumentCaptor<SimpleMailMessage> simpleMailMessageArgumentCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(simpleMailMessageArgumentCaptor.capture());

        assertThat(simpleMailMessageArgumentCaptor.getValue()).extracting(SimpleMailMessage::getTo, SimpleMailMessage::getSubject, SimpleMailMessage::getText)
                .containsExactly(new String[]{"test@test.fr"}, "subject", "body");
    }

    @Test
    void sendEmail_multipleRecipients() {
        // WHEN
        mailService.sendEmail("test@test.fr,example@test.fr", "subject", "body");
        // THEN
        ArgumentCaptor<SimpleMailMessage> simpleMailMessageArgumentCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(simpleMailMessageArgumentCaptor.capture());

        assertThat(simpleMailMessageArgumentCaptor.getValue()).extracting(SimpleMailMessage::getTo, SimpleMailMessage::getSubject, SimpleMailMessage::getText)
                .containsExactly(new String[]{"test@test.fr", "example@test.fr"}, "subject", "body");
    }
}