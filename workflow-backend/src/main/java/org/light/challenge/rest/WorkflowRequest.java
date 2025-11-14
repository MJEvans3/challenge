package org.light.challenge.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request DTO for workflow execution.
 */
public class WorkflowRequest {
    @JsonProperty("amount")
    private double amount;

    @JsonProperty("department")
    private String department;

    @JsonProperty("requiresManagerApproval")
    private boolean requiresManagerApproval;

    // Default constructor for Jackson
    public WorkflowRequest() {
    }

    public WorkflowRequest(double amount, String department, boolean requiresManagerApproval) {
        this.amount = amount;
        this.department = department;
        this.requiresManagerApproval = requiresManagerApproval;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public boolean isRequiresManagerApproval() {
        return requiresManagerApproval;
    }

    public void setRequiresManagerApproval(boolean requiresManagerApproval) {
        this.requiresManagerApproval = requiresManagerApproval;
    }
}
