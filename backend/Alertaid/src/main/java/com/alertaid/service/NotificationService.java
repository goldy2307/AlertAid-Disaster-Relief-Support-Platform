package com.alertaid.service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import com.alertaid.model.Report;
import com.alertaid.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * Sends notification emails to all registered users when new reports are submitted.
 */
@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm z")
        .withLocale(Locale.ENGLISH)
        .withZone(ZoneId.systemDefault());

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final boolean emailEnabled;
    private final String fromAddress;
    private final String subject;
    private final int batchSize;

    public NotificationService(JavaMailSender mailSender,
                               UserRepository userRepository,
                               @Value("${alertaid.notifications.email-enabled:false}") boolean emailEnabled,
                               @Value("${alertaid.notifications.from:noreply@alertaid.local}") String fromAddress,
                               @Value("${alertaid.notifications.report-subject:New AlertAid report submitted}") String subject,
                               @Value("${alertaid.notifications.batch-size:50}") int batchSize) {
        this.mailSender = mailSender;
        this.userRepository = userRepository;
        this.emailEnabled = emailEnabled;
        this.fromAddress = fromAddress;
        this.subject = subject;
        this.batchSize = Math.max(batchSize, 1);
        log.info("NotificationService initialized (emailEnabled={}, from={}, batchSize={})",
            this.emailEnabled, this.fromAddress, this.batchSize);
    }

    @Async("notificationExecutor")
    public void notifyReportSubmitted(Report report) {
        if (!emailEnabled) {
            log.debug("Email notifications disabled; skipping message for report {}", report.getId());
            return;
        }
        List<String> recipients = sanitizeEmails(userRepository.findAllEmails());
        if (recipients.isEmpty()) {
            log.warn("No recipients available for report notification {}", report.getId());
            return;
        }
        String body = buildBody(report);
        List<List<String>> batches = partition(recipients, batchSize);
        log.info("Sending report notification {} to {} recipients across {} batch(es)",
            report.getId(), recipients.size(), batches.size());
        for (List<String> batch : batches) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(fromAddress); // satisfy providers requiring a TO field
            message.setBcc(batch.toArray(new String[0]));
            message.setSubject(subject);
            message.setText(body);
            try {
                mailSender.send(message);
            } catch (MailException ex) {
                log.error("Failed to send report notification for report {} to batch {}", report.getId(), batch, ex);
            }
        }
    }

    private List<String> sanitizeEmails(List<String> emails) {
        if (CollectionUtils.isEmpty(emails)) {
            return Collections.emptyList();
        }
        LinkedHashSet<String> sanitized = new LinkedHashSet<>();
        for (String email : emails) {
            if (StringUtils.hasText(email)) {
                sanitized.add(email.trim());
            }
        }
        return new ArrayList<>(sanitized);
    }

    private List<List<String>> partition(List<String> items, int size) {
        if (items.isEmpty()) {
            return Collections.emptyList();
        }
        List<List<String>> partitions = new ArrayList<>();
        for (int i = 0; i < items.size(); i += size) {
            partitions.add(new ArrayList<>(items.subList(i, Math.min(i + size, items.size()))));
        }
        return partitions;
    }

    private String buildBody(Report report) {
        StringBuilder builder = new StringBuilder();
        builder.append("A new disaster report has been submitted on AlertAid.\n\n");
        builder.append("Report ID: ").append(report.getId()).append('\n');
        builder.append("Submitted: ").append(DATE_FORMATTER.format(
            Objects.requireNonNullElse(report.getCreatedAt(), report.getUpdatedAt()))).append('\n');
        builder.append("Reporter: ").append(report.getReporterName())
            .append(" (").append(report.getReporterPhone()).append(')').append('\n');
        builder.append("Severity: ").append(report.getSeverity()).append('\n');
        builder.append("Disaster Type: ").append(report.getDisasterType()).append('\n');
        if (StringUtils.hasText(report.getLocation())) {
            builder.append("Location: ").append(report.getLocation()).append('\n');
        }
        if (StringUtils.hasText(report.getPeopleAffected())) {
            builder.append("People Affected: ").append(report.getPeopleAffected()).append('\n');
        }
        if (StringUtils.hasText(report.getInjuries())) {
            builder.append("Injuries: ").append(report.getInjuries()).append('\n');
        }
        builder.append('\n');
        if (StringUtils.hasText(report.getDescription())) {
            builder.append("Description:\n").append(report.getDescription()).append("\n\n");
        }
        if (StringUtils.hasText(report.getAdditionalInfo())) {
            builder.append("Additional Information:\n").append(report.getAdditionalInfo()).append("\n\n");
        }
        builder.append("This notification was sent automatically. Do not reply to this email.");
        return builder.toString();
    }
}
