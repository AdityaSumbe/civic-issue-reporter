package com.aditya.civic_issue_reporter.services;

import com.aditya.civic_issue_reporter.entity.Issue;
import com.aditya.civic_issue_reporter.entity.IssueStatus;
import com.aditya.civic_issue_reporter.entity.StatusHistory;
import com.aditya.civic_issue_reporter.entity.User;
import com.aditya.civic_issue_reporter.repository.StatusHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatusHistoryService {

    private final StatusHistoryRepository statusHistoryRepository;

    public StatusHistoryService(StatusHistoryRepository statusHistoryRepository) {
        this.statusHistoryRepository = statusHistoryRepository;
    }

    public StatusHistory createHistory(Issue issue,
                                       IssueStatus oldStatus,
                                       IssueStatus newStatus,
                                       User changedBy,
                                       String remarks) {

        StatusHistory history = new StatusHistory();

        history.setIssue(issue);
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setChangedBy(changedBy);
        history.setRemarks(remarks);

        return statusHistoryRepository.save(history);
    }

    public List<StatusHistory> getIssueHistory(Long issueId) {
        return statusHistoryRepository
                .findByIssueIdOrderByChangedAtAsc(issueId);
    }
}