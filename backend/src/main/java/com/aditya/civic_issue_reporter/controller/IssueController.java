package com.aditya.civic_issue_reporter.controller;

import com.aditya.civic_issue_reporter.dto.IssueCreateRequest;
import com.aditya.civic_issue_reporter.entity.Issue;
import com.aditya.civic_issue_reporter.dto.IssueResponse;
import com.aditya.civic_issue_reporter.entity.User;
import com.aditya.civic_issue_reporter.repository.UserRepository;
import com.aditya.civic_issue_reporter.services.IssueService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.aditya.civic_issue_reporter.dto.DepartmentAssignmentRequest;
import com.aditya.civic_issue_reporter.dto.OfficerAssignmentRequest;

import java.util.List;

@RestController
@RequestMapping("/api/issues")
public class IssueController {

    private final IssueService issueService;
    private final UserRepository userRepository;

    public IssueController(
            IssueService issueService,
            UserRepository userRepository
    ) {
        this.issueService = issueService;
        this.userRepository = userRepository;
    }

    @PostMapping
    @PreAuthorize("hasRole('CITIZEN')")
    public ResponseEntity<IssueResponse> createIssue(
            @RequestBody IssueCreateRequest request,
            Authentication authentication
    ) {
        User citizen = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        Issue issue = issueService.createIssue(request, citizen.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(issueService.toIssueResponse(issue));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CITIZEN')")
    public ResponseEntity<List<IssueResponse>> getMyIssues(
            Authentication authentication
    ) {
        User citizen = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        return ResponseEntity.ok(
                issueService.getIssuesByCitizen(citizen.getId())
        );
    }

    @PatchMapping("/{id}/department")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<IssueResponse> assignDepartment(
            @PathVariable Long id,
            @RequestBody DepartmentAssignmentRequest request
    ) {
        Issue issue = issueService.assignDepartment(
                id,
                request.getDepartmentId()
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

    @GetMapping("/{id}")
    public ResponseEntity<IssueResponse> getIssueById(@PathVariable Long id) {
        Issue issue = issueService.getIssueById(id);

        return ResponseEntity.ok(issueService.toIssueResponse(issue));
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


}