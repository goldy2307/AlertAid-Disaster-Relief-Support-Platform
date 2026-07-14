package com.alertaid.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "admin_decisions", indexes = {
        @Index(name = "idx_decisions_report_id", columnList = "report_id"),
        @Index(name = "idx_decisions_admin_id", columnList = "admin_id"),
        @Index(name = "idx_decisions_created_at", columnList = "decision_timestamp")
})
public class AdminDecision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Decision decision;

    @Column(name = "decision_timestamp", nullable = false)
    private Instant decisionTimestamp = Instant.now();

    @Column(columnDefinition = "text")
    private String notes;

    public enum Decision { ACCEPTED, REJECTED }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getAdmin() { return admin; }
    public void setAdmin(User admin) { this.admin = admin; }

    public Report getReport() { return report; }
    public void setReport(Report report) { this.report = report; }

    public Decision getDecision() { return decision; }
    public void setDecision(Decision decision) { this.decision = decision; }

    public Instant getDecisionTimestamp() { return decisionTimestamp; }
    public void setDecisionTimestamp(Instant decisionTimestamp) { this.decisionTimestamp = decisionTimestamp; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}