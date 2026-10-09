package com.aditya.civic_issue_reporter.dto;

public class CategoryIssueCountResponse {

    private Long categoryId;
    private String categoryName;
    private long issueCount;

    public CategoryIssueCountResponse() {
    }

    public CategoryIssueCountResponse(
            Long categoryId,
            String categoryName,
            long issueCount
    ) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.issueCount = issueCount;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public long getIssueCount() {
        return issueCount;
    }

    public void setIssueCount(long issueCount) {
        this.issueCount = issueCount;
    }
}