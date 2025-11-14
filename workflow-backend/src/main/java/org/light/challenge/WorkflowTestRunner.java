package org.light.challenge;

import org.light.challenge.engine.WorkflowEngine;
import org.light.challenge.model.ApprovalAction;
import org.light.challenge.model.Invoice;
import org.light.challenge.repository.WorkflowRepository;

/**
 * Standalone test runner to quickly test the workflow engine.
 * Run this class to see the workflow evaluation in action.
 */
public class WorkflowTestRunner {
    
    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("   Workflow Engine Test Runner");
        System.out.println("===========================================\n");

        WorkflowRepository repository = new WorkflowRepository();
        WorkflowEngine engine = new WorkflowEngine(repository);

        // Test Case 1: High value marketing invoice
        testInvoice(engine, 15000, "marketing", false);
        
        // Test Case 2: High value non-marketing invoice
        testInvoice(engine, 15000, "engineering", false);
        
        // Test Case 3: Medium value with manager approval
        testInvoice(engine, 7000, "operations", true);
        
        // Test Case 4: Medium value without manager approval
        testInvoice(engine, 7000, "operations", false);
        
        // Test Case 5: Low value invoice
        testInvoice(engine, 3000, "hr", false);
        
        // Edge case: Exactly 10000
        testInvoice(engine, 10000, "sales", false);
        
        // Edge case: Exactly 5000
        testInvoice(engine, 5000, "sales", false);
    }

    private static void testInvoice(WorkflowEngine engine, double amount, 
                                   String department, boolean requiresManagerApproval) {
        Invoice invoice = new Invoice(amount, department, requiresManagerApproval);
        
        try {
            ApprovalAction action = engine.evaluateWorkflow(invoice);
            action.execute();
            System.out.println("-------------------------------------------\n");
        } catch (WorkflowEngine.WorkflowException e) {
            System.err.println("ERROR: " + e.getMessage());
            System.out.println("-------------------------------------------\n");
        }
    }
}
