package com.sanedge.ecommerce.merchant_award.service.impl;

import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.observability.TracingMetrics;
import com.sanedge.ecommerce.merchant_award.domain.requests.CreateMerchantAwardRequest;
import com.sanedge.ecommerce.merchant_award.domain.requests.UpdateMerchantAwardRequest;
import com.sanedge.ecommerce.merchant_award.domain.responses.MerchantAwardResponse;
import com.sanedge.ecommerce.merchant_award.domain.responses.MerchantAwardResponseDeleteAt;
import com.sanedge.ecommerce.merchant_award.models.MerchantCertificationAndAward;
import com.sanedge.ecommerce.merchant_award.repository.MerchantAwardCommandRepository;
import com.sanedge.ecommerce.merchant_award.service.MerchantAwardCommandService;
import com.sanedge.ecommerce.common.adapter.merchant.MerchantPort;
import com.sanedge.ecommerce.common.exception.ResourceNotFoundException;

import io.opentelemetry.api.common.Attributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MerchantAwardCommandImplService implements MerchantAwardCommandService {

    private final MerchantPort merchantPort;
    private final MerchantAwardCommandRepository merchantAwardCommandRepository;
    private final TracingMetrics tracingMetrics;

    @Override
    public ApiResponse<MerchantAwardResponse> createMerchantAward(CreateMerchantAwardRequest req) {
        Attributes attrs = Attributes.builder()
                .put("award.title", req.getTitle() != null ? req.getTitle() : "")
                .build();

        return tracingMetrics.traceAndMeasure("createMerchantAward", "create_award", attrs, () -> {
            log.info("🆕 Creating merchant award: {}", req.getTitle());
            try {
                if (!merchantExists(req.getMerchantId())) {
                    return ApiResponse.<MerchantAwardResponse>builder()
                            .status("error")
                            .message("Merchant not found with id " + req.getMerchantId())
                            .data(null)
                            .build();
                }

                MerchantCertificationAndAward award = MerchantCertificationAndAward.fromCreateRequest(req);
                MerchantCertificationAndAward saved = merchantAwardCommandRepository.save(award);

                return ApiResponse.<MerchantAwardResponse>builder()
                        .status("success")
                        .message("Merchant award created successfully")
                        .data(MerchantAwardResponse.from(saved))
                        .build();

            } catch (Exception e) {
                log.error("❌ Failed to create merchant award", e);
                return ApiResponse.<MerchantAwardResponse>builder()
                        .status("error")
                        .message("Failed to create merchant award")
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<MerchantAwardResponse> updateMerchantAward(UpdateMerchantAwardRequest req) {
        Attributes attrs = Attributes.builder()
                .put("award.id", String.valueOf(req.getMerchantCertificationId()))
                .build();

        return tracingMetrics.traceAndMeasure("updateMerchantAward", "update_award", attrs, () -> {
            log.info("✏️ Updating merchant award ID: {}", req.getMerchantCertificationId());
            try {
                MerchantCertificationAndAward award = merchantAwardCommandRepository
                        .findById(String.valueOf(req.getMerchantCertificationId()))
                        .orElse(null);

                if (award == null) {
                    return ApiResponse.<MerchantAwardResponse>builder()
                            .status("error")
                            .message("Merchant award not found with id " + req.getMerchantCertificationId())
                            .data(null)
                            .build();
                }

                award.updateFromRequest(req);
                MerchantCertificationAndAward updated = merchantAwardCommandRepository.save(award);

                return ApiResponse.<MerchantAwardResponse>builder()
                        .status("success")
                        .message("Merchant award updated successfully")
                        .data(MerchantAwardResponse.from(updated))
                        .build();

            } catch (Exception e) {
                log.error("❌ Failed to update merchant award", e);
                return ApiResponse.<MerchantAwardResponse>builder()
                        .status("error")
                        .message("Failed to update merchant award")
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<MerchantAwardResponseDeleteAt> trashedMerchantAward(Integer merchantAwardId) {
        Attributes attrs = Attributes.builder()
                .put("award.id", String.valueOf(merchantAwardId))
                .build();

        return tracingMetrics.traceAndMeasure("trashedMerchantAward", "trash_award", attrs, () -> {
            log.info("🗑️ Soft deleting merchant award ID: {}", merchantAwardId);
            try {
                MerchantCertificationAndAward award = merchantAwardCommandRepository
                        .trashed(String.valueOf(merchantAwardId));
                if (award == null) {
                    return ApiResponse.<MerchantAwardResponseDeleteAt>builder()
                            .status("error")
                            .message("Merchant award not found with id " + merchantAwardId)
                            .data(null)
                            .build();
                }

                return ApiResponse.<MerchantAwardResponseDeleteAt>builder()
                        .status("success")
                        .message("Merchant award trashed successfully")
                        .data(MerchantAwardResponseDeleteAt.from(award))
                        .build();

            } catch (Exception e) {
                log.error("❌ Failed to soft delete merchant award", e);
                return ApiResponse.<MerchantAwardResponseDeleteAt>builder()
                        .status("error")
                        .message("Failed to soft delete merchant award")
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<MerchantAwardResponseDeleteAt> restoreMerchantAward(Integer merchantAwardId) {
        Attributes attrs = Attributes.builder()
                .put("award.id", String.valueOf(merchantAwardId))
                .build();

        return tracingMetrics.traceAndMeasure("restoreMerchantAward", "restore_award", attrs, () -> {
            log.info("♻️ Restoring merchant award ID: {}", merchantAwardId);
            try {
                MerchantCertificationAndAward award = merchantAwardCommandRepository
                        .restore(String.valueOf(merchantAwardId));
                if (award == null) {
                    return ApiResponse.<MerchantAwardResponseDeleteAt>builder()
                            .status("error")
                            .message("Merchant award not found with id " + merchantAwardId)
                            .data(null)
                            .build();
                }

                return ApiResponse.<MerchantAwardResponseDeleteAt>builder()
                        .status("success")
                        .message("Merchant award restored successfully")
                        .data(MerchantAwardResponseDeleteAt.from(award))
                        .build();

            } catch (Exception e) {
                log.error("❌ Failed to restore merchant award", e);
                return ApiResponse.<MerchantAwardResponseDeleteAt>builder()
                        .status("error")
                        .message("Failed to restore merchant award")
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<Boolean> deleteMerchantAwardPermanent(Integer merchantAwardId) {
        Attributes attrs = Attributes.builder()
                .put("award.id", String.valueOf(merchantAwardId))
                .build();

        return tracingMetrics.traceAndMeasure("deleteMerchantAwardPermanent", "delete_award_permanent", attrs, () -> {
            log.warn("❌ Permanently deleting merchant award ID: {}", merchantAwardId);
            try {
                boolean deleted = merchantAwardCommandRepository.deletePermanent(String.valueOf(merchantAwardId));
                if (!deleted) {
                    return ApiResponse.<Boolean>builder()
                            .status("error")
                            .message("Merchant award not found with id " + merchantAwardId)
                            .data(false)
                            .build();
                }

                return ApiResponse.<Boolean>builder()
                        .status("success")
                        .message("Merchant award permanently deleted")
                        .data(true)
                        .build();

            } catch (Exception e) {
                log.error("❌ Failed to permanently delete merchant award", e);
                return ApiResponse.<Boolean>builder()
                        .status("error")
                        .message("Failed to permanently delete merchant award")
                        .data(false)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<Boolean> restoreAllMerchantAward() {
        return tracingMetrics.traceAndMeasure("restoreAllMerchantAward", "restore_all_awards", () -> {
            log.info("♻️ Restoring all trashed merchant awards");
            try {
                boolean restored = merchantAwardCommandRepository.restoreAllDeleted();
                return ApiResponse.<Boolean>builder()
                        .status("success")
                        .message("All trashed merchant awards restored")
                        .data(restored)
                        .build();

            } catch (Exception e) {
                log.error("❌ Failed to restore all merchant awards", e);
                return ApiResponse.<Boolean>builder()
                        .status("error")
                        .message("Failed to restore all merchant awards")
                        .data(false)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<Boolean> deleteAllMerchantAwardPermanent() {
        return tracingMetrics.traceAndMeasure("deleteAllMerchantAwardPermanent", "delete_all_awards_permanent", () -> {
            log.warn("❌ Permanently deleting all trashed merchant awards");
            try {
                boolean deleted = merchantAwardCommandRepository.deleteAllDeleted();
                return ApiResponse.<Boolean>builder()
                        .status("success")
                        .message("All trashed merchant awards permanently deleted")
                        .data(deleted)
                        .build();

            } catch (Exception e) {
                log.error("❌ Failed to delete all merchant awards permanently", e);
                return ApiResponse.<Boolean>builder()
                        .status("error")
                        .message("Failed to permanently delete all merchant awards")
                        .data(false)
                        .build();
            }
        });
    }

    private boolean merchantExists(Integer merchantId) {
        try {
            merchantPort.findById(merchantId);
            return true;
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }
}
