package com.alertaid.controller;

import com.alertaid.model.Report;
import com.alertaid.model.User;
import com.alertaid.service.ReportService;
import com.alertaid.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;
    private final UserService userService;

    public ReportController(ReportService reportService, UserService userService) {
        this.reportService = reportService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Report> submitReport(@RequestBody Report report, Authentication auth) {
        // Validate required fields
        if (report.getSeverity() == null || report.getSeverity().trim().isEmpty()) {
            throw new IllegalArgumentException("Severity is required");
        }
        if (report.getDisasterType() == null || report.getDisasterType().trim().isEmpty()) {
            throw new IllegalArgumentException("Disaster type is required");
        }
        if (report.getReporterName() == null || report.getReporterName().trim().isEmpty()) {
            throw new IllegalArgumentException("Reporter name is required");
        }
        if (report.getReporterPhone() == null || report.getReporterPhone().trim().isEmpty()) {
            throw new IllegalArgumentException("Reporter phone is required");
        }
        
        Long userId = resolveUserId(auth);
        Report saved = reportService.submitReport(userId, report);
        
        // Log what's being returned to frontend
        System.out.println("[ATTACHMENTS DEBUG] Returning saved report to frontend: ID=" + saved.getId() + 
            ", photoCount=" + saved.getPhotoCount() + 
            ", hasAttachments=" + (saved.getAttachments() != null && !saved.getAttachments().trim().isEmpty()) +
            ", attachmentsLength=" + (saved.getAttachments() != null ? saved.getAttachments().length() : 0));
        
        return ResponseEntity.created(URI.create("/api/reports/" + saved.getId())).body(saved);
    }

    @GetMapping("/accepted")
    public Page<Report> listAccepted(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return reportService.listAccepted(pageable);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<Report> listPending(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return reportService.listPending(pageable);
    }

    @PostMapping("/{id}/accept")
    @PreAuthorize("hasRole('ADMIN')")
    public Report accept(@PathVariable Long id,
                         @RequestParam(required = false) String notes,
                         Authentication auth) {
        return reportService.acceptReport(resolveUserId(auth), id, notes);
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public Report reject(@PathVariable Long id,
                         @RequestParam(required = false) String notes,
                         Authentication auth) {
        return reportService.rejectReport(resolveUserId(auth), id, notes);
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public Page<Report> getMyReports(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "20") int size,
                                     Authentication auth) {
        Long userId = resolveUserId(auth);
        Pageable pageable = PageRequest.of(page, size);
        return reportService.getUserReports(userId, pageable);
    }

    @GetMapping("/recent")
    public Page<Report> getRecentReports(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return reportService.getRecentAcceptedReports(pageable);
    }

    // Public: all user-submitted reports (any status), newest first
    @GetMapping("/public")
    public Page<Report> getPublicReports(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return reportService.getAllPublicReports(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Report> getReportById(@PathVariable Long id) {
        Report report = reportService.getReportById(id);
        if (report == null) {
            return ResponseEntity.notFound().build();
        }
        // Log attachment info for debugging
        System.out.println("[ATTACHMENTS DEBUG] Report ID: " + id + 
            ", photoCount: " + report.getPhotoCount() + 
            ", hasAttachments: " + (report.getAttachments() != null) +
            ", attachmentsLength: " + (report.getAttachments() != null ? report.getAttachments().length() : 0));
        return ResponseEntity.ok(report);
    }

    private Long resolveUserId(Authentication auth) {
        if (auth == null || auth.getName() == null || "anonymousUser".equalsIgnoreCase(auth.getName())) {
            throw new AccessDeniedException("Authentication required");
        }
        String email = auth.getName();
        User user = userService.getUserByEmail(email).orElseThrow(() -> new AccessDeniedException("User not found"));
        return user.getId();
    }
}
