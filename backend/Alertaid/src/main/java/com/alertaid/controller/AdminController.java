package com.alertaid.controller;

import com.alertaid.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private ReportRepository reportRepository;

    @PostMapping("/fix-attachments")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> fixInconsistentAttachments() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            int count = 0;
            var allReports = reportRepository.findAll();
            
            for (var report : allReports) {
                if (report.getPhotoCount() != null && report.getPhotoCount() > 0) {
                    String attachments = report.getAttachments();
                    if (attachments == null || 
                        attachments.trim().isEmpty() || 
                        attachments.trim().equals("null")) {
                        System.out.println("[ATTACHMENTS FIX] Fixing report ID: " + report.getId() + 
                            ", photoCount: " + report.getPhotoCount());
                        report.setPhotoCount(0);
                        reportRepository.save(report);
                        count++;
                    }
                }
            }
            
            result.put("success", true);
            result.put("reportsFixed", count);
            result.put("message", "Fixed " + count + " report(s) with inconsistent attachment data");
            
            System.out.println("[ATTACHMENTS FIX] Total fixed: " + count + " reports");
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.out.println("[ATTACHMENTS FIX] Error: " + e.getMessage());
            e.printStackTrace();
            result.put("success", false);
            result.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }
}

