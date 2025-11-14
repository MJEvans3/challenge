package org.light.challenge.condition;

import org.light.challenge.model.Invoice;

/**
 * Condition that evaluates invoice amount against a threshold.
 */
public class AmountCondition implements Condition {
    private final ComparisonOperator operator;
    private final double threshold;

    public AmountCondition(ComparisonOperator operator, double threshold) {
        this.operator = operator;
        this.threshold = threshold;
    }

    @Override
    public boolean evaluate(Invoice invoice) {
        return operator.compare(invoice.getAmount(), threshold);
    }

    @Override
    public String getDescription() {
        return String.format("Amount %s %.2f", operator.getSymbol(), threshold);
    }

    @Override
    public String toString() {
        return getDescription();
    }
}
