package com.sanedge.migration;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import io.mongock.runner.springboot.EnableMongock;

/**
 * One-shot MongoDB migration runner for the e-commerce platform.
 *
 * <p>Runs as a non-web application: Mongock executes the
 * {@code @ChangeUnit}s at startup, then the context closes and the process
 * exits 0. In docker-compose / k8s this is wired as a job that other services
 * wait for ({@code service_completed_successfully} / an init-container gate)
 * so the collections and indexes exist before the domain services boot.
 *
 * <p>Also owns the Kafka email topology check
 * ({@link com.sanedge.migration.topology.TopologyValidator}): the runner is the
 * natural place to assert that the topics the modules publish to — and that
 * {@code email-service} consumes — exist, because the schema is only fully
 * provisioned when both halves are present.
 */
@EnableMongock
@SpringBootApplication
public class MigrationApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(MigrationApplication.class)
                .web(WebApplicationType.NONE)
                .run(args)
                .close();
    }
}
