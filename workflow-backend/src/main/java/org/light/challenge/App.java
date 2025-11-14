package org.light.challenge;

import com.fasterxml.jackson.databind.DeserializationFeature;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.light.challenge.rest.WorkflowResource;

/**
 * Main Dropwizard application class.
 */
public class App extends Application<Configuration> {

    public static void main(String[] args) throws Exception {
        new App().run(args);
    }

    @Override
    public String getName() {
        return "workflow-approval-system";
    }

    @Override
    public void initialize(Bootstrap<Configuration> bootstrap) {
        // Configure Jackson
        bootstrap.getObjectMapper().configure(
            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, 
            false
        );
    }

    @Override
    public void run(Configuration configuration, Environment environment) {
        // Register REST resources
        final WorkflowResource workflowResource = new WorkflowResource();
        environment.jersey().register(workflowResource);

        // Register health check
        environment.healthChecks().register("workflow", 
            new com.codahale.metrics.health.HealthCheck() {
                @Override
                protected Result check() {
                    return Result.healthy();
                }
            }
        );

        System.out.println("====================================");
        System.out.println("Workflow Approval System Started");
        System.out.println("====================================");
    }
}
