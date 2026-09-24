package com.aditya.civic_issue_reporter.services;

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

import java.util.List;
import java.util.UUID;

@Service
public class IssueService {

    private final IssueRepository issueRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public IssueService(IssueRepository issueRepository,
                        CategoryRepository categoryRepository,
                        UserRepository userRepository) {
        this.issueRepository = issueRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
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

    public List<Issue> getIssuesByCitizen(Long userId) {
        return issueRepository.findAll()
                .stream()
                .filter(issue ->
                        issue.getReportedBy().getId().equals(userId))
                .toList();
    }

    private String generateIssueNumber() {
        return "CIV-" + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
    }
}