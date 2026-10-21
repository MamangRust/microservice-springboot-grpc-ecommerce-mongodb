package com.sanedge.ecommerce.banner.config;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.sanedge.ecommerce.banner.models.Banner;
import com.sanedge.ecommerce.banner.repository.BannerCommandRepository;
import com.sanedge.ecommerce.banner.repository.BannerQueryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DatabaseSeeder implements CommandLineRunner {

    private final BannerQueryRepository bannerQueryRepository;
    private final BannerCommandRepository bannerCommandRepository;

    @Override
    public void run(String... args) {
        try {
            seedBanners();
            log.info("Database seeding completed successfully.");
        } catch (Exception e) {
            log.error("Error during database seeding: {}", e.getMessage());
        }
    }

    private void seedBanners() {
        if (bannerQueryRepository.count() == 0) {
            Banner promo = new Banner();
            promo.setName("Promo Diskon 50%");
            promo.setStartDate(LocalDate.parse("2025-09-28"));
            promo.setEndDate(LocalDate.parse("2025-10-28"));
            promo.setStartTime(LocalTime.parse("08:00"));
            promo.setEndTime(LocalTime.parse("22:00"));
            promo.setIsActive(true);

            Banner flashSale = new Banner();
            flashSale.setName("Flash Sale Akhir Pekan");
            flashSale.setStartDate(LocalDate.parse("2025-10-04"));
            flashSale.setEndDate(LocalDate.parse("2025-10-05"));
            flashSale.setStartTime(LocalTime.parse("10:00"));
            flashSale.setEndTime(LocalTime.parse("23:59"));
            flashSale.setIsActive(true);

            bannerCommandRepository.saveAll(Arrays.asList(promo, flashSale));
            log.info("Banners seeded.");
        }
    }
}
