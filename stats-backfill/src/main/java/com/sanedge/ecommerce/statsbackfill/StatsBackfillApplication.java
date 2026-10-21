package com.sanedge.ecommerce.statsbackfill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StatsBackfillApplication {

    public static void main(String[] args) {
        // One-shot job: web stack disabled, runs the CommandLineRunner then exits.
        System.setProperty("spring.main.web-application-type", "none");
        SpringApplication.run(StatsBackfillApplication.class, args);
    }
}
