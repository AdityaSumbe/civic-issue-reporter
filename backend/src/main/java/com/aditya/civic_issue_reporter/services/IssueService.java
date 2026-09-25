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

import java.util.List;
import java.util.UUID;

@Service
public class IssueService {

    private final IssueRepository issueRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    public IssueService(IssueRepository issueRepository,
                        CategoryRepository categoryRepository,
                        UserRepository userRepository,
                        DepartmentRepository departmentRepository) {
        this.issueRepository = issueRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
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

    public Issue assignDepartment(Long issueId, Long departmentId) {

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() ->
                        new RuntimeException("Issue not found with id: " + issueId));

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() ->
                        new RuntimeException("Department not found with id: " + departmentId));

        if (!department.isActive()) {
            throw new RuntimeException("Department is inactive");
        }

        issue.setDepartment(department);

        if (issue.getStatus() == IssueStatus.OPEN) {
            issue.setStatus(IssueStatus.ASSIGNED);
        }

        return issueRepository.save(issue);
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