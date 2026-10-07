package com.aditya.civic_issue_reporter.dto;

public class IssueStatusSummaryResponse {

    private long total;
    private long open;
    private long assigned;
    private long inProgress;
    private long resolved;
    private long verified;
    private long rejected;
    private long reopened;

    public IssueStatusSummaryResponse() {
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getOpen() {
        return open;
    }

    public void setOpen(long open) {
        this.open = open;
    }

    public long getAssigned() {
        return assigned;
    }

    public void setAssigned(long assigned) {
        this.assigned = assigned;
    }

    public long getInProgress() {
        return inProgress;
    }

    public void setInProgress(long inProgress) {
        this.inProgress = inProgress;
    }

    public long getResolved() {
        return resolved;
    }

    public void setResolved(long resolved) {
        this.resolved = resolved;
    }

    public long getVerified() {
        return verified;
    }

    public void setVerified(long verified) {
        this.verified = verified;
    }

    public long getRejected() {
        return rejected;
    }

    public void setRejected(long rejected) {
        this.rejected = rejected;
    }

    public long getReopened() {
        return reopened;
    }

    public void setReopened(long reopened) {
        this.reopened = reopened;
    }
}