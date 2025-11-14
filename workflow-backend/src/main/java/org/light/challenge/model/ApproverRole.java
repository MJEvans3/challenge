package org.light.challenge.model;

/**
 * Defines the roles that can approve invoices.
 */
public enum ApproverRole {
    FINANCE_TEAM("Finance Team"),
    FINANCE_MANAGER("Finance Manager"),
    CFO("CFO"),
    CMO("CMO");

    private final String displayName;

    ApproverRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
