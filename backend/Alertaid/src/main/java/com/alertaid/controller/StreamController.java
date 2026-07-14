package com.alertaid.controller;

import com.alertaid.service.SseService;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/stream")
public class StreamController {

    private final SseService sseService;

    public StreamController(SseService sseService) {
        this.sseService = sseService;
    }

    // Public: accepted reports updates
    @GetMapping(path = "/reports/accepted", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter acceptedReports() {
        return sseService.subscribe("reports.accepted");
    }

    // Public: all reports updates
    @GetMapping(path = "/reports/public", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter publicReports() {
        return sseService.subscribe("reports.public");
    }

    // Admin-only: pending reports updates
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/reports/pending", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter pendingReports() {
        return sseService.subscribe("reports.pending");
    }

    // Public: donations real-time
    @GetMapping(path = "/donations", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter donations() {
        return sseService.subscribe("donations.all");
    }

    // Public: weather alerts real-time
    @GetMapping(path = "/alerts", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter alerts() {
        return sseService.subscribe("alerts.all");
    }
}
