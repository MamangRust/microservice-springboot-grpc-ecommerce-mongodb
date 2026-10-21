package com.sanedge.email;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

/**
 * Integration test for the dependency-aware readiness endpoint against a REAL
 * (fake, protocol-correct) SMTP server: {@code /actuator/health/readiness} must
 * report {@code 200} and mark {@code smtp} as {@code UP}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "smtp.health.enabled=true",
        "spring.mail.host=127.0.0.1",
        "spring.mail.port=" + FakeSmtpServer.SMTP_PORT,
        "management.endpoint.health.probes.enabled=true"
})
class SmtpHealthCheckIT extends AbstractKafkaRedisIT {

    private static FakeSmtpServer smtp;

    @BeforeAll
    static void startSmtp() {
        smtp = new FakeSmtpServer();
    }

    @AfterAll
    static void stopSmtp() {
        if (smtp != null) {
            smtp.close();
        }
    }

    @LocalServerPort
    int port;

    @Test
    void readinessReportsUpWhenSmtpReachable() throws Exception {
        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/actuator/health/readiness"))
                        .GET().build(),
                        HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body())
                .contains("\"smtp\"")
                .contains("\"UP\"");
    }
}
