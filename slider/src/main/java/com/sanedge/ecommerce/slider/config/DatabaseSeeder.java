package com.sanedge.ecommerce.slider.config;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.sanedge.ecommerce.slider.models.Slider;
import com.sanedge.ecommerce.slider.repository.SliderCommandRepository;
import com.sanedge.ecommerce.slider.repository.SliderQueryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DatabaseSeeder implements CommandLineRunner {

    private final SliderQueryRepository sliderQueryRepository;
    private final SliderCommandRepository sliderCommandRepository;

    @Override
    public void run(String... args) {
        try {
            seedSliders();
            log.info("Database seeding completed successfully.");
        } catch (Exception e) {
            log.error("Error during database seeding: {}", e.getMessage());
        }
    }

    private void seedSliders() {
        if (sliderQueryRepository.count() == 0) {
            Slider promoAkhirTahun = new Slider();
            promoAkhirTahun.setName("Promo Akhir Tahun");
            promoAkhirTahun.setImage("static/slider/promo_akhir_tahun.jpg");

            Slider promoRamadan = new Slider();
            promoRamadan.setName("Promo Ramadan");
            promoRamadan.setImage("static/slider/promo_ramadan.jpg");

            sliderCommandRepository.saveAll(Arrays.asList(promoAkhirTahun, promoRamadan));
            log.info("Sliders seeded.");
        }
    }
}
