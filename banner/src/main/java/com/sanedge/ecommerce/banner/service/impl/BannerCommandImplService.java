package com.sanedge.ecommerce.banner.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.banner.domain.requests.CreateBannerRequest;
import com.sanedge.ecommerce.banner.domain.requests.UpdateBannerRequest;
import com.sanedge.ecommerce.banner.domain.responses.BannerResponse;
import com.sanedge.ecommerce.banner.domain.responses.BannerResponseDeleteAt;
import com.sanedge.ecommerce.banner.models.Banner;
import com.sanedge.ecommerce.banner.repository.BannerCommandRepository;
import com.sanedge.ecommerce.banner.repository.BannerQueryRepository;
import com.sanedge.ecommerce.banner.service.BannerCommandService;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.exception.ResourceNotFoundException;
import com.sanedge.ecommerce.common.observability.TracingMetrics;

import io.opentelemetry.api.common.Attributes;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class BannerCommandImplService implements BannerCommandService {

    private final BannerQueryRepository bannerQueryRepository;
    private final BannerCommandRepository bannerCommandRepository;
    private final Validator validator;
    private final TracingMetrics tracingMetrics;

    @Override
    public ApiResponse<BannerResponse> createBanner(CreateBannerRequest req) {
        Attributes attrs = Attributes.builder()
                .put("banner.name", req.getName() != null ? req.getName() : "")
                .build();

        return tracingMetrics.traceAndMeasure("createBanner", "create_banner", attrs, () -> {
            try {
                validateRequest(req);

                log.info("🆕 Creating banner name={}", req.getName());

                bannerQueryRepository.findByName(req.getName())
                        .ifPresent(b -> {
                            log.warn("❌ Banner creation failed. Banner name '{}' already exists", req.getName());
                            throw new IllegalArgumentException("Banner with name '" + req.getName() + "' already exists");
                        });

                Banner banner = new Banner();
                banner.setName(req.getName());
                banner.setStartDate(LocalDate.parse(req.getStartDate()));
                banner.setEndDate(LocalDate.parse(req.getEndDate()));
                banner.setStartTime(LocalTime.parse(req.getStartTime()));
                banner.setEndTime(LocalTime.parse(req.getEndTime()));
                banner.setIsActive(req.getIsActive());
                banner.setCreatedAt(LocalDateTime.now());
                banner.setUpdatedAt(LocalDateTime.now());

                Banner savedBanner = bannerCommandRepository.save(banner);
                BannerResponse response = BannerResponse.from(savedBanner);

                log.info("✅ Banner created successfully with id={}", response.getId());

                return ApiResponse.<BannerResponse>builder()
                        .status("success")
                        .message("✅ Banner created successfully!")
                        .data(response)
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to create banner", e);
                return ApiResponse.<BannerResponse>builder()
                        .status("error")
                        .message(e.getMessage())
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<BannerResponse> updateBanner(UpdateBannerRequest req) {
        Attributes attrs = Attributes.builder()
                .put("banner.id", String.valueOf(req.getBannerID()))
                .build();

        return tracingMetrics.traceAndMeasure("updateBanner", "update_banner", attrs, () -> {
            try {
                validateRequest(req);

                if (req.getBannerID() == null) {
                    throw new ResourceNotFoundException("banner_id is required");
                }

                log.info("🔄 Updating banner id={}", req.getBannerID());

                Banner banner = bannerCommandRepository.findById(String.valueOf(req.getBannerID()))
                        .orElseThrow(() -> new ResourceNotFoundException("Banner not found"));

                banner.setName(req.getName());
                banner.setStartDate(LocalDate.parse(req.getStartDate()));
                banner.setEndDate(LocalDate.parse(req.getEndDate()));
                banner.setStartTime(LocalTime.parse(req.getStartTime()));
                banner.setEndTime(LocalTime.parse(req.getEndTime()));
                banner.setIsActive(req.getIsActive());
                banner.setUpdatedAt(LocalDateTime.now());

                Banner updatedBanner = bannerCommandRepository.save(banner);
                BannerResponse response = BannerResponse.from(updatedBanner);

                log.info("✅ Banner updated successfully with id={}", response.getId());

                return ApiResponse.<BannerResponse>builder()
                        .status("success")
                        .message("✅ Banner updated successfully!")
                        .data(response)
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to update banner id={}", req.getBannerID(), e);
                return ApiResponse.<BannerResponse>builder()
                        .status("error")
                        .message(e.getMessage())
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<BannerResponseDeleteAt> trashedBanner(Integer bannerId) {
        Attributes attrs = Attributes.builder()
                .put("banner.id", String.valueOf(bannerId))
                .build();

        return tracingMetrics.traceAndMeasure("trashBanner", "trash_banner", attrs, () -> {
            log.info("🗑️ Trashing banner id={}", bannerId);
            try {
                Banner banner = bannerCommandRepository.trashed(String.valueOf(bannerId));
                return ApiResponse.<BannerResponseDeleteAt>builder()
                        .status("success")
                        .message("🗑️ Banner trashed successfully!")
                        .data(BannerResponseDeleteAt.from(banner))
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to trash banner id={}", bannerId, e);
                return ApiResponse.<BannerResponseDeleteAt>builder()
                        .status("error")
                        .message("Failed to trash banner: " + e.getMessage())
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<BannerResponseDeleteAt> restoreBanner(Integer bannerId) {
        Attributes attrs = Attributes.builder()
                .put("banner.id", String.valueOf(bannerId))
                .build();

        return tracingMetrics.traceAndMeasure("restoreBanner", "restore_banner", attrs, () -> {
            log.info("♻️ Restoring banner id={}", bannerId);
            try {
                Banner banner = bannerCommandRepository.restore(String.valueOf(bannerId));
                return ApiResponse.<BannerResponseDeleteAt>builder()
                        .status("success")
                        .message("♻️ Banner restored successfully!")
                        .data(BannerResponseDeleteAt.from(banner))
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to restore banner id={}", bannerId, e);
                return ApiResponse.<BannerResponseDeleteAt>builder()
                        .status("error")
                        .message("Failed to restore banner: " + e.getMessage())
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<Boolean> deleteBannerPermanent(Integer bannerId) {
        Attributes attrs = Attributes.builder()
                .put("banner.id", String.valueOf(bannerId))
                .build();

        return tracingMetrics.traceAndMeasure("deleteBanner", "delete_banner", attrs, () -> {
            log.info("🧨 Permanently deleting banner id={}", bannerId);
            try {
                bannerCommandRepository.deletePermanent(String.valueOf(bannerId));
                return ApiResponse.<Boolean>builder()
                        .status("success")
                        .message("🧨 Banner permanently deleted!")
                        .data(true)
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to permanently delete banner id={}", bannerId, e);
                return ApiResponse.<Boolean>builder()
                        .status("error")
                        .message("Failed to permanently delete banner: " + e.getMessage())
                        .data(false)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<Boolean> restoreAllBanner() {
        return tracingMetrics.traceAndMeasure("restoreAllBanners", "restore_all_banners", () -> {
            log.info("🔄 Restoring ALL trashed banners");
            try {
                bannerCommandRepository.restoreAllDeleted();
                return ApiResponse.<Boolean>builder()
                        .status("success")
                        .message("🔄 All banners restored successfully!")
                        .data(true)
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to restore all banners", e);
                return ApiResponse.<Boolean>builder()
                        .status("error")
                        .message("Failed to restore all banners: " + e.getMessage())
                        .data(false)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<Boolean> deleteAllBannerPermanent() {
        return tracingMetrics.traceAndMeasure("deleteAllBanners", "delete_all_banners", () -> {
            log.info("💣 Permanently deleting ALL trashed banners");
            try {
                bannerCommandRepository.deleteAllDeleted();
                return ApiResponse.<Boolean>builder()
                        .status("success")
                        .message("💣 All banners permanently deleted!")
                        .data(true)
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to delete all banners", e);
                return ApiResponse.<Boolean>builder()
                        .status("error")
                        .message("Failed to delete all banners: " + e.getMessage())
                        .data(false)
                        .build();
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
