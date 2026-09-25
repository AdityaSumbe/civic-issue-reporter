package com.aditya.civic_issue_reporter.controller;

import com.aditya.civic_issue_reporter.dto.StatusHistoryResponse;
import com.aditya.civic_issue_reporter.dto.DepartmentAssignmentRequest;
import com.aditya.civic_issue_reporter.dto.IssueCreateRequest;
import com.aditya.civic_issue_reporter.dto.IssueResponse;
import com.aditya.civic_issue_reporter.dto.IssueStatusUpdateRequest;
import com.aditya.civic_issue_reporter.dto.OfficerAssignmentRequest;
import com.aditya.civic_issue_reporter.entity.Issue;
import com.aditya.civic_issue_reporter.entity.IssueStatus;
import com.aditya.civic_issue_reporter.entity.User;
import com.aditya.civic_issue_reporter.repository.UserRepository;
import com.aditya.civic_issue_reporter.services.IssueService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.aditya.civic_issue_reporter.entity.StatusHistory;
import com.aditya.civic_issue_reporter.services.StatusHistoryService;
import com.aditya.civic_issue_reporter.exception.ResourceNotFoundException;
import com.aditya.civic_issue_reporter.exception.BadRequestException;

import java.util.List;

@RestController
@RequestMapping("/api/issues")
public class IssueController {

    private final IssueService issueService;
    private final UserRepository userRepository;
    private final StatusHistoryService statusHistoryService;

    public IssueController(
            IssueService issueService,
            UserRepository userRepository,
            StatusHistoryService statusHistoryService
    ) {
        this.issueService = issueService;
        this.userRepository = userRepository;
        this.statusHistoryService = statusHistoryService;
    }

    @PostMapping
    @PreAuthorize("hasRole('CITIZEN')")
    public ResponseEntity<IssueResponse> createIssue(
            @RequestBody IssueCreateRequest request,
            Authentication authentication
    ) {
        User citizen = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Authenticated user not found"));

        Issue issue = issueService.createIssue(
                request,
                citizen.getId()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(issueService.toIssueResponse(issue));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CITIZEN')")
    public ResponseEntity<List<IssueResponse>> getMyIssues(
            Authentication authentication
    ) {
        User citizen = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("Authenticated user not found"));

        return ResponseEntity.ok(
                issueService.getIssuesByCitizen(citizen.getId())
        );
    }

    @PatchMapping("/{id}/department")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<IssueResponse> assignDepartment(
            @PathVariable Long id,
            @RequestBody DepartmentAssignmentRequest request,
            Authentication authentication
    ) {
        User admin = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("Authenticated user not found"));

        Issue issue = issueService.assignDepartment(
                id,
                request.getDepartmentId(),
                admin.getId()
        );

        return ResponseEntity.ok(
                issueService.toIssueResponse(issue)
        );
    }

    @PatchMapping("/{id}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<IssueResponse> assignOfficer(
            @PathVariable Long id,
            @RequestBody OfficerAssignmentRequest request
    ) {
        Issue issue = issueService.assignOfficer(
                id,
                request.getOfficerId()
        );

        return ResponseEntity.ok(
                issueService.toIssueResponse(issue)
        );
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public ResponseEntity<IssueResponse> updateIssueStatus(
            @PathVariable Long id,
            @RequestBody IssueStatusUpdateRequest request,
            Authentication authentication
    ) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Authenticated user not found"));

        IssueStatus newStatus;

        try {
            newStatus = IssueStatus.valueOf(
                    request.getStatus().toUpperCase()
            );
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(
                    "Invalid status: " + request.getStatus()
            );
        }

        Issue issue = issueService.updateIssueStatus(
                id,
                newStatus,
                request.getRemarks(),
                user.getId()
        );

        return ResponseEntity.ok(
                issueService.toIssueResponse(issue)
        );
    }

    @PatchMapping("/{id}/verify")
    @PreAuthorize("hasRole('CITIZEN')")
    public ResponseEntity<IssueResponse> verifyIssue(
            @PathVariable Long id,
            Authentication authentication
    ) {
        User citizen = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("Authenticated user not found"));

        Issue issue = issueService.verifyIssue(
                id,
                citizen.getId()
        );

        return ResponseEntity.ok(
                issueService.toIssueResponse(issue)
        );
    }

    @PatchMapping("/{id}/reopen")
    @PreAuthorize("hasRole('CITIZEN')")
    public ResponseEntity<IssueResponse> reopenIssue(
            @PathVariable Long id,
            @RequestBody IssueStatusUpdateRequest request,
            Authentication authentication
    ) {
        User citizen = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("Authenticated user not found"));

        Issue issue = issueService.reopenIssue(
                id,
                citizen.getId(),
                request.getRemarks()
        );

        return ResponseEntity.ok(
                issueService.toIssueResponse(issue)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<IssueResponse> getIssueById(
            @PathVariable Long id,
            Authentication authentication
    ) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Authenticated user not found"));

        Issue issue = issueService.getIssueByIdForUser(
                id,
                user.getId()
        );

        return ResponseEntity.ok(
                issueService.toIssueResponse(issue)
        );
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<StatusHistoryResponse>> getIssueHistory(
            @PathVariable Long id
    ) {
        List<StatusHistoryResponse> history =
                statusHistoryService.getIssueHistory(id)
                        .stream()
                        .map(this::toStatusHistoryResponse)
                        .toList();

        return ResponseEntity.ok(history);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public ResponseEntity<List<IssueResponse>> getAllIssues() {

        List<IssueResponse> issues = issueService.getAllIssues()
                .stream()
                .map(issueService::toIssueResponse)
                .toList();

        return ResponseEntity.ok(issues);
    }

    private StatusHistoryResponse toStatusHistoryResponse(StatusHistory history) {

        StatusHistoryResponse response = new StatusHistoryResponse();

        response.setId(history.getId());
        response.setOldStatus(
                history.getOldStatus() != null
                        ? history.getOldStatus().name()
                        : null
        );
        response.setNewStatus(history.getNewStatus().name());
        response.setChangedBy(history.getChangedBy().getId());
        response.setChangedByName(history.getChangedBy().getName());
        response.setRemarks(history.getRemarks());
        response.setChangedAt(history.getChangedAt());

        return response;
    }
}