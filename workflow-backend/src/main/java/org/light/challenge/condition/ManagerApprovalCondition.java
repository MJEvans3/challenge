package org.light.challenge.condition;

import org.light.challenge.model.Invoice;

/**
 * Condition that evaluates if manager approval is required.
 */
public class ManagerApprovalCondition implements Condition {
    private final boolean expectedValue;

    public ManagerApprovalCondition(boolean expectedValue) {
        this.expectedValue = expectedValue;
    }

    @Override
    public boolean evaluate(Invoice invoice) {
        return invoice.isRequiresManagerApproval() == expectedValue;
    }

    @Override
    public String getDescription() {
        return String.format("Requires Manager Approval == %s", expectedValue);
    }

    @Override
    public String toString() {
        return getDescription();
    }
}
