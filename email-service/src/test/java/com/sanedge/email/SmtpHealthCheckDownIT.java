package com.sanedge.email;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

/**
 * Integration test for the dependency-aware readiness endpoint with an
 * UNAVAILABLE SMTP server: {@code /actuator/health/readiness} must report
 * {@code 503} and mark {@code smtp} as {@code DOWN} when {@code spring.mail.port}
 * points at a closed port.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "smtp.health.enabled=true",
        "spring.mail.host=127.0.0.1",
        "spring.mail.port=59998",
        "smtp.health.timeout-ms=1000",
        "management.endpoint.health.probes.enabled=true"
})
class SmtpHealthCheckDownIT extends AbstractKafkaRedisIT {

    @LocalServerPort
    int port;

    @Test
    void readinessReportsDownWhenSmtpUnreachable() throws Exception {
        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/actuator/health/readiness"))
                        .GET().build(),
                        HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(503);
        assertThat(response.body())
                .contains("\"smtp\"")
                .contains("\"DOWN\"");
    }
}
