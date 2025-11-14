package org.light.challenge.repository;

import org.light.challenge.condition.*;
import org.light.challenge.model.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory repository for workflow rules.
 * Initializes with the rules from the workflow diagram.
 */
public class WorkflowRepository {
    private final List<WorkflowRule> rules;

    public WorkflowRepository() {
        this.rules = new ArrayList<>();
        initializeWorkflowRules();
    }

    /**
     * Initialize workflow rules based on the diagram.
     * 
     * The workflow logic:
     * 1. If amount > 10000:
     *    - If marketing department → CMO via email
     *    - Otherwise → CFO via Slack
     * 
     * 2. If amount > 5000 (but <= 10000):
     *    - If requires manager approval → Finance Manager via email
     *    - Otherwise → Finance Team via Slack
     * 
     * 3. If amount <= 5000:
     *    - Finance Team via Slack
     */
    private void initializeWorkflowRules() {
        // PRIORITY 1: Amount > 10000 AND Department = Marketing → CMO via Email
        rules.add(new WorkflowRule(
            "rule-1",
            1,
            Arrays.asList(
                new AmountCondition(ComparisonOperator.GREATER_THAN, 10000),
                new DepartmentCondition("marketing")
            ),
            new ApprovalAction(
                ApproverRole.CMO,
                NotificationChannel.EMAIL,
                "High value marketing invoice requires CMO approval"
            ),
            "Amount > 10000 AND Marketing Department → CMO via Email"
        ));

        // PRIORITY 2: Amount > 10000 AND Department != Marketing → CFO via Slack
        rules.add(new WorkflowRule(
            "rule-2",
            2,
            Arrays.asList(
                new AmountCondition(ComparisonOperator.GREATER_THAN, 10000)
                // No department condition = applies to all non-marketing (handled by priority)
            ),
            new ApprovalAction(
                ApproverRole.CFO,
                NotificationChannel.SLACK,
                "High value invoice requires CFO approval"
            ),
            "Amount > 10000 → CFO via Slack"
        ));

        // PRIORITY 3: Amount > 5000 AND <= 10000 AND Requires Manager Approval → Finance Manager via Email
        rules.add(new WorkflowRule(
            "rule-3",
            3,
            Arrays.asList(
                new AmountCondition(ComparisonOperator.GREATER_THAN, 5000),
                new AmountCondition(ComparisonOperator.LESS_THAN_OR_EQUAL, 10000),
                new ManagerApprovalCondition(true)
            ),
            new ApprovalAction(
                ApproverRole.FINANCE_MANAGER,
                NotificationChannel.EMAIL,
                "Invoice requires Finance Manager approval"
            ),
            "5000 < Amount <= 10000 AND Requires Manager Approval → Finance Manager via Email"
        ));

        // PRIORITY 4: Amount > 5000 AND <= 10000 AND Does NOT Require Manager Approval → Finance Team via Slack
        rules.add(new WorkflowRule(
            "rule-4",
            4,
            Arrays.asList(
                new AmountCondition(ComparisonOperator.GREATER_THAN, 5000),
                new AmountCondition(ComparisonOperator.LESS_THAN_OR_EQUAL, 10000),
                new ManagerApprovalCondition(false)
            ),
            new ApprovalAction(
                ApproverRole.FINANCE_TEAM,
                NotificationChannel.SLACK,
                "Invoice can be approved by any Finance Team member"
            ),
            "5000 < Amount <= 10000 AND No Manager Approval Required → Finance Team via Slack"
        ));

        // PRIORITY 5: Amount <= 5000 → Finance Team via Slack (catch-all for small amounts)
        rules.add(new WorkflowRule(
            "rule-5",
            5,
            Arrays.asList(
                new AmountCondition(ComparisonOperator.LESS_THAN_OR_EQUAL, 5000)
            ),
            new ApprovalAction(
                ApproverRole.FINANCE_TEAM,
                NotificationChannel.SLACK,
                "Standard invoice approval by Finance Team"
            ),
            "Amount <= 5000 → Finance Team via Slack"
        ));

        System.out.println("Initialized " + rules.size() + " workflow rules");
    }

    /**
     * Returns all rules sorted by priority (ascending order).
     */
    public List<WorkflowRule> getRulesByPriority() {
        return rules.stream()
                .sorted(Comparator.comparingInt(WorkflowRule::getPriority))
                .collect(Collectors.toList());
    }

    /**
     * Returns a rule by ID.
     */
    public Optional<WorkflowRule> getRuleById(String id) {
        return rules.stream()
                .filter(rule -> rule.getId().equals(id))
                .findFirst();
    }

    /**
     * Adds a new rule to the repository.
     * In a real system, this would persist to a database.
     */
    public void addRule(WorkflowRule rule) {
        rules.add(rule);
    }

    /**
     * Returns all rules.
     */
    public List<WorkflowRule> getAllRules() {
        return new ArrayList<>(rules);
    }
}
