package org.light.challenge.model;

/**
 * Represents an invoice that needs approval.
 * Contains all the information needed to evaluate workflow rules.
 */
public class Invoice {
    private final double amount;
    private final String department;
    private final boolean requiresManagerApproval;

    public Invoice(double amount, String department, boolean requiresManagerApproval) {
        this.amount = amount;
        this.department = department;
        this.requiresManagerApproval = requiresManagerApproval;
    }

    public double getAmount() {
        return amount;
    }

    public String getDepartment() {
        return department;
    }

    public boolean isRequiresManagerApproval() {
        return requiresManagerApproval;
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "amount=" + amount +
                ", department='" + department + '\'' +
                ", requiresManagerApproval=" + requiresManagerApproval +
                '}';
    }
}
