package org.light.challenge.condition;

/**
 * Defines comparison operators for numeric conditions.
 */
public enum ComparisonOperator {
    GREATER_THAN(">"),
    GREATER_THAN_OR_EQUAL(">="),
    LESS_THAN("<"),
    LESS_THAN_OR_EQUAL("<="),
    EQUAL("=="),
    NOT_EQUAL("!=");

    private final String symbol;

    ComparisonOperator(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    /**
     * Compares two double values using this operator.
     */
    public boolean compare(double value1, double value2) {
        switch (this) {
            case GREATER_THAN:
                return value1 > value2;
            case GREATER_THAN_OR_EQUAL:
                return value1 >= value2;
            case LESS_THAN:
                return value1 < value2;
            case LESS_THAN_OR_EQUAL:
                return value1 <= value2;
            case EQUAL:
                return Math.abs(value1 - value2) < 0.001; // Handle floating point comparison
            case NOT_EQUAL:
                return Math.abs(value1 - value2) >= 0.001;
            default:
                throw new IllegalStateException("Unknown operator: " + this);
        }
    }
}
