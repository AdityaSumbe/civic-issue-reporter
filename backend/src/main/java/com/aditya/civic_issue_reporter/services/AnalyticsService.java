package com.aditya.civic_issue_reporter.services;

import com.aditya.civic_issue_reporter.dto.IssueStatusSummaryResponse;
import com.aditya.civic_issue_reporter.entity.IssueStatus;
import com.aditya.civic_issue_reporter.repository.IssueRepository;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsService {

    private final IssueRepository issueRepository;

    public AnalyticsService(IssueRepository issueRepository) {
        this.issueRepository = issueRepository;
    }

    public IssueStatusSummaryResponse getIssueStatusSummary() {

        IssueStatusSummaryResponse response =
                new IssueStatusSummaryResponse();

        response.setTotal(issueRepository.count());
        response.setOpen(issueRepository.countByStatus(IssueStatus.OPEN));
        response.setAssigned(issueRepository.countByStatus(IssueStatus.ASSIGNED));
        response.setInProgress(issueRepository.countByStatus(IssueStatus.IN_PROGRESS));
        response.setResolved(issueRepository.countByStatus(IssueStatus.RESOLVED));
        response.setVerified(issueRepository.countByStatus(IssueStatus.VERIFIED));
        response.setRejected(issueRepository.countByStatus(IssueStatus.REJECTED));
        response.setReopened(issueRepository.countByStatus(IssueStatus.REOPENED));

        return response;
    }
}