package org.light.challenge.condition;

import org.light.challenge.model.Invoice;

/**
 * Interface for workflow conditions that can be evaluated against an invoice.
 * This allows for dynamic, composable rule conditions.
 */
public interface Condition {
    /**
     * Evaluates the condition against the given invoice.
     * @param invoice The invoice to evaluate
     * @return true if the condition is satisfied, false otherwise
     */
    boolean evaluate(Invoice invoice);

    /**
     * Returns a human-readable description of this condition.
     */
    String getDescription();
}
