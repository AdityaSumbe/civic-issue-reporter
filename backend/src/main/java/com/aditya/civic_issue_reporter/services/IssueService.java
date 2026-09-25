package com.aditya.civic_issue_reporter.services;

import com.aditya.civic_issue_reporter.entity.Department;
import com.aditya.civic_issue_reporter.repository.DepartmentRepository;
import com.aditya.civic_issue_reporter.dto.IssueCreateRequest;
import com.aditya.civic_issue_reporter.entity.Category;
import com.aditya.civic_issue_reporter.entity.Issue;
import com.aditya.civic_issue_reporter.entity.IssuePriority;
import com.aditya.civic_issue_reporter.entity.IssueStatus;
import com.aditya.civic_issue_reporter.entity.User;
import com.aditya.civic_issue_reporter.repository.CategoryRepository;
import com.aditya.civic_issue_reporter.repository.IssueRepository;
import com.aditya.civic_issue_reporter.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.aditya.civic_issue_reporter.dto.IssueResponse;
import com.aditya.civic_issue_reporter.entity.UserRole;
import com.aditya.civic_issue_reporter.entity.StatusHistory;
import com.aditya.civic_issue_reporter.repository.StatusHistoryRepository;

import java.util.List;
import java.util.UUID;

@Service
public class IssueService {

    private final IssueRepository issueRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final StatusHistoryRepository statusHistoryRepository;

    public IssueService(IssueRepository issueRepository,
                        CategoryRepository categoryRepository,
                        UserRepository userRepository,
                        DepartmentRepository departmentRepository,
                        StatusHistoryRepository statusHistoryRepository) {
        this.issueRepository = issueRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.statusHistoryRepository = statusHistoryRepository;
    }

    public Issue createIssue(IssueCreateRequest request, Long userId) {

        User citizen = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found with id: " + userId));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new RuntimeException("Category not found with id: "
                                + request.getCategoryId()));

        Issue issue = new Issue();

        issue.setIssueNumber(generateIssueNumber());
        issue.setTitle(request.getTitle());
        issue.setDescription(request.getDescription());
        issue.setCategory(category);
        issue.setReportedBy(citizen);

        issue.setStatus(IssueStatus.OPEN);
        issue.setPriority(IssuePriority.MEDIUM);

        issue.setLatitude(request.getLatitude());
        issue.setLongitude(request.getLongitude());
        issue.setAddress(request.getAddress());
        issue.setImageUrl(request.getImageUrl());

        return issueRepository.save(issue);
    }

    public Issue getIssueById(Long id) {
        return issueRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Issue not found with id: " + id));
    }

    public Issue getIssueByIdForUser(Long issueId, Long userId) {

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() ->
                        new RuntimeException("Issue not found with id: " + issueId));

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found with id: " + userId));

        if (user.getRole() == UserRole.ADMIN) {
            return issue;
        }

        if (user.getRole() == UserRole.CITIZEN) {
            if (!issue.getReportedBy().getId().equals(userId)) {
                throw new RuntimeException(
                        "You are not authorized to view this issue"
                );
            }

            return issue;
        }

        if (user.getRole() == UserRole.OFFICER) {
            if (issue.getAssignedTo() == null
                    || !issue.getAssignedTo().getId().equals(userId)) {
                throw new RuntimeException(
                        "You are not authorized to view this issue"
                );
            }

            return issue;
        }

        throw new RuntimeException(
                "You are not authorized to view this issue"
        );
    }

    public List<Issue> getAllIssues() {
        return issueRepository.findAll();
    }

    public List<IssueResponse> getIssuesByCitizen(Long userId) {
        return issueRepository.findByReportedById(userId)
                .stream()
                .map(this::toIssueResponse)
                .toList();
    }

    private String generateIssueNumber() {
        return "CIV-" + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
    }

    public Issue assignDepartment(
            Long issueId,
            Long departmentId,
            Long changedByUserId
    ) {

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() ->
                        new RuntimeException("Issue not found with id: " + issueId));

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() ->
                        new RuntimeException("Department not found with id: " + departmentId));

        if (!department.isActive()) {
            throw new RuntimeException("Department is inactive");
        }

        User admin = userRepository.findById(changedByUserId)
                .orElseThrow(() ->
                        new RuntimeException("User not found with id: " + changedByUserId));

        if (admin.getRole() != UserRole.ADMIN) {
            throw new RuntimeException("Only an admin can assign departments");
        }

        IssueStatus oldStatus = issue.getStatus();

        issue.setDepartment(department);

        if (oldStatus == IssueStatus.OPEN) {
            issue.setStatus(IssueStatus.ASSIGNED);
        }

        Issue savedIssue = issueRepository.save(issue);

        if (oldStatus != savedIssue.getStatus()) {

            StatusHistory history = new StatusHistory();

            history.setIssue(savedIssue);
            history.setOldStatus(oldStatus);
            history.setNewStatus(savedIssue.getStatus());
            history.setChangedBy(admin);
            history.setRemarks(
                    "Department assigned: " + department.getName()
            );

            statusHistoryRepository.save(history);
        }

        return savedIssue;
    }

    public Issue assignOfficer(Long issueId, Long officerId) {

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() ->
                        new RuntimeException("Issue not found with id: " + issueId));

        User officer = userRepository.findById(officerId)
                .orElseThrow(() ->
                        new RuntimeException("User not found with id: " + officerId));

        if (officer.getRole() != UserRole.OFFICER) {
            throw new RuntimeException("Selected user is not an officer");
        }

        issue.setAssignedTo(officer);

        if (issue.getStatus() == IssueStatus.OPEN) {
            issue.setStatus(IssueStatus.ASSIGNED);
        }

        return issueRepository.save(issue);
    }

    public Issue updateIssueStatus(
            Long issueId,
            IssueStatus newStatus,
            String remarks,
            Long userId
    ) {

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() ->
                        new RuntimeException("Issue not found with id: " + issueId));

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found with id: " + userId));

        IssueStatus oldStatus = issue.getStatus();

        // Verify that the officer is assigned to this issue
        if (user.getRole() == UserRole.OFFICER) {

            if (issue.getAssignedTo() == null
                    || !issue.getAssignedTo().getId().equals(userId)) {

                throw new RuntimeException(
                        "You are not assigned to this issue"
                );
            }
        }

        // Validate status transition
        if (!isValidStatusTransition(oldStatus, newStatus)) {
            throw new RuntimeException(
                    "Invalid status transition: "
                            + oldStatus + " -> " + newStatus
            );
        }

        issue.setStatus(newStatus);

        if (newStatus == IssueStatus.RESOLVED) {
            issue.setResolvedAt(java.time.LocalDateTime.now());
        }

        Issue savedIssue = issueRepository.save(issue);

        // Create status history record
        StatusHistory history = new StatusHistory();

        history.setIssue(issue);
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setChangedBy(user);
        history.setRemarks(remarks);

        statusHistoryRepository.save(history);

        return savedIssue;
    }
    public Issue verifyIssue(Long issueId, Long citizenId) {

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() ->
                        new RuntimeException("Issue not found with id: " + issueId));

        User citizen = userRepository.findById(citizenId)
                .orElseThrow(() ->
                        new RuntimeException("User not found with id: " + citizenId));

        if (citizen.getRole() != UserRole.CITIZEN) {
            throw new RuntimeException("Only citizens can verify issues");
        }

        if (!issue.getReportedBy().getId().equals(citizenId)) {
            throw new RuntimeException(
                    "You can only verify your own reported issues"
            );
        }

        if (issue.getStatus() != IssueStatus.RESOLVED) {
            throw new RuntimeException(
                    "Only resolved issues can be verified"
            );
        }

        issue.setStatus(IssueStatus.VERIFIED);

        Issue savedIssue = issueRepository.save(issue);

        StatusHistory history = new StatusHistory();
        history.setIssue(savedIssue);
        history.setOldStatus(IssueStatus.RESOLVED);
        history.setNewStatus(IssueStatus.VERIFIED);
        history.setChangedBy(citizen);
        history.setRemarks("Issue verified by citizen");

        statusHistoryRepository.save(history);

        return savedIssue;
    }
    public Issue reopenIssue(Long issueId, Long citizenId, String remarks) {

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() ->
                        new RuntimeException("Issue not found with id: " + issueId));

        User citizen = userRepository.findById(citizenId)
                .orElseThrow(() ->
                        new RuntimeException("User not found with id: " + citizenId));

        if (citizen.getRole() != UserRole.CITIZEN) {
            throw new RuntimeException("Only citizens can reopen issues");
        }

        if (!issue.getReportedBy().getId().equals(citizenId)) {
            throw new RuntimeException(
                    "You can only reopen your own reported issues"
            );
        }

        if (issue.getStatus() != IssueStatus.RESOLVED) {
            throw new RuntimeException(
                    "Only resolved issues can be reopened"
            );
        }

        issue.setStatus(IssueStatus.REOPENED);
        issue.setResolvedAt(null);

        Issue savedIssue = issueRepository.save(issue);

        StatusHistory history = new StatusHistory();
        history.setIssue(savedIssue);
        history.setOldStatus(IssueStatus.RESOLVED);
        history.setNewStatus(IssueStatus.REOPENED);
        history.setChangedBy(citizen);
        history.setRemarks(remarks);

        statusHistoryRepository.save(history);

        return savedIssue;
    }



    private boolean isValidStatusTransition(
            IssueStatus oldStatus,
            IssueStatus newStatus
    ) {

        return switch (oldStatus) {

            case OPEN ->
                    newStatus == IssueStatus.ASSIGNED
                            || newStatus == IssueStatus.REJECTED;

            case ASSIGNED ->
                    newStatus == IssueStatus.IN_PROGRESS
                            || newStatus == IssueStatus.REJECTED;

            case IN_PROGRESS ->
                    newStatus == IssueStatus.RESOLVED;

            case RESOLVED ->
                    newStatus == IssueStatus.VERIFIED
                            || newStatus == IssueStatus.REOPENED;

            case REOPENED ->
                    newStatus == IssueStatus.IN_PROGRESS;

            case VERIFIED, REJECTED ->
                    false;
        };
    }

    public IssueResponse toIssueResponse(Issue issue) {
        IssueResponse response = new IssueResponse();

        response.setId(issue.getId());
        response.setIssueNumber(issue.getIssueNumber());
        response.setTitle(issue.getTitle());
        response.setDescription(issue.getDescription());

        response.setCategoryId(issue.getCategory().getId());
        response.setCategoryName(issue.getCategory().getName());

        if (issue.getDepartment() != null) {
            response.setDepartmentId(issue.getDepartment().getId());
            response.setDepartmentName(issue.getDepartment().getName());
        }

        if (issue.getAssignedTo() != null) {
            response.setAssignedToId(issue.getAssignedTo().getId());
            response.setAssignedToName(issue.getAssignedTo().getName());
        }

        response.setStatus(issue.getStatus());
        response.setPriority(issue.getPriority());

        response.setLatitude(issue.getLatitude());
        response.setLongitude(issue.getLongitude());
        response.setAddress(issue.getAddress());
        response.setImageUrl(issue.getImageUrl());

        response.setReportedById(issue.getReportedBy().getId());
        response.setReportedByName(issue.getReportedBy().getName());

        response.setCreatedAt(issue.getCreatedAt());
        response.setUpdatedAt(issue.getUpdatedAt());
        response.setResolvedAt(issue.getResolvedAt());

        return response;
    }
}