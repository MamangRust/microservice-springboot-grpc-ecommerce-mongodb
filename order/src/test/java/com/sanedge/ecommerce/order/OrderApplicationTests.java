package com.sanedge.ecommerce.order;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class OrderApplicationTests {

    @Test
    void contextLoads() {
        // fails if any bean (handlers, repositories, gRPC server/client config) cannot be wired
    }
}
