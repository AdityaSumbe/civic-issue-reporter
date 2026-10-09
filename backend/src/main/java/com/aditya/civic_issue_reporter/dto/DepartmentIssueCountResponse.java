package com.aditya.civic_issue_reporter.dto;

public class DepartmentIssueCountResponse {

    private Long departmentId;
    private String departmentName;
    private long issueCount;

    public DepartmentIssueCountResponse() {
    }

    public DepartmentIssueCountResponse(
            Long departmentId,
            String departmentName,
            long issueCount
    ) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.issueCount = issueCount;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public long getIssueCount() {
        return issueCount;
    }

    public void setIssueCount(long issueCount) {
        this.issueCount = issueCount;
    }
}