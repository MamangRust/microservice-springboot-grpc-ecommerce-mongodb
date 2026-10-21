package com.sanedge.ecommerce.merchant.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.sanedge.ecommerce.merchant.models.Merchant;
import com.sanedge.ecommerce.merchant.repository.MerchantCommandRepository;
import com.sanedge.ecommerce.merchant.repository.MerchantQueryRepository;
import com.sanedge.ecommerce.pb.user.ApiResponseUser;
import com.sanedge.ecommerce.pb.user.FindByIdUserRequest;
import com.sanedge.ecommerce.pb.user.UserQueryServiceGrpc;

import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DatabaseSeeder implements CommandLineRunner {

    private static final Integer FALLBACK_OWNER_ID = 1;

    private final MerchantQueryRepository merchantQueryRepository;
    private final MerchantCommandRepository merchantCommandRepository;
    private final UserQueryServiceGrpc.UserQueryServiceBlockingStub userQueryServiceBlockingStub;

    @Override
    public void run(String... args) {
        try {
            if (merchantQueryRepository.count() > 0) {
                return;
            }

            Integer ownerId = resolveOwnerUserId();

            merchantCommandRepository.save(buildMerchant(ownerId, "Sanedge Official Store",
                    "Official store for Sanedge products", "Jakarta, Indonesia",
                    "contact@sanedge.com", "08123456789"));

            merchantCommandRepository.save(buildMerchant(ownerId, "Sanedge Digital Hub",
                    "Digital goods and services marketplace", "Bandung, Indonesia",
                    "digital@sanedge.com", "08987654321"));

            log.info("Merchants seeded.");
        } catch (Exception e) {
            log.error("Error during merchant seeding: {}", e.getMessage());
        }
    }

    private Integer resolveOwnerUserId() {
        try {
            ApiResponseUser res = userQueryServiceBlockingStub.findById(
                    FindByIdUserRequest.newBuilder().setId(FALLBACK_OWNER_ID).build());
            if (res.hasData() && res.getData().getId() > 0) {
                return res.getData().getId();
            }
        } catch (StatusRuntimeException e) {
            log.warn("Skipping user lookup: {} — falling back to fixed owner id {}",
                    e.getStatus().getCode(), FALLBACK_OWNER_ID);
        }
        return FALLBACK_OWNER_ID;
    }

    private Merchant buildMerchant(Integer ownerId, String name, String description, String address,
            String contactEmail, String contactPhone) {
        Merchant merchant = new Merchant();
        merchant.setUserId(ownerId);
        merchant.setName(name);
        merchant.setDescription(description);
        merchant.setAddress(address);
        merchant.setContactEmail(contactEmail);
        merchant.setContactPhone(contactPhone);
        return merchant;
    }
}
