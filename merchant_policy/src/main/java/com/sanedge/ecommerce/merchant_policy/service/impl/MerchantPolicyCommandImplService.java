package com.sanedge.ecommerce.merchant_policy.service.impl;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.observability.TracingMetrics;
import com.sanedge.ecommerce.merchant_policy.domain.requests.CreateMerchantPolicyRequest;
import com.sanedge.ecommerce.merchant_policy.domain.requests.UpdateMerchantPolicyRequest;
import com.sanedge.ecommerce.merchant_policy.domain.responses.MerchantPoliciesResponse;
import com.sanedge.ecommerce.merchant_policy.domain.responses.MerchantPoliciesResponseDeleteAt;
import com.sanedge.ecommerce.merchant_policy.models.MerchantPolicy;
import com.sanedge.ecommerce.merchant_policy.repository.MerchantPolicyCommandRepository;
import com.sanedge.ecommerce.merchant_policy.service.MerchantPolicyCommandService;
import com.sanedge.ecommerce.common.adapter.merchant.MerchantPort;
import com.sanedge.ecommerce.common.exception.ResourceNotFoundException;
import io.opentelemetry.api.common.Attributes;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MerchantPolicyCommandImplService implements MerchantPolicyCommandService {

    private final MerchantPort merchantPort;
    private final MerchantPolicyCommandRepository merchantPolicyCommandRepository;
    private final Validator validator;
    private final TracingMetrics tracingMetrics;

    @Override
    public ApiResponse<MerchantPoliciesResponse> create(CreateMerchantPolicyRequest request) {
        Attributes attrs = Attributes.builder()
                .put("merchant.id", String.valueOf(request.getMerchantId()))
                .build();

        return tracingMetrics.traceAndMeasure("createMerchantPolicy", "create_policy", attrs, () -> {
            validateRequest(request);
            log.info("🆕 Creating merchant policy for merchantId={} title={}", request.getMerchantId(), request.getTitle());

            try {
                if (!merchantExists(request.getMerchantId())) {
                    log.warn("⚠️ Merchant not found with id={}", request.getMerchantId());
                    return ApiResponse.<MerchantPoliciesResponse>builder()
                            .status("failed")
                            .message("Merchant not found with id " + request.getMerchantId())
                            .data(null)
                            .build();
                }

                MerchantPolicy policy = new MerchantPolicy();
                policy.setMerchantId(request.getMerchantId());
                policy.setPolicyType(request.getPolicyType());
                policy.setTitle(request.getTitle());
                policy.setDescription(request.getDescription());
                policy.setCreatedAt(LocalDateTime.now());
                policy.setUpdatedAt(LocalDateTime.now());

                MerchantPolicy savedPolicy = merchantPolicyCommandRepository.save(policy);
                return ApiResponse.<MerchantPoliciesResponse>builder()
                        .status("success")
                        .message("✅ Merchant policy created successfully!")
                        .data(MerchantPoliciesResponse.from(savedPolicy))
                        .build();

            } catch (Exception e) {
                log.error("💥 Failed to create merchant policy for merchantId={}", request.getMerchantId(), e);
                return ApiResponse.<MerchantPoliciesResponse>builder()
                        .status("failed")
                        .message("Failed to create merchant policy")
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<MerchantPoliciesResponse> update(UpdateMerchantPolicyRequest request) {
        Attributes attrs = Attributes.builder()
                .put("policy.id", String.valueOf(request.getMerchantPolicyId()))
                .build();

        return tracingMetrics.traceAndMeasure("updateMerchantPolicy", "update_policy", attrs, () -> {
            validateRequest(request);
            log.info("🔄 Updating merchant policy id={}", request.getMerchantPolicyId());

            try {
                MerchantPolicy policy = merchantPolicyCommandRepository
                        .findById(String.valueOf(request.getMerchantPolicyId()))
                        .orElse(null);

                if (policy == null) {
                    log.warn("⚠️ Merchant policy not found id={}", request.getMerchantPolicyId());
                    return ApiResponse.<MerchantPoliciesResponse>builder()
                            .status("failed")
                            .message("Merchant policy not found")
                            .data(null)
                            .build();
                }

                policy.setPolicyType(request.getPolicyType());
                policy.setTitle(request.getTitle());
                policy.setDescription(request.getDescription());
                policy.setUpdatedAt(LocalDateTime.now());

                MerchantPolicy updatedPolicy = merchantPolicyCommandRepository.save(policy);
                return ApiResponse.<MerchantPoliciesResponse>builder()
                        .status("success")
                        .message("✅ Merchant policy updated successfully!")
                        .data(MerchantPoliciesResponse.from(updatedPolicy))
                        .build();

            } catch (Exception e) {
                log.error("💥 Failed to update merchant policy id={}", request.getMerchantPolicyId(), e);
                return ApiResponse.<MerchantPoliciesResponse>builder()
                        .status("failed")
                        .message("Failed to update merchant policy")
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<MerchantPoliciesResponseDeleteAt> trash(Integer id) {
        Attributes attrs = Attributes.builder()
                .put("policy.id", String.valueOf(id))
                .build();

        return tracingMetrics.traceAndMeasure("trashMerchantPolicy", "trash_policy", attrs, () -> {
            log.info("🗑️ Trashing merchant policy id={}", id);

            try {
                MerchantPolicy policy = merchantPolicyCommandRepository.trashed(String.valueOf(id));
                return ApiResponse.<MerchantPoliciesResponseDeleteAt>builder()
                        .status("success")
                        .message("🗑️ Merchant policy trashed successfully!")
                        .data(MerchantPoliciesResponseDeleteAt.from(policy))
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to trash merchant policy id={}", id, e);
                return ApiResponse.<MerchantPoliciesResponseDeleteAt>builder()
                        .status("failed")
                        .message("Failed to trash merchant policy")
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<MerchantPoliciesResponseDeleteAt> restore(Integer id) {
        Attributes attrs = Attributes.builder()
                .put("policy.id", String.valueOf(id))
                .build();

        return tracingMetrics.traceAndMeasure("restoreMerchantPolicy", "restore_policy", attrs, () -> {
            log.info("♻️ Restoring merchant policy id={}", id);

            try {
                MerchantPolicy policy = merchantPolicyCommandRepository.restore(String.valueOf(id));
                return ApiResponse.<MerchantPoliciesResponseDeleteAt>builder()
                        .status("success")
                        .message("♻️ Merchant policy restored successfully!")
                        .data(MerchantPoliciesResponseDeleteAt.from(policy))
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to restore merchant policy id={}", id, e);
                return ApiResponse.<MerchantPoliciesResponseDeleteAt>builder()
                        .status("failed")
                        .message("Failed to restore merchant policy")
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<Boolean> delete(Integer id) {
        Attributes attrs = Attributes.builder()
                .put("policy.id", String.valueOf(id))
                .build();

        return tracingMetrics.traceAndMeasure("deleteMerchantPolicy", "delete_policy_permanent", attrs, () -> {
            log.info("🧨 Permanently deleting merchant policy id={}", id);

            try {
                merchantPolicyCommandRepository.deletePermanent(String.valueOf(id));
                return ApiResponse.<Boolean>builder()
                        .status("success")
                        .message("🧨 Merchant policy permanently deleted!")
                        .data(true)
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to permanently delete merchant policy id={}", id, e);
                return ApiResponse.<Boolean>builder()
                        .status("failed")
                        .message("Failed to permanently delete merchant policy")
                        .data(false)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<Boolean> restoreAll() {
        return tracingMetrics.traceAndMeasure("restoreAllMerchantPolicies", "restore_all_policies", () -> {
            log.info("🔄 Restoring ALL trashed merchant policies");

            try {
                merchantPolicyCommandRepository.restoreAllDeleted();
                return ApiResponse.<Boolean>builder()
                        .status("success")
                        .message("🔄 All merchant policies restored successfully!")
                        .data(true)
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to restore all merchant policies", e);
                return ApiResponse.<Boolean>builder()
                        .status("failed")
                        .message("Failed to restore all merchant policies")
                        .data(false)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<Boolean> deleteAll() {
        return tracingMetrics.traceAndMeasure("deleteAllMerchantPolicies", "delete_all_policies_permanent", () -> {
            log.info("💣 Permanently deleting ALL trashed merchant policies");

            try {
                merchantPolicyCommandRepository.deleteAllDeleted();
                return ApiResponse.<Boolean>builder()
                        .status("success")
                        .message("💣 All merchant policies permanently deleted!")
                        .data(true)
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to delete all merchant policies", e);
                return ApiResponse.<Boolean>builder()
                        .status("failed")
                        .message("Failed to delete all merchant policies")
                        .data(false)
                        .build();
            }
        });
    }

    private boolean merchantExists(Integer merchantId) {
        if (merchantId == null) {
            return false;
        }
        try {
            merchantPort.findById(merchantId);
            return true;
        } catch (ResourceNotFoundException e) {
            return false;
        }
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
