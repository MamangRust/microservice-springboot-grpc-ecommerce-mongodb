package com.sanedge.ecommerce.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class GatewayApplicationTests {

    @Test
    void contextLoads() {
        // fails if any bean (controllers, security, gRPC client config) cannot be wired
    }
}
