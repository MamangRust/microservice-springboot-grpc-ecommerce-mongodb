package com.sanedge.ecommerce.merchant_detail;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class MerchantDetailApplicationTests {

    @Test
    void contextLoads() {
        // fails if any bean (handlers, repositories, gRPC server/client config) cannot be wired
    }
}
