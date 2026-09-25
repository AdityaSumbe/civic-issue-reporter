package com.aditya.civic_issue_reporter.dto;

import jakarta.validation.constraints.NotNull;

public class OfficerAssignmentRequest {

    @NotNull(message = "Officer ID is required")
    private Long officerId;

    public Long getOfficerId() {
        return officerId;
    }

    public void setOfficerId(Long officerId) {
        this.officerId = officerId;
    }
}