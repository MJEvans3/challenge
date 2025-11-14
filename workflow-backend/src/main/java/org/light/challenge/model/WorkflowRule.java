package org.light.challenge.model;

import org.light.challenge.condition.Condition;
import java.util.List;

/**
 * Represents a single workflow rule with conditions and an action.
 * Rules are evaluated in priority order (lower number = higher priority).
 */
public class WorkflowRule {
    private final String id;
    private final int priority;
    private final List<Condition> conditions;
    private final ApprovalAction action;
    private final String description;

    public WorkflowRule(String id, int priority, List<Condition> conditions, 
                       ApprovalAction action, String description) {
        this.id = id;
        this.priority = priority;
        this.conditions = conditions;
        this.action = action;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public int getPriority() {
        return priority;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public ApprovalAction getAction() {
        return action;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Evaluates if this rule matches the given invoice.
     * All conditions must be satisfied (AND logic).
     */
    public boolean matches(Invoice invoice) {
        return conditions.stream()
                .allMatch(condition -> condition.evaluate(invoice));
    }

    @Override
    public String toString() {
        return "WorkflowRule{" +
                "id='" + id + '\'' +
                ", priority=" + priority +
                ", description='" + description + '\'' +
                ", conditions=" + conditions.size() +
                '}';
    }
}
