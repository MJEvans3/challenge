package org.light.challenge.model;

/**
 * Represents the action to be taken when a workflow rule matches.
 * Contains information about who to notify and how.
 */
public class ApprovalAction {
    private final ApproverRole approverRole;
    private final NotificationChannel channel;
    private final String message;

    public ApprovalAction(ApproverRole approverRole, NotificationChannel channel, String message) {
        this.approverRole = approverRole;
        this.channel = channel;
        this.message = message;
    }

    public ApproverRole getApproverRole() {
        return approverRole;
    }

    public NotificationChannel getChannel() {
        return channel;
    }

    public String getMessage() {
        return message;
    }

    /**
     * Executes the approval action by sending the notification.
     * In a real system, this would integrate with Slack/Email services.
     */
    public void execute() {
        System.out.println(String.format(
            "Sending approval request to %s via %s: %s",
            approverRole.getDisplayName(),
            channel.name(),
            message
        ));
    }

    @Override
    public String toString() {
        return "ApprovalAction{" +
                "approverRole=" + approverRole +
                ", channel=" + channel +
                ", message='" + message + '\'' +
                '}';
    }
}
