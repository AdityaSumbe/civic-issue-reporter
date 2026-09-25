package com.aditya.civic_issue_reporter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class IssueStatusUpdateRequest {

    @NotBlank(message = "Status is required")
    private String status;

    @Size(max = 1000, message = "Remarks must not exceed 1000 characters")
    private String remarks;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}