package com.aditya.civic_issue_reporter.controller;

import com.aditya.civic_issue_reporter.dto.IssueStatusSummaryResponse;
import com.aditya.civic_issue_reporter.services.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/issues/status-summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<IssueStatusSummaryResponse> getIssueStatusSummary() {
        return ResponseEntity.ok(
                analyticsService.getIssueStatusSummary()
        );
    }
}