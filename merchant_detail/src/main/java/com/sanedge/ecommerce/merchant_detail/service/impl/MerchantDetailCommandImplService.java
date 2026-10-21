package com.sanedge.ecommerce.merchant_detail.service.impl;

import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.observability.TracingMetrics;
import com.sanedge.ecommerce.merchant_detail.domain.requests.CreateMerchantDetailRequest;
import com.sanedge.ecommerce.merchant_detail.domain.requests.UpdateMerchantDetailRequest;
import com.sanedge.ecommerce.merchant_detail.domain.responses.MerchantDetailResponse;
import com.sanedge.ecommerce.merchant_detail.domain.responses.MerchantDetailResponseDeleteAt;
import com.sanedge.ecommerce.merchant_detail.models.MerchantDetail;
import com.sanedge.ecommerce.merchant_detail.repository.MerchantDetailCommandRepository;
import com.sanedge.ecommerce.merchant_detail.repository.MerchantDetailQueryRepository;
import com.sanedge.ecommerce.merchant_detail.service.MerchantDetailCommandService;

import io.opentelemetry.api.common.Attributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantDetailCommandImplService implements MerchantDetailCommandService {
    private final MerchantDetailQueryRepository merchantDetailQueryRepository;
    private final MerchantDetailCommandRepository merchantDetailCommandRepository;
    private final TracingMetrics tracingMetrics;

    @Override
    public ApiResponse<MerchantDetailResponse> createMerchant(CreateMerchantDetailRequest req) {
        Attributes attrs = Attributes.builder()
                .put("merchant.id", String.valueOf(req.getMerchantId()))
                .build();

        return tracingMetrics.traceAndMeasure("createMerchantDetail", "create_detail", attrs, () -> {
            log.info("🆕 Creating merchant detail: {}", req);
            try {
                MerchantDetail entity = new MerchantDetail();
                entity.setMerchantId(req.getMerchantId());
                entity.setDisplayName(req.getDisplayName());
                entity.setShortDescription(req.getShortDescription());
                entity.setWebsiteUrl(req.getWebsiteUrl());
                entity.setCoverImageUrl(req.getCoverImageUrl());
                entity.setLogoUrl(req.getLogoUrl());

                MerchantDetail saved = merchantDetailCommandRepository.save(entity);

                return ApiResponse.<MerchantDetailResponse>builder()
                        .status("success")
                        .message("✅ Merchant detail created successfully!")
                        .data(MerchantDetailResponse.from(saved))
                        .build();

            } catch (Exception e) {
                log.error("💥 Failed to create merchant detail: {}", req, e);
                return ApiResponse.<MerchantDetailResponse>builder()
                        .status("error")
                        .message("Failed to create merchant detail")
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<MerchantDetailResponse> updateMerchant(UpdateMerchantDetailRequest req) {
        Attributes attrs = Attributes.builder()
                .put("detail.id", String.valueOf(req.getMerchantDetailId()))
                .build();

        return tracingMetrics.traceAndMeasure("updateMerchantDetail", "update_detail", attrs, () -> {
            log.info("✏️ Updating merchant detail id={}", req.getMerchantDetailId());
            try {
                MerchantDetail existing = merchantDetailQueryRepository
                        .findById(String.valueOf(req.getMerchantDetailId()))
                        .orElse(null);

                if (existing == null) {
                    return ApiResponse.<MerchantDetailResponse>builder()
                            .status("error")
                            .message("Merchant detail not found")
                            .build();
                }

                existing.setDisplayName(req.getDisplayName());
                existing.setShortDescription(req.getShortDescription());
                existing.setWebsiteUrl(req.getWebsiteUrl());
                existing.setCoverImageUrl(req.getCoverImageUrl());
                existing.setLogoUrl(req.getLogoUrl());

                MerchantDetail updated = merchantDetailCommandRepository.save(existing);

                return ApiResponse.<MerchantDetailResponse>builder()
                        .status("success")
                        .message("✅ Merchant detail updated successfully!")
                        .data(MerchantDetailResponse.from(updated))
                        .build();

            } catch (Exception e) {
                log.error("💥 Failed to update merchant detail id={}", req.getMerchantDetailId(), e);
                return ApiResponse.<MerchantDetailResponse>builder()
                        .status("error")
                        .message("Failed to update merchant detail")
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<MerchantDetailResponseDeleteAt> trashedMerchant(Integer merchantID) {
        Attributes attrs = Attributes.builder()
                .put("detail.id", String.valueOf(merchantID))
                .build();

        return tracingMetrics.traceAndMeasure("trashedMerchantDetail", "trash_detail", attrs, () -> {
            log.info("🗑️ Trashing merchant detail id={}", merchantID);
            try {
                MerchantDetail entity = merchantDetailQueryRepository.findById(String.valueOf(merchantID))
                        .orElse(null);

                if (entity == null) {
                    log.warn("⚠️ Merchant detail not found id={}", merchantID);
                    return ApiResponse.<MerchantDetailResponseDeleteAt>builder()
                            .status("error")
                            .message("Merchant detail not found")
                            .data(null)
                            .build();
                }

                MerchantDetail updated = merchantDetailCommandRepository.trashed(String.valueOf(merchantID));

                return ApiResponse.<MerchantDetailResponseDeleteAt>builder()
                        .status("success")
                        .message("🗑️ Merchant detail trashed successfully!")
                        .data(MerchantDetailResponseDeleteAt.from(updated))
                        .build();

            } catch (Exception e) {
                log.error("💥 Failed to trash merchant detail id={}", merchantID, e);
                return ApiResponse.<MerchantDetailResponseDeleteAt>builder()
                        .status("error")
                        .message("Failed to trash merchant detail")
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<MerchantDetailResponseDeleteAt> restoreMerchant(Integer merchantID) {
        Attributes attrs = Attributes.builder()
                .put("detail.id", String.valueOf(merchantID))
                .build();

        return tracingMetrics.traceAndMeasure("restoreMerchantDetail", "restore_detail", attrs, () -> {
            log.info("♻️ Restoring merchant detail id={}", merchantID);
            try {
                MerchantDetail entity = merchantDetailQueryRepository.findById(String.valueOf(merchantID))
                        .orElse(null);

                if (entity == null) {
                    log.warn("⚠️ Merchant detail not found id={}", merchantID);
                    return ApiResponse.<MerchantDetailResponseDeleteAt>builder()
                            .status("error")
                            .message("Merchant detail not found")
                            .data(null)
                            .build();
                }

                MerchantDetail updated = merchantDetailCommandRepository.restore(String.valueOf(merchantID));

                return ApiResponse.<MerchantDetailResponseDeleteAt>builder()
                        .status("success")
                        .message("♻️ Merchant detail restored successfully!")
                        .data(MerchantDetailResponseDeleteAt.from(updated))
                        .build();

            } catch (Exception e) {
                log.error("💥 Failed to restore merchant detail id={}", merchantID, e);
                return ApiResponse.<MerchantDetailResponseDeleteAt>builder()
                        .status("error")
                        .message("Failed to restore merchant detail")
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<Boolean> deleteMerchantPermanent(Integer merchantID) {
        Attributes attrs = Attributes.builder()
                .put("detail.id", String.valueOf(merchantID))
                .build();

        return tracingMetrics.traceAndMeasure("deleteMerchantPermanent", "delete_detail_permanent", attrs, () -> {
            log.info("🗑️ Permanently deleting merchant detail id={}", merchantID);
            try {
                merchantDetailQueryRepository.deleteById(String.valueOf(merchantID));
                return ApiResponse.<Boolean>builder()
                        .status("success")
                        .message("✅ Merchant detail permanently deleted")
                        .data(true)
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to permanently delete merchant detail id={}", merchantID, e);
                return ApiResponse.<Boolean>builder()
                        .status("error")
                        .message("Failed to permanently delete merchant detail")
                        .data(false)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<Boolean> restoreAllMerchant() {
        return tracingMetrics.traceAndMeasure("restoreAllMerchant", "restore_all_details", () -> {
            log.info("♻️ Restoring all trashed merchant details");
            try {
                merchantDetailCommandRepository.restoreAllDeleted();
                return ApiResponse.<Boolean>builder()
                        .status("success")
                        .message("✅ All trashed merchant details restored")
                        .data(true)
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to restore all merchants", e);
                return ApiResponse.<Boolean>builder()
                        .status("error")
                        .message("Failed to restore all merchants")
                        .data(false)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<Boolean> deleteAllMerchantPermanent() {
        return tracingMetrics.traceAndMeasure("deleteAllMerchantPermanent", "delete_all_details_permanent", () -> {
            log.info("🗑️ Permanently deleting all trashed merchant details");
            try {
                merchantDetailCommandRepository.deleteAllDeleted();
                return ApiResponse.<Boolean>builder()
                        .status("success")
                        .message("✅ All trashed merchant details permanently deleted")
                        .data(true)
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to permanently delete all trashed merchants", e);
                return ApiResponse.<Boolean>builder()
                        .status("error")
                        .message("Failed to permanently delete all trashed merchants")
                        .data(false)
                        .build();
            }
        });
    }
}
