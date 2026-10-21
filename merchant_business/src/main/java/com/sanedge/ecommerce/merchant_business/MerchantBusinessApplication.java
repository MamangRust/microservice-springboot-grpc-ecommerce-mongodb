package com.sanedge.ecommerce.merchant_business;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.sanedge.ecommerce")
public class MerchantBusinessApplication {

    public static void main(String[] args) {
        SpringApplication.run(MerchantBusinessApplication.class, args);
    }
}
