package org.light.challenge.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response DTO for workflow execution.
 */
public class WorkflowResponse {
    @JsonProperty("success")
    private boolean success;

    @JsonProperty("approverRole")
    private String approverRole;

    @JsonProperty("channel")
    private String channel;

    @JsonProperty("message")
    private String message;

    @JsonProperty("error")
    private String error;

    // Default constructor for Jackson
    public WorkflowResponse() {
    }

    // Success response constructor
    public WorkflowResponse(String approverRole, String channel, String message) {
        this.success = true;
        this.approverRole = approverRole;
        this.channel = channel;
        this.message = message;
    }

    // Error response constructor
    public static WorkflowResponse error(String errorMessage) {
        WorkflowResponse response = new WorkflowResponse();
        response.success = false;
        response.error = errorMessage;
        return response;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getApproverRole() {
        return approverRole;
    }

    public void setApproverRole(String approverRole) {
        this.approverRole = approverRole;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
