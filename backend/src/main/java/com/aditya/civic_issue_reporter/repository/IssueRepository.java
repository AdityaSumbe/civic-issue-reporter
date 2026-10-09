package com.aditya.civic_issue_reporter.repository;

import com.aditya.civic_issue_reporter.dto.CategoryIssueCountResponse;
import com.aditya.civic_issue_reporter.entity.Issue;
import com.aditya.civic_issue_reporter.entity.IssueStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.aditya.civic_issue_reporter.dto.DepartmentIssueCountResponse;
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

    @Query("""
    SELECT new com.aditya.civic_issue_reporter.dto.CategoryIssueCountResponse(
        c.id,
        c.name,
        COUNT(i)
    )
    FROM Issue i
    JOIN i.category c
    GROUP BY c.id, c.name
    ORDER BY COUNT(i) DESC
    """)
    List<CategoryIssueCountResponse> countIssuesByCategory();

    @Query("""
    SELECT new com.aditya.civic_issue_reporter.dto.DepartmentIssueCountResponse(
        d.id,
        d.name,
        COUNT(i)
    )
    FROM Issue i
    LEFT JOIN i.department d
    GROUP BY d.id, d.name
    ORDER BY COUNT(i) DESC
    """)
    List<DepartmentIssueCountResponse> countIssuesByDepartment();
}