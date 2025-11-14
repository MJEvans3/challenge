package org.light.challenge.engine;

import org.light.challenge.model.ApprovalAction;
import org.light.challenge.model.Invoice;
import org.light.challenge.model.WorkflowRule;
import org.light.challenge.repository.WorkflowRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Main workflow engine that evaluates invoice approval rules.
 * Follows the Rule Engine pattern for dynamic, configurable workflows.
 */
public class WorkflowEngine {
    private final WorkflowRepository repository;

    public WorkflowEngine(WorkflowRepository repository) {
        this.repository = repository;
    }

    /**
     * Evaluates the workflow for the given invoice and returns the approval action(s).
     * Rules are evaluated in priority order, and the first matching rule wins.
     * 
     * @param invoice The invoice to evaluate
     * @return The approval action to take
     * @throws WorkflowException if no matching rule is found
     */
    public ApprovalAction evaluateWorkflow(Invoice invoice) throws WorkflowException {
        System.out.println("=== Evaluating Workflow ===");
        System.out.println("Invoice: " + invoice);
        System.out.println();

        List<WorkflowRule> rules = repository.getRulesByPriority();
        
        for (WorkflowRule rule : rules) {
            System.out.println("Checking Rule: " + rule.getDescription());
            System.out.println("  Conditions: " + 
                rule.getConditions().stream()
                    .map(c -> c.getDescription())
                    .collect(Collectors.joining(", ")));
            
            if (rule.matches(invoice)) {
                System.out.println("  ✓ MATCH FOUND!");
                System.out.println("  Action: " + rule.getAction());
                System.out.println();
                return rule.getAction();
            } else {
                System.out.println("  ✗ No match");
                System.out.println();
            }
        }

        throw new WorkflowException("No matching workflow rule found for invoice: " + invoice);
    }

    /**
     * Custom exception for workflow-related errors.
     */
    public static class WorkflowException extends Exception {
        public WorkflowException(String message) {
            super(message);
        }
    }
}
