package com.aditya.civic_issue_reporter.repository;

import com.aditya.civic_issue_reporter.entity.StatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatusHistoryRepository extends JpaRepository<StatusHistory, Long> {

    List<StatusHistory> findByIssueIdOrderByChangedAtAsc(Long issueId);
}