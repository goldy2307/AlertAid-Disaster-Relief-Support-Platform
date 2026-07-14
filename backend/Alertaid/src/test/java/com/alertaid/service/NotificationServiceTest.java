package com.alertaid.service;

import com.alertaid.model.Report;
import com.alertaid.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    private JavaMailSender mailSender;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        mailSender = mock(JavaMailSender.class);
        userRepository = mock(UserRepository.class);
    }

    @Test
    void notifyReportSubmitted_sendsBatchesWithDistinctRecipients() {
        when(userRepository.findAllEmails()).thenReturn(Arrays.asList(
            "citizen@example.com", "volunteer@example.com", "citizen@example.com", " ", null, "admin@example.com"
        ));

        NotificationService service = new NotificationService(
            mailSender,
            userRepository,
            true,
            "alerts@example.com",
            "subject",
            2
        );

        Report report = new Report();
        report.setId(42L);
        report.setReporterName("John Doe");
        report.setReporterPhone("+1-555-1234");
        report.setSeverity("HIGH");
        report.setDisasterType("FLOOD");
        report.setCreatedAt(Instant.now());

        service.notifyReportSubmitted(report);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(2)).send(captor.capture());

        List<SimpleMailMessage> messages = captor.getAllValues();
        assertThat(messages.get(0).getBcc()).containsExactly("citizen@example.com", "volunteer@example.com");
        assertThat(messages.get(1).getBcc()).containsExactly("admin@example.com");
    }

    @Test
    void notifyReportSubmitted_skipsWhenDisabled() {
        NotificationService service = new NotificationService(
            mailSender,
            userRepository,
            false,
            "alerts@example.com",
            "subject",
            10
        );

        Report report = new Report();
        report.setId(7L);

        service.notifyReportSubmitted(report);

        verifyNoInteractions(mailSender);
        verifyNoInteractions(userRepository);
    }
}

