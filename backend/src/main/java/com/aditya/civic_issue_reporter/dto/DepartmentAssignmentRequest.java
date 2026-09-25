package com.aditya.civic_issue_reporter.dto;

import jakarta.validation.constraints.NotNull;

public class DepartmentAssignmentRequest {

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }
}