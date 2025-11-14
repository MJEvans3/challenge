package org.light.challenge.rest;

import org.light.challenge.engine.WorkflowEngine;
import org.light.challenge.model.ApprovalAction;
import org.light.challenge.model.Invoice;
import org.light.challenge.repository.WorkflowRepository;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST resource for workflow execution.
 * Provides an endpoint to evaluate invoice approval workflows.
 */
@Path("/workflow")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WorkflowResource {
    
    private final WorkflowEngine engine;

    public WorkflowResource() {
        // Initialize repository and engine
        WorkflowRepository repository = new WorkflowRepository();
        this.engine = new WorkflowEngine(repository);
    }

    /**
     * Executes the workflow for an invoice and returns the approval action.
     * 
     * POST /workflow
     * Body: {
     *   "amount": 15000.00,
     *   "department": "marketing",
     *   "requiresManagerApproval": false
     * }
     * 
     * Returns: {
     *   "success": true,
     *   "approverRole": "CMO",
     *   "channel": "EMAIL",
     *   "message": "High value marketing invoice requires CMO approval"
     * }
     */
    @POST
    public Response executeWorkflow(WorkflowRequest request) {
        try {
            // Validate input
            if (request == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(WorkflowResponse.error("Request body is required"))
                        .build();
            }

            if (request.getAmount() < 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(WorkflowResponse.error("Amount must be non-negative"))
                        .build();
            }

            if (request.getDepartment() == null || request.getDepartment().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(WorkflowResponse.error("Department is required"))
                        .build();
            }

            // Create invoice from request
            Invoice invoice = new Invoice(
                request.getAmount(),
                request.getDepartment(),
                request.isRequiresManagerApproval()
            );

            // Evaluate workflow
            ApprovalAction action = engine.evaluateWorkflow(invoice);

            // Execute the action (print to console)
            action.execute();

            // Build response
            WorkflowResponse response = new WorkflowResponse(
                action.getApproverRole().name(),
                action.getChannel().name(),
                action.getMessage()
            );

            return Response.ok(response).build();

        } catch (WorkflowEngine.WorkflowException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(WorkflowResponse.error(e.getMessage()))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(WorkflowResponse.error("Internal server error: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Health check endpoint.
     */
    @GET
    @Path("/health")
    public Response health() {
        return Response.ok("{\"status\": \"healthy\"}").build();
    }
}
