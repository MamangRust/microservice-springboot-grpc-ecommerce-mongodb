package com.sanedge.ecommerce.banner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.sanedge.ecommerce")
public class BannerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BannerApplication.class, args);
    }
}
