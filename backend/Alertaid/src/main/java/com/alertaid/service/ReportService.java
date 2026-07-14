package com.alertaid.service;

import com.alertaid.model.*;
import com.alertaid.repository.AdminDecisionRepository;
import com.alertaid.repository.ReportRepository;
import com.alertaid.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final AdminDecisionRepository decisionRepository;
    private final UserRepository userRepository;
    private final SseService sseService;
    private final NotificationService notificationService;

    public ReportService(ReportRepository reportRepository,
                         AdminDecisionRepository decisionRepository,
                         UserRepository userRepository,
                         SseService sseService,
                         NotificationService notificationService) {
        this.reportRepository = reportRepository;
        this.decisionRepository = decisionRepository;
        this.userRepository = userRepository;
        this.sseService = sseService;
        this.notificationService = notificationService;
    }

    public Report submitReport(Long userId, Report report) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        report.setUser(user);
        
        // Validate photoCount and attachments consistency
        Integer photoCount = report.getPhotoCount();
        String attachments = report.getAttachments();
        
        if (photoCount != null && photoCount > 0) {
            if (attachments == null || attachments.trim().isEmpty() || attachments.trim().equals("null")) {
                System.out.println("[ATTACHMENTS WARNING] Report submission has photoCount=" + photoCount + 
                    " but attachments is null/empty. Setting photoCount to 0.");
                report.setPhotoCount(0);
            }
        } else if (photoCount == null || photoCount == 0) {
            // If no photos, ensure attachments is null
            report.setAttachments(null);
        }
        
        // Auto-accept reports so they become visible immediately without admin intervention
        report.setStatus(ReportStatus.ACCEPTED);
        Report saved = reportRepository.save(report);
        
        // Log for debugging
        System.out.println("[ATTACHMENTS DEBUG] Saved report ID: " + saved.getId() + 
            ", photoCount: " + saved.getPhotoCount() + 
            ", hasAttachments: " + (saved.getAttachments() != null && !saved.getAttachments().trim().isEmpty()));
        
        // Notify listeners that accepted/public listings changed
        sseService.broadcast("reports.accepted", "accepted-updated", saved.getId());
        // Notify public listings as well
        sseService.broadcast("reports.public", "reports-updated", saved.getId());
        notificationService.notifyReportSubmitted(saved);
        return saved;
    }

    public Page<Report> listAccepted(Pageable pageable) {
        return reportRepository.findByStatus(ReportStatus.ACCEPTED, pageable);
    }

    public Page<Report> listPending(Pageable pageable) {
        return reportRepository.findByStatus(ReportStatus.PENDING, pageable);
    }

    @Transactional
    public Report acceptReport(Long adminId, Long reportId, String notes) {
        return decide(adminId, reportId, AdminDecision.Decision.ACCEPTED, notes);
    }

    @Transactional
    public Report rejectReport(Long adminId, Long reportId, String notes) {
        return decide(adminId, reportId, AdminDecision.Decision.REJECTED, notes);
    }

    private Report decide(Long adminId, Long reportId, AdminDecision.Decision decision, String notes) {
        User admin = userRepository.findById(adminId).orElseThrow(() -> new IllegalArgumentException("Admin not found"));
        if (admin.getRole() == null || !admin.getRole().name().equals("ADMIN")) {
            throw new AccessDeniedException("Only admins can decide on reports");
        }
        Report report = reportRepository.findById(reportId).orElseThrow(() -> new IllegalArgumentException("Report not found"));
        report.setStatus(decision == AdminDecision.Decision.ACCEPTED ? ReportStatus.ACCEPTED : ReportStatus.REJECTED);
        Report saved = reportRepository.save(report);

        AdminDecision adminDecision = new AdminDecision();
        adminDecision.setAdmin(admin);
        adminDecision.setReport(saved);
        adminDecision.setDecision(decision);
        adminDecision.setNotes(notes);
        decisionRepository.save(adminDecision);
        // Notify public and admins
        if (saved.getStatus() == ReportStatus.ACCEPTED) {
            sseService.broadcast("reports.accepted", "accepted-updated", saved.getId());
        }
        sseService.broadcast("reports.pending", "pending-updated", saved.getId());
        // Notify public listings for any state change
        sseService.broadcast("reports.public", "reports-updated", saved.getId());
        return saved;
    }

    public Page<Report> getUserReports(Long userId, Pageable pageable) {
        return reportRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    public Page<Report> getRecentAcceptedReports(Pageable pageable) {
        return reportRepository.findByStatusOrderByCreatedAtDesc(ReportStatus.ACCEPTED, pageable);
    }

    public Page<Report> getAllPublicReports(Pageable pageable) {
        // Publicly visible: all reports regardless of status, newest first
        return reportRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    public Report getReportById(Long id) {
        Report report = reportRepository.findById(id).orElse(null);
        if (report != null) {
            // Log for debugging attachment issues
            System.out.println("[ATTACHMENTS DEBUG] Retrieved report ID: " + id + 
                ", photoCount: " + report.getPhotoCount() + 
                ", attachments: " + (report.getAttachments() != null ? 
                    (report.getAttachments().length() > 100 ? 
                        report.getAttachments().substring(0, 100) + "..." : 
                        report.getAttachments()) : 
                    "NULL"));
            
            // Warn if there's inconsistent data
            if (report.getPhotoCount() != null && report.getPhotoCount() > 0 && 
                (report.getAttachments() == null || report.getAttachments().trim().isEmpty())) {
                System.out.println("[ATTACHMENTS WARNING] Report " + id + " has photoCount=" + 
                    report.getPhotoCount() + " but attachments is null/empty!");
            }
        }
        return report;
    }
}
