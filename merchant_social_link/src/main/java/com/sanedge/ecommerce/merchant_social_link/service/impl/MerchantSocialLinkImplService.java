package com.sanedge.ecommerce.merchant_social_link.service.impl;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.exception.ResourceNotFoundException;
import com.sanedge.ecommerce.common.observability.TracingMetrics;
import com.sanedge.ecommerce.merchant_social_link.domain.requests.CreateMerchantSocialRequest;
import com.sanedge.ecommerce.merchant_social_link.domain.requests.UpdateMerchantSocialRequest;
import com.sanedge.ecommerce.merchant_social_link.domain.responses.MerchantSocialMediaLinkResponse;
import com.sanedge.ecommerce.merchant_social_link.domain.responses.MerchantSocialMediaLinkResponseDeleteAt;
import com.sanedge.ecommerce.merchant_social_link.models.MerchantSocialMediaLink;
import com.sanedge.ecommerce.merchant_social_link.repository.MerchantSocialMediaLinkRepository;
import com.sanedge.ecommerce.merchant_social_link.service.MerchantSocialLinkService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MerchantSocialLinkImplService implements MerchantSocialLinkService {

    private final MerchantSocialMediaLinkRepository merchantSocialMediaLinkRepository;
    private final Validator validator;
    private final TracingMetrics tracingMetrics;

    @Override
    public ApiResponse<MerchantSocialMediaLinkResponse> create(CreateMerchantSocialRequest request) {
        return tracingMetrics.traceAndMeasure("createMerchantSocialLink", "create_social_link", () -> {
            validateRequest(request);

            log.info("🆕 Creating merchant social link platform={} for merchantDetailId={}",
                    request.getPlatform(), request.getMerchantDetailId());

            merchantSocialMediaLinkRepository.findByMerchantDetailIdAndPlatform(
                    request.getMerchantDetailId(), request.getPlatform()).ifPresent(existing -> {
                        log.warn("❌ Merchant social creation failed. Platform '{}' already exists for merchantDetailId={}",
                                request.getPlatform(), request.getMerchantDetailId());
                        throw new IllegalArgumentException("Platform '" + request.getPlatform() + "' already exists");
                    });

            MerchantSocialMediaLink link = new MerchantSocialMediaLink();
            link.setMerchantDetailId(request.getMerchantDetailId());
            link.setPlatform(request.getPlatform());
            link.setUrl(request.getUrl());
            link.setCreatedAt(LocalDateTime.now());
            link.setUpdatedAt(LocalDateTime.now());

            MerchantSocialMediaLink saved = merchantSocialMediaLinkRepository.save(link);
            MerchantSocialMediaLinkResponse response = MerchantSocialMediaLinkResponse.from(saved);

            log.info("✅ Merchant social link created successfully with id={}", response.getId());

            return ApiResponse.<MerchantSocialMediaLinkResponse>builder()
                    .status("success")
                    .message("✅ Merchant social link created successfully!")
                    .data(response)
                    .build();
        });
    }

    @Override
    public ApiResponse<MerchantSocialMediaLinkResponse> update(UpdateMerchantSocialRequest request) {
        return tracingMetrics.traceAndMeasure("updateMerchantSocialLink", "update_social_link", () -> {
            validateRequest(request);

            if (request.getId() == null) {
                throw new ResourceNotFoundException("id is required");
            }

            log.info("🔄 Updating merchant social link id={}", request.getId());

            MerchantSocialMediaLink link = merchantSocialMediaLinkRepository
                    .findById(String.valueOf(request.getId()))
                    .orElseThrow(() -> {
                        log.error("❌ Merchant social link with id {} not found", request.getId());
                        return new ResourceNotFoundException("Merchant social link not found");
                    });

            link.setMerchantDetailId(request.getMerchantDetailId());
            link.setPlatform(request.getPlatform());
            link.setUrl(request.getUrl());
            link.setUpdatedAt(LocalDateTime.now());

            MerchantSocialMediaLink updated = merchantSocialMediaLinkRepository.save(link);
            MerchantSocialMediaLinkResponse response = MerchantSocialMediaLinkResponse.from(updated);

            log.info("✅ Merchant social link updated successfully with id={}", response.getId());

            return ApiResponse.<MerchantSocialMediaLinkResponse>builder()
                    .status("success")
                    .message("✅ Merchant social link updated successfully!")
                    .data(response)
                    .build();
        });
    }

    @Override
    public ApiResponse<MerchantSocialMediaLinkResponseDeleteAt> trash(Integer id) {
        return tracingMetrics.traceAndMeasure("trashMerchantSocialLink", "trash_social_link", () -> {
            log.info("🗑️ Trashing merchant social link id={}", id);

            try {
                MerchantSocialMediaLink link = merchantSocialMediaLinkRepository.trashed(String.valueOf(id));
                return ApiResponse.<MerchantSocialMediaLinkResponseDeleteAt>builder()
                        .status("success")
                        .message("🗑️ Merchant social link trashed successfully!")
                        .data(MerchantSocialMediaLinkResponseDeleteAt.from(link))
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to trash merchant social link id={}", id, e);
                throw new ResourceNotFoundException("Failed to trash merchant social link", e);
            }
        });
    }

    @Override
    public ApiResponse<MerchantSocialMediaLinkResponseDeleteAt> restore(Integer id) {
        return tracingMetrics.traceAndMeasure("restoreMerchantSocialLink", "restore_social_link", () -> {
            log.info("♻️ Restoring merchant social link id={}", id);

            try {
                MerchantSocialMediaLink link = merchantSocialMediaLinkRepository.restore(String.valueOf(id));
                return ApiResponse.<MerchantSocialMediaLinkResponseDeleteAt>builder()
                        .status("success")
                        .message("♻️ Merchant social link restored successfully!")
                        .data(MerchantSocialMediaLinkResponseDeleteAt.from(link))
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to restore merchant social link id={}", id, e);
                throw new ResourceNotFoundException("Failed to restore merchant social link", e);
            }
        });
    }

    @Override
    public ApiResponse<Boolean> delete(Integer id) {
        return tracingMetrics.traceAndMeasure("deleteMerchantSocialLink", "delete_social_link_permanent", () -> {
            log.info("🧨 Permanently deleting merchant social link id={}", id);

            try {
                merchantSocialMediaLinkRepository.deletePermanent(String.valueOf(id));
                return ApiResponse.<Boolean>builder()
                        .status("success")
                        .message("🧨 Merchant social link permanently deleted!")
                        .data(true)
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to permanently delete merchant social link id={}", id, e);
                throw new ResourceNotFoundException("Failed to permanently delete merchant social link", e);
            }
        });
    }

    @Override
    public ApiResponse<Boolean> restoreAll() {
        return tracingMetrics.traceAndMeasure("restoreAllMerchantSocialLinks", "restore_all_social_links", () -> {
            log.info("🔄 Restoring ALL trashed merchant social links");

            try {
                merchantSocialMediaLinkRepository.restoreAllDeleted();
                return ApiResponse.<Boolean>builder()
                        .status("success")
                        .message("🔄 All merchant social links restored successfully!")
                        .data(true)
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to restore all merchant social links", e);
                throw new ResourceNotFoundException("Failed to restore all merchant social links", e);
            }
        });
    }

    @Override
    public ApiResponse<Boolean> deleteAll() {
        return tracingMetrics.traceAndMeasure("deleteAllMerchantSocialLinks", "delete_all_social_links_permanent", () -> {
            log.info("💣 Permanently deleting ALL trashed merchant social links");

            try {
                merchantSocialMediaLinkRepository.deleteAllDeleted();
                return ApiResponse.<Boolean>builder()
                        .status("success")
                        .message("💣 All merchant social links permanently deleted!")
                        .data(true)
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to delete all merchant social links", e);
                throw new ResourceNotFoundException("Failed to delete all merchant social links", e);
            }
        });
    }

    private <T> void validateRequest(T req) {
        Set<ConstraintViolation<T>> violations = validator.validate(req);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<T> violation : violations) {
                sb.append(violation.getPropertyPath()).append(": ").append(violation.getMessage()).append("; ");
            }
            log.error("Validation failed: {}", sb);
            throw new ConstraintViolationException("Validation failed: " + sb, violations);
        }
    }
}
