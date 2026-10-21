package com.sanedge.ecommerce.review.config;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.sanedge.ecommerce.review.models.Review;
import com.sanedge.ecommerce.review.repository.ReviewCommandRepository;
import com.sanedge.ecommerce.review.repository.ReviewQueryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DatabaseSeeder implements CommandLineRunner {

    private final ReviewQueryRepository reviewQueryRepository;
    private final ReviewCommandRepository reviewCommandRepository;

    @Override
    public void run(String... args) {
        try {
            seedReviews();
            log.info("Database seeding completed successfully.");
        } catch (Exception e) {
            log.error("Error during database seeding: {}", e.getMessage());
        }
    }

    private void seedReviews() {
        if (reviewQueryRepository.count() == 0) {
            Review first = new Review();
            first.setUserId(1);
            first.setProductId(1);
            first.setName("John Doe");
            first.setComment("Produk sangat bagus!");
            first.setRating(5);

            Review second = new Review();
            second.setUserId(2);
            second.setProductId(1);
            second.setName("Jane Smith");
            second.setComment("Pengiriman cepat, produk sesuai deskripsi.");
            second.setRating(4);

            reviewCommandRepository.saveAll(Arrays.asList(first, second));
            log.info("Reviews seeded.");
        }
    }
}
