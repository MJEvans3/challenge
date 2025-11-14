package org.light.challenge.condition;

import org.light.challenge.model.Invoice;

/**
 * Condition that evaluates if invoice department matches the expected value.
 */
public class DepartmentCondition implements Condition {
    private final String expectedDepartment;

    public DepartmentCondition(String expectedDepartment) {
        this.expectedDepartment = expectedDepartment;
    }

    @Override
    public boolean evaluate(Invoice invoice) {
        if (invoice.getDepartment() == null) {
            return false;
        }
        return invoice.getDepartment().equalsIgnoreCase(expectedDepartment);
    }

    @Override
    public String getDescription() {
        return String.format("Department == '%s'", expectedDepartment);
    }

    @Override
    public String toString() {
        return getDescription();
    }
}
