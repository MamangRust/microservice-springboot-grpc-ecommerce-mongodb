package com.sanedge.ecommerce.product.config;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.sanedge.ecommerce.product.models.Product;
import com.sanedge.ecommerce.product.repository.ProductCommandRepository;
import com.sanedge.ecommerce.product.repository.ProductQueryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DatabaseSeeder implements CommandLineRunner {

    private final ProductQueryRepository productQueryRepository;
    private final ProductCommandRepository productCommandRepository;

    @Override
    public void run(String... args) {
        try {
            seedProducts();
            log.info("Database seeding completed successfully.");
        } catch (Exception e) {
            log.error("Error during database seeding: {}", e.getMessage());
        }
    }

    private void seedProducts() {
        if (productQueryRepository.count() != 0) {
            return;
        }

        Product smartphone = new Product();
        smartphone.setMerchantId(1);
        smartphone.setCategoryId(1);
        smartphone.setName("Smartphone X");
        smartphone.setDescription("Latest smartphone with advanced features");
        smartphone.setPrice(12000000);
        smartphone.setCountInStock(50);
        smartphone.setBrand("BrandX");
        smartphone.setWeight(200);
        smartphone.setRating(4.5f);
        smartphone.setSlugProduct("smartphone-x");
        smartphone.setImageProduct("http://example.com/smartphone_x.jpg");

        Product laptop = new Product();
        laptop.setMerchantId(1);
        laptop.setCategoryId(1);
        laptop.setName("Laptop Pro");
        laptop.setDescription("High performance laptop for professionals");
        laptop.setPrice(20000000);
        laptop.setCountInStock(20);
        laptop.setBrand("BrandY");
        laptop.setWeight(1500);
        laptop.setRating(4.8f);
        laptop.setSlugProduct("laptop-pro");
        laptop.setImageProduct("http://example.com/laptop_pro.jpg");

        Product headphone = new Product();
        headphone.setMerchantId(1);
        headphone.setCategoryId(1);
        headphone.setName("Wireless Headphone");
        headphone.setDescription("Noise cancelling wireless headphone");
        headphone.setPrice(2500000);
        headphone.setCountInStock(100);
        headphone.setBrand("BrandZ");
        headphone.setWeight(300);
        headphone.setRating(4.2f);
        headphone.setSlugProduct("wireless-headphone");
        headphone.setImageProduct("http://example.com/wireless_headphone.jpg");

        productCommandRepository.saveAll(Arrays.asList(smartphone, laptop, headphone));
        log.info("Products seeded.");
    }
}
