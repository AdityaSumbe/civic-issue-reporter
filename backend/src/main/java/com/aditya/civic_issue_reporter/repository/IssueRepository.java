package com.aditya.civic_issue_reporter.repository;

import com.aditya.civic_issue_reporter.entity.Issue;
import com.aditya.civic_issue_reporter.entity.IssueStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IssueRepository extends JpaRepository<Issue, Long>, JpaSpecificationExecutor<Issue> {

    Optional<Issue> findByIssueNumber(String issueNumber);

    boolean existsByIssueNumber(String issueNumber);

    List<Issue> findByReportedById(Long userId);

    Page<Issue> findByReportedById(Long userId, Pageable pageable);

    Page<Issue> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrIssueNumberContainingIgnoreCase(
            String title,
            String description,
            String issueNumber,
            Pageable pageable
    );

    long countByStatus(IssueStatus status);

    @Query("SELECT i FROM Issue i WHERE i.dueAt < CURRENT_TIMESTAMP")
    Page<Issue> findOverdueIssues(Pageable pageable);
}