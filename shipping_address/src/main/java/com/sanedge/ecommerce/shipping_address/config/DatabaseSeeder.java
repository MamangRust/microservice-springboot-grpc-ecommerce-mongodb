package com.sanedge.ecommerce.shipping_address.config;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.sanedge.ecommerce.shipping_address.models.ShippingAddress;
import com.sanedge.ecommerce.shipping_address.repository.ShippingAddressCommandRepository;
import com.sanedge.ecommerce.shipping_address.repository.ShippingAddressQueryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DatabaseSeeder implements CommandLineRunner {

    private final ShippingAddressQueryRepository shippingAddressQueryRepository;
    private final ShippingAddressCommandRepository shippingAddressCommandRepository;

    @Override
    public void run(String... args) {
        try {
            seedShippingAddresses();
            log.info("Database seeding completed successfully.");
        } catch (Exception e) {
            log.error("Error during database seeding: {}", e.getMessage());
        }
    }

    private void seedShippingAddresses() {
        if (shippingAddressQueryRepository.count() == 0) {
            ShippingAddress home = new ShippingAddress();
            home.setOrderId(1);
            home.setAlamat("Jl. Merdeka No.1");
            home.setProvinsi("DKI Jakarta");
            home.setNegara("Indonesia");
            home.setKota("Jakarta Pusat");
            home.setCourier("JNE");
            home.setShippingMethod("REG");
            home.setShippingCost(15000);

            ShippingAddress office = new ShippingAddress();
            office.setOrderId(2);
            office.setAlamat("Jl. Sudirman No.45");
            office.setProvinsi("Jawa Barat");
            office.setNegara("Indonesia");
            office.setKota("Bandung");
            office.setCourier("J&T");
            office.setShippingMethod("YES");
            office.setShippingCost(25000);

            shippingAddressCommandRepository.saveAll(Arrays.asList(home, office));
            log.info("Shipping addresses seeded.");
        }
    }
}
