package com.sanedge.ecommerce.merchant.service.impl;

import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.enums.Status;
import com.sanedge.ecommerce.common.exception.ResourceNotFoundException;
import com.sanedge.ecommerce.common.observability.TracingMetrics;
import com.sanedge.ecommerce.common.outbox.OutboxEventFactory;
import com.sanedge.ecommerce.merchant.domain.requests.CreateMerchantRequest;
import com.sanedge.ecommerce.merchant.domain.requests.UpdateMerchantRequest;
import com.sanedge.ecommerce.merchant.domain.responses.MerchantResponse;
import com.sanedge.ecommerce.merchant.domain.responses.MerchantResponseDeleteAt;
import com.sanedge.ecommerce.merchant.models.Merchant;
import com.sanedge.ecommerce.merchant.models.MerchantOutbox;
import com.sanedge.ecommerce.merchant.repository.MerchantCommandRepository;
import com.sanedge.ecommerce.merchant.repository.MerchantOutboxRepository;
import com.sanedge.ecommerce.merchant.repository.MerchantQueryRepository;
import com.sanedge.ecommerce.merchant.service.MerchantCommandService;
import com.sanedge.ecommerce.common.adapter.user.UserPort;
import io.opentelemetry.api.common.Attributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MerchantCommandImplService implements MerchantCommandService {
    private static final String TOPIC_MERCHANT_CREATE = "email-service-topic-merchant-create";
    private static final String TOPIC_MERCHANT_UPDATE_STATUS = "email-service-topic-merchant-update-status";

    private final MerchantCommandRepository merchantCommandRepository;
    private final MerchantQueryRepository merchantQueryRepository;
    private final UserPort userPort;
    private final TracingMetrics tracingMetrics;
    private final MerchantOutboxRepository merchantOutboxRepository;

    @Override
    public ApiResponse<MerchantResponse> createMerchant(CreateMerchantRequest req) {
        Attributes attrs = Attributes.builder()
                .put("merchant.name", req.getName() != null ? req.getName() : "")
                .put("user.id", String.valueOf(req.getUserId()))
                .build();

        return tracingMetrics.traceAndMeasure("createMerchant", "create_merchant", attrs, () -> {
            log.info("🆕 Creating merchant: {}", req.getName());
            try {
                if (req.getUserId() != null) {
                    assertUserExists(req.getUserId());
                }

                Merchant merchant = Merchant.fromCreateRequest(req);
                Merchant saved = merchantCommandRepository.save(merchant);


                sendMerchantEmail(saved, TOPIC_MERCHANT_CREATE, "Merchant Registered Successfully",
                        String.format(
                                "Hello,\n\nYour merchant account '%s' has been created and is pending review.\n\nRegards,\nSupport Team",
                                saved.getName()));

                return ApiResponse.<MerchantResponse>builder()
                        .status("success")
                        .message("Merchant created successfully")
                        .data(MerchantResponse.from(saved))
                        .build();
            } catch (ResourceNotFoundException ex) {
                log.warn("❌ Resource not found: {}", ex.getMessage());
                return ApiResponse.<MerchantResponse>builder()
                        .status("error")
                        .message("Resource not found")
                        .data(null)
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to create merchant: {}", req.getName(), e);
                return ApiResponse.<MerchantResponse>builder()
                        .status("error")
                        .message("Failed to create merchant")
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<MerchantResponse> updateMerchant(UpdateMerchantRequest req) {
        Attributes attrs = Attributes.builder()
                .put("merchant.id", String.valueOf(req.getMerchantId()))
                .build();

        return tracingMetrics.traceAndMeasure("updateMerchant", "update_merchant", attrs, () -> {
            log.info("✏️ Updating merchant ID: {}", req.getMerchantId());
            try {
                Merchant merchant = merchantQueryRepository.findById(String.valueOf(req.getMerchantId()))
                        .orElseThrow(() -> new ResourceNotFoundException("Merchant not found"));

                merchant.updateFromRequest(req);
                Merchant updated = merchantCommandRepository.save(merchant);

                return ApiResponse.<MerchantResponse>builder()
                        .status("success")
                        .message("Merchant updated successfully")
                        .data(MerchantResponse.from(updated))
                        .build();
            } catch (ResourceNotFoundException ex) {
                log.warn("❌ Resource not found: {}", ex.getMessage());
                return ApiResponse.<MerchantResponse>builder()
                        .status("error")
                        .message("Resource not found")
                        .data(null)
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to update merchant ID: {}", req.getMerchantId(), e);
                return ApiResponse.<MerchantResponse>builder()
                        .status("error")
                        .message("Failed to update merchant")
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<MerchantResponse> updateMerchantStatus(Integer merchantId, String status) {
        Attributes attrs = Attributes.builder()
                .put("merchant.id", String.valueOf(merchantId))
                .build();

        return tracingMetrics.traceAndMeasure("updateMerchantStatus", "update_merchant_status", attrs, () -> {
            log.info("✏️ Updating merchant status ID: {}", merchantId);
            try {
                Merchant merchant = merchantQueryRepository.findById(String.valueOf(merchantId))
                        .orElseThrow(() -> new ResourceNotFoundException("Merchant not found"));

                try {
                    merchant.setStatus(Status.valueOf(status.toLowerCase()));
                } catch (IllegalArgumentException e) {
                    merchant.setStatus(Status.PENDING);
                }

                Merchant updated = merchantCommandRepository.save(merchant);


                sendMerchantEmail(updated, TOPIC_MERCHANT_UPDATE_STATUS, "Merchant Status Updated",
                        String.format(
                                "Hello,\n\nYour merchant account '%s' status has changed. New status: %s.\n\nRegards,\nSupport Team",
                                updated.getName(), updated.getStatus()));


                return ApiResponse.<MerchantResponse>builder()
                        .status("success")
                        .message("Merchant status updated successfully")
                        .data(MerchantResponse.from(updated))
                        .build();
            } catch (ResourceNotFoundException ex) {
                log.warn("❌ Resource not found: {}", ex.getMessage());
                return ApiResponse.<MerchantResponse>builder()
                        .status("error")
                        .message("Resource not found")
                        .data(null)
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to update merchant status ID: {}", merchantId, e);
                return ApiResponse.<MerchantResponse>builder()
                        .status("error")
                        .message("Failed to update merchant status")
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<MerchantResponseDeleteAt> trashedMerchant(Integer merchantId) {
        Attributes attrs = Attributes.builder()
                .put("merchant.id", String.valueOf(merchantId))
                .build();

        return tracingMetrics.traceAndMeasure("trashMerchant", "trash_merchant", attrs, () -> {
            log.info("🗑️ Soft deleting merchant ID: {}", merchantId);
            try {
                Merchant merchant = merchantCommandRepository.trashed(String.valueOf(merchantId));
                return ApiResponse.<MerchantResponseDeleteAt>builder()
                        .status("success")
                        .message("Merchant trashed successfully")
                        .data(MerchantResponseDeleteAt.from(merchant))
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to trash merchant ID: {}", merchantId, e);
                return ApiResponse.<MerchantResponseDeleteAt>builder()
                        .status("error")
                        .message("Failed to trash merchant")
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<MerchantResponseDeleteAt> restoreMerchant(Integer merchantId) {
        Attributes attrs = Attributes.builder()
                .put("merchant.id", String.valueOf(merchantId))
                .build();

        return tracingMetrics.traceAndMeasure("restoreMerchant", "restore_merchant", attrs, () -> {
            log.info("♻️ Restoring merchant ID: {}", merchantId);
            try {
                Merchant merchant = merchantCommandRepository.restore(String.valueOf(merchantId));
                return ApiResponse.<MerchantResponseDeleteAt>builder()
                        .status("success")
                        .message("Merchant restored successfully")
                        .data(MerchantResponseDeleteAt.from(merchant))
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to restore merchant ID: {}", merchantId, e);
                return ApiResponse.<MerchantResponseDeleteAt>builder()
                        .status("error")
                        .message("Failed to restore merchant")
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<Boolean> deleteMerchantPermanent(Integer merchantId) {
        Attributes attrs = Attributes.builder()
                .put("merchant.id", String.valueOf(merchantId))
                .build();

        return tracingMetrics.traceAndMeasure("deleteMerchant", "delete_merchant", attrs, () -> {
            log.warn("❌ Permanently deleting merchant ID: {}", merchantId);
            try {
                boolean deleted = merchantCommandRepository.deletePermanent(String.valueOf(merchantId));
                return ApiResponse.<Boolean>builder()
                        .status("success")
                        .message("Merchant permanently deleted")
                        .data(deleted)
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to permanently delete merchant ID: {}", merchantId, e);
                return ApiResponse.<Boolean>builder()
                        .status("error")
                        .message("Failed to permanently delete merchant")
                        .data(false)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<Boolean> restoreAllMerchant() {
        return tracingMetrics.traceAndMeasure("restoreAllMerchants", "restore_all_merchants", () -> {
            log.info("♻️ Restoring all trashed merchants");
            try {
                boolean restored = merchantCommandRepository.restoreAllDeleted();
                return ApiResponse.<Boolean>builder()
                        .status("success")
                        .message("All trashed merchants restored")
                        .data(restored)
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to restore all trashed merchants", e);
                return ApiResponse.<Boolean>builder()
                        .status("error")
                        .message("Failed to restore all trashed merchants")
                        .data(false)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<Boolean> deleteAllMerchantPermanent() {
        return tracingMetrics.traceAndMeasure("deleteAllMerchants", "delete_all_merchants", () -> {
            log.warn("❌ Permanently deleting all trashed merchants");
            try {
                boolean deleted = merchantCommandRepository.deleteAllDeleted();
                return ApiResponse.<Boolean>builder()
                        .status("success")
                        .message("All trashed merchants permanently deleted")
                        .data(deleted)
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

    private void assertUserExists(Integer userId) {
        userPort.findById(userId);
    }

    private void sendMerchantEmail(Merchant merchant, String topic, String subject, String body) {
        try {
            String email = merchant.getContactEmail() != null ? merchant.getContactEmail() : "";
            OutboxEventFactory.Envelope envelope = OutboxEventFactory.email(topic, email, subject, body);
            MerchantOutbox event = new MerchantOutbox();
            event.setId(envelope.eventId());
            event.setTopic(topic);
            event.setEventKey(email);
            event.setPayload(envelope.payload());
            merchantOutboxRepository.enqueue(event);
        } catch (RuntimeException e) {
            log.warn("⚠️ Failed to enqueue merchant email | merchantId={}: {}",
                    merchant.getMerchantId(), e.getMessage());
        }
    }
}
