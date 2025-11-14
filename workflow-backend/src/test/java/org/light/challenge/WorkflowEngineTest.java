package org.light.challenge;

import org.junit.Before;
import org.junit.Test;
import org.light.challenge.engine.WorkflowEngine;
import org.light.challenge.model.ApprovalAction;
import org.light.challenge.model.ApproverRole;
import org.light.challenge.model.Invoice;
import org.light.challenge.model.NotificationChannel;
import org.light.challenge.repository.WorkflowRepository;

import static org.junit.Assert.*;

/**
 * Tests for the WorkflowEngine.
 */
public class WorkflowEngineTest {
    
    private WorkflowEngine engine;

    @Before
    public void setUp() {
        WorkflowRepository repository = new WorkflowRepository();
        engine = new WorkflowEngine(repository);
    }

    @Test
    public void testHighValueMarketingInvoice() throws Exception {
        Invoice invoice = new Invoice(15000, "marketing", false);
        ApprovalAction action = engine.evaluateWorkflow(invoice);
        
        assertEquals(ApproverRole.CMO, action.getApproverRole());
        assertEquals(NotificationChannel.EMAIL, action.getChannel());
    }

    @Test
    public void testHighValueNonMarketingInvoice() throws Exception {
        Invoice invoice = new Invoice(15000, "engineering", false);
        ApprovalAction action = engine.evaluateWorkflow(invoice);
        
        assertEquals(ApproverRole.CFO, action.getApproverRole());
        assertEquals(NotificationChannel.SLACK, action.getChannel());
    }

    @Test
    public void testMediumValueWithManagerApproval() throws Exception {
        Invoice invoice = new Invoice(7000, "operations", true);
        ApprovalAction action = engine.evaluateWorkflow(invoice);
        
        assertEquals(ApproverRole.FINANCE_MANAGER, action.getApproverRole());
        assertEquals(NotificationChannel.EMAIL, action.getChannel());
    }

    @Test
    public void testMediumValueWithoutManagerApproval() throws Exception {
        Invoice invoice = new Invoice(7000, "operations", false);
        ApprovalAction action = engine.evaluateWorkflow(invoice);
        
        assertEquals(ApproverRole.FINANCE_TEAM, action.getApproverRole());
        assertEquals(NotificationChannel.SLACK, action.getChannel());
    }

    @Test
    public void testLowValueInvoice() throws Exception {
        Invoice invoice = new Invoice(3000, "hr", false);
        ApprovalAction action = engine.evaluateWorkflow(invoice);
        
        assertEquals(ApproverRole.FINANCE_TEAM, action.getApproverRole());
        assertEquals(NotificationChannel.SLACK, action.getChannel());
    }
}
