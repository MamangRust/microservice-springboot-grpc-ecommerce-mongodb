package com.sanedge.ecommerce.cart.config;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.sanedge.ecommerce.cart.models.Cart;
import com.sanedge.ecommerce.cart.repository.CartCommandRepository;
import com.sanedge.ecommerce.cart.repository.CartQueryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DatabaseSeeder implements CommandLineRunner {

    private final CartQueryRepository cartQueryRepository;
    private final CartCommandRepository cartCommandRepository;

    @Override
    public void run(String... args) {
        try {
            seedCarts();
            log.info("Database seeding completed successfully.");
        } catch (Exception e) {
            log.error("Error during database seeding: {}", e.getMessage());
        }
    }

    private void seedCarts() {
        if (cartQueryRepository.count() == 0) {
            Cart first = new Cart();
            first.setUserId(1);
            first.setProductId(101);
            first.setName("Product 101");
            first.setPrice(100);
            first.setImage("default.png");
            first.setQuantity(2);
            first.setWeight(100);

            Cart second = new Cart();
            second.setUserId(1);
            second.setProductId(102);
            second.setName("Product 102");
            second.setPrice(150);
            second.setImage("default.png");
            second.setQuantity(1);
            second.setWeight(200);

            cartCommandRepository.saveAll(Arrays.asList(first, second));
            log.info("Carts seeded.");
        }
    }
}
