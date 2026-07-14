package com.alertaid.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "reports", indexes = {
        @Index(name = "idx_reports_status", columnList = "status"),
        @Index(name = "idx_reports_created_at", columnList = "created_at"),
        @Index(name = "idx_reports_user_id", columnList = "user_id"),
        @Index(name = "idx_reports_severity", columnList = "severity"),
        @Index(name = "idx_reports_disaster_type", columnList = "disaster_type")
})
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Basic report information
    @Column(length = 255)
    private String location; // free-text location/address

    @Column(columnDefinition = "text")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private ReportStatus status = ReportStatus.PENDING;

    // New detailed fields from the form
    @Column(length = 20, nullable = false)
    private String severity; // critical, high, medium, low

    @Column(name = "disaster_type", length = 100, nullable = false)
    private String disasterType; // earthquake, flood, wildfire, etc.

    @Column(name = "people_affected", length = 50)
    private String peopleAffected; // 1-5, 6-20, etc.

    @Column(length = 50)
    private String injuries; // none, minor, serious, fatalities, unknown

    @Column(name = "reporter_name", length = 255, nullable = false)
    private String reporterName;

    @Column(name = "reporter_phone", length = 20, nullable = false)
    private String reporterPhone;

    @Column(name = "additional_info", columnDefinition = "text")
    private String additionalInfo;

    @Column(name = "photo_count")
    private Integer photoCount = 0; // Number of photos uploaded

    @Column(name = "attachments", columnDefinition = "TEXT")
    private String attachments; // JSON string of attachment data (base64 encoded)

    // Timestamps
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt;

    // Optional: latitude/longitude if frontend supports
    private Double latitude;
    private Double longitude;

    @PreUpdate
    public void onUpdate() { this.updatedAt = Instant.now(); }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ReportStatus getStatus() { return status; }
    public void setStatus(ReportStatus status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    // Getters and setters for new fields
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getDisasterType() { return disasterType; }
    public void setDisasterType(String disasterType) { this.disasterType = disasterType; }

    public String getPeopleAffected() { return peopleAffected; }
    public void setPeopleAffected(String peopleAffected) { this.peopleAffected = peopleAffected; }

    public String getInjuries() { return injuries; }
    public void setInjuries(String injuries) { this.injuries = injuries; }

    public String getReporterName() { return reporterName; }
    public void setReporterName(String reporterName) { this.reporterName = reporterName; }

    public String getReporterPhone() { return reporterPhone; }
    public void setReporterPhone(String reporterPhone) { this.reporterPhone = reporterPhone; }

    public String getAdditionalInfo() { return additionalInfo; }
    public void setAdditionalInfo(String additionalInfo) { this.additionalInfo = additionalInfo; }

    public Integer getPhotoCount() { return photoCount; }
    public void setPhotoCount(Integer photoCount) { this.photoCount = photoCount; }

    public String getAttachments() { return attachments; }
    public void setAttachments(String attachments) { this.attachments = attachments; }
}
