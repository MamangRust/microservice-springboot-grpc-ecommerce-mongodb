package com.sanedge.ecommerce.merchant_business.service.impl;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.exception.ResourceNotFoundException;
import com.sanedge.ecommerce.common.observability.TracingMetrics;
import com.sanedge.ecommerce.merchant_business.domain.requests.CreateMerchantBusinessRequest;
import com.sanedge.ecommerce.merchant_business.domain.requests.UpdateMerchantBusinessRequest;
import com.sanedge.ecommerce.merchant_business.domain.responses.MerchantBusinessResponse;
import com.sanedge.ecommerce.merchant_business.domain.responses.MerchantBusinessResponseDeleteAt;
import com.sanedge.ecommerce.merchant_business.models.MerchantBusinessInformation;
import com.sanedge.ecommerce.merchant_business.repository.MerchantBusinessCommandRepository;
import com.sanedge.ecommerce.merchant_business.repository.MerchantBusinessQueryRepository;
import com.sanedge.ecommerce.merchant_business.service.MerchantBusinessCommandService;
import com.sanedge.ecommerce.common.adapter.merchant.MerchantPort;
import io.opentelemetry.api.common.Attributes;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MerchantBusinessCommandImplService implements MerchantBusinessCommandService {

    private final MerchantBusinessCommandRepository merchantBusinessCommandRepository;
    private final MerchantBusinessQueryRepository merchantBusinessQueryRepository;
    private final MerchantPort merchantPort;
    private final Validator validator;
    private final TracingMetrics tracingMetrics;

    @Override
    public ApiResponse<MerchantBusinessResponse> createMerchantBusiness(CreateMerchantBusinessRequest req) {
        Attributes attrs = Attributes.builder()
                .put("merchant.id", String.valueOf(req.getMerchantId()))
                .build();

        return tracingMetrics.traceAndMeasure("createMerchantBusiness", "create_business", attrs, () -> {
            try {
                log.info("🆕 Creating merchant business info for merchant ID: {}", req.getMerchantId());

                validateRequest(req);
                validateMerchantExists(req.getMerchantId());

                MerchantBusinessInformation business = MerchantBusinessInformation.fromCreateRequest(req);
                MerchantBusinessInformation saved = merchantBusinessCommandRepository.save(business);

                return ApiResponse.<MerchantBusinessResponse>builder()
                        .status("success")
                        .message("Merchant business info created successfully")
                        .data(MerchantBusinessResponse.from(saved))
                        .build();
            } catch (ResourceNotFoundException ex) {
                log.error("❌ Resource not found: {}", ex.getMessage());
                return ApiResponse.<MerchantBusinessResponse>builder()
                        .status("error")
                        .message(ex.getMessage())
                        .data(null)
                        .build();
            } catch (Exception ex) {
                log.error("❌ Failed to create merchant business info: {}", ex.getMessage(), ex);
                return ApiResponse.<MerchantBusinessResponse>builder()
                        .status("error")
                        .message("Failed to create merchant business info: " + ex.getMessage())
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<MerchantBusinessResponse> updateMerchantBusiness(UpdateMerchantBusinessRequest req) {
        Attributes attrs = Attributes.builder()
                .put("business.id", String.valueOf(req.getMerchantBusinessInfoId()))
                .build();

        return tracingMetrics.traceAndMeasure("updateMerchantBusiness", "update_business", attrs, () -> {
            try {
                log.info("✏️ Updating merchant business info ID: {}", req.getMerchantBusinessInfoId());

                validateRequest(req);

                MerchantBusinessInformation business = merchantBusinessQueryRepository
                        .findMerchantBusinessInformationById(String.valueOf(req.getMerchantBusinessInfoId()))
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Merchant business info not found with id " + req.getMerchantBusinessInfoId()));

                business.setBusinessType(req.getBusinessType());
                business.setTaxId(req.getTaxId());
                business.setEstablishedYear(req.getEstablishedYear());
                business.setNumberOfEmployees(req.getNumberOfEmployees());
                business.setWebsiteUrl(req.getWebsiteUrl());

                MerchantBusinessInformation updated = merchantBusinessCommandRepository.save(business);

                return ApiResponse.<MerchantBusinessResponse>builder()
                        .status("success")
                        .message("Merchant business info updated successfully")
                        .data(MerchantBusinessResponse.from(updated))
                        .build();
            } catch (ResourceNotFoundException ex) {
                log.error("❌ Resource not found: {}", ex.getMessage());
                return ApiResponse.<MerchantBusinessResponse>builder()
                        .status("error")
                        .message(ex.getMessage())
                        .data(null)
                        .build();
            } catch (Exception ex) {
                log.error("❌ Failed to update merchant business info: {}", ex.getMessage(), ex);
                return ApiResponse.<MerchantBusinessResponse>builder()
                        .status("error")
                        .message("Failed to update merchant business info: " + ex.getMessage())
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<MerchantBusinessResponseDeleteAt> trashedMerchantBusiness(Integer merchantBusinessInfoId) {
        Attributes attrs = Attributes.builder()
                .put("business.id", String.valueOf(merchantBusinessInfoId))
                .build();

        return tracingMetrics.traceAndMeasure("trashedMerchantBusiness", "trash_business", attrs, () -> {
            try {
                log.info("🗑️ Soft deleting merchant business info ID: {}", merchantBusinessInfoId);
                MerchantBusinessInformation business = merchantBusinessCommandRepository
                        .trashed(String.valueOf(merchantBusinessInfoId));

                return ApiResponse.<MerchantBusinessResponseDeleteAt>builder()
                        .status("success")
                        .message("Merchant business info trashed successfully")
                        .data(MerchantBusinessResponseDeleteAt.from(business))
                        .build();
            } catch (ResourceNotFoundException ex) {
                log.error("❌ Merchant business not found: {}", ex.getMessage());
                return ApiResponse.<MerchantBusinessResponseDeleteAt>builder()
                        .status("error")
                        .message(ex.getMessage())
                        .data(null)
                        .build();
            } catch (Exception ex) {
                log.error("❌ Failed to trash merchant business info: {}", ex.getMessage(), ex);
                return ApiResponse.<MerchantBusinessResponseDeleteAt>builder()
                        .status("error")
                        .message("Failed to trash merchant business info: " + ex.getMessage())
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<MerchantBusinessResponseDeleteAt> restoreMerchantBusiness(Integer merchantBusinessInfoId) {
        Attributes attrs = Attributes.builder()
                .put("business.id", String.valueOf(merchantBusinessInfoId))
                .build();

        return tracingMetrics.traceAndMeasure("restoreMerchantBusiness", "restore_business", attrs, () -> {
            try {
                log.info("♻️ Restoring merchant business info ID: {}", merchantBusinessInfoId);
                MerchantBusinessInformation business = merchantBusinessCommandRepository
                        .restore(String.valueOf(merchantBusinessInfoId));

                return ApiResponse.<MerchantBusinessResponseDeleteAt>builder()
                        .status("success")
                        .message("Merchant business info restored successfully")
                        .data(MerchantBusinessResponseDeleteAt.from(business))
                        .build();
            } catch (ResourceNotFoundException ex) {
                log.error("❌ Merchant business not found: {}", ex.getMessage());
                return ApiResponse.<MerchantBusinessResponseDeleteAt>builder()
                        .status("error")
                        .message(ex.getMessage())
                        .data(null)
                        .build();
            } catch (Exception ex) {
                log.error("❌ Failed to restore merchant business info: {}", ex.getMessage(), ex);
                return ApiResponse.<MerchantBusinessResponseDeleteAt>builder()
                        .status("error")
                        .message("Failed to restore merchant business info: " + ex.getMessage())
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<Boolean> deleteMerchantBusinessPermanent(Integer merchantBusinessInfoId) {
        Attributes attrs = Attributes.builder()
                .put("business.id", String.valueOf(merchantBusinessInfoId))
                .build();

        return tracingMetrics.traceAndMeasure("deleteMerchantBusinessPermanent", "delete_business_permanent", attrs, () -> {
            try {
                boolean deleted = merchantBusinessCommandRepository.deletePermanent(String.valueOf(merchantBusinessInfoId));
                return ApiResponse.<Boolean>builder()
                        .status("success")
                        .message("Merchant business info permanently deleted")
                        .data(deleted)
                        .build();
            } catch (ResourceNotFoundException ex) {
                return ApiResponse.<Boolean>builder()
                        .status("error")
                        .message("Merchant business info not found: " + ex.getMessage())
                        .data(false)
                        .build();
            } catch (Exception ex) {
                return ApiResponse.<Boolean>builder()
                        .status("error")
                        .message("Failed to delete merchant business info: " + ex.getMessage())
                        .data(false)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<Boolean> restoreAllMerchantBusiness() {
        return tracingMetrics.traceAndMeasure("restoreAllMerchantBusiness", "restore_all_business_info", () -> {
            try {
                boolean restored = merchantBusinessCommandRepository.restoreAllDeleted();
                return ApiResponse.<Boolean>builder()
                        .status("success")
                        .message("All trashed merchant business info restored")
                        .data(restored)
                        .build();
            } catch (Exception ex) {
                return ApiResponse.<Boolean>builder()
                        .status("error")
                        .message("Failed to restore merchant business info: " + ex.getMessage())
                        .data(false)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<Boolean> deleteAllMerchantBusinessPermanent() {
        return tracingMetrics.traceAndMeasure("deleteAllMerchantBusinessPermanent", "delete_all_business_info_permanent",
                () -> {
                    try {
                        boolean deleted = merchantBusinessCommandRepository.deleteAllDeleted();
                        return ApiResponse.<Boolean>builder()
                                .status("success")
                                .message("All trashed merchant business info permanently deleted")
                                .data(deleted)
                                .build();
                    } catch (Exception ex) {
                        return ApiResponse.<Boolean>builder()
                                .status("error")
                                .message("Failed to delete all merchant business info: " + ex.getMessage())
                                .data(false)
                                .build();
                    }
                });
    }

    /**
     * Replaces the monolith's {@code MerchantQueryRepository} lookup with a gRPC
     * call to the merchant service.
     */
    private void validateMerchantExists(Integer merchantId) {
        merchantPort.findById(merchantId);
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
