package com.aditya.civic_issue_reporter.repository;

import com.aditya.civic_issue_reporter.entity.Issue;
import com.aditya.civic_issue_reporter.entity.IssueStatus;
import com.aditya.civic_issue_reporter.entity.IssuePriority;
import org.springframework.data.jpa.domain.Specification;

public class IssueSpecification {

    public static Specification<Issue> hasStatus(IssueStatus status) {
        return (root, query, criteriaBuilder) ->
                status == null
                        ? null
                        : criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Issue> hasPriority(IssuePriority priority) {
        return (root, query, criteriaBuilder) ->
                priority == null
                        ? null
                        : criteriaBuilder.equal(root.get("priority"), priority);
    }

    public static Specification<Issue> hasCategory(Long categoryId) {
        return (root, query, criteriaBuilder) ->
                categoryId == null
                        ? null
                        : criteriaBuilder.equal(
                        root.get("category").get("id"),
                        categoryId
                );
    }

    public static Specification<Issue> hasDepartment(Long departmentId) {
        return (root, query, criteriaBuilder) ->
                departmentId == null
                        ? null
                        : criteriaBuilder.equal(
                        root.get("department").get("id"),
                        departmentId
                );
    }
}