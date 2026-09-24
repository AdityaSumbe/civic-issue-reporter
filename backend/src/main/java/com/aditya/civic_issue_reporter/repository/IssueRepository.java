package com.aditya.civic_issue_reporter.repository;

import com.aditya.civic_issue_reporter.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IssueRepository extends JpaRepository<Issue, Long> {

    Optional<Issue> findByIssueNumber(String issueNumber);

    boolean existsByIssueNumber(String issueNumber);
}