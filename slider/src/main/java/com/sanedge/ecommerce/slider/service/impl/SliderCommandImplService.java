package com.sanedge.ecommerce.slider.service.impl;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.observability.TracingMetrics;
import com.sanedge.ecommerce.slider.domain.requests.CreateSliderRequest;
import com.sanedge.ecommerce.slider.domain.requests.UpdateSliderRequest;
import com.sanedge.ecommerce.slider.domain.responses.SliderResponse;
import com.sanedge.ecommerce.slider.domain.responses.SliderResponseDeleteAt;
import com.sanedge.ecommerce.slider.models.Slider;
import com.sanedge.ecommerce.slider.repository.SliderCommandRepository;
import com.sanedge.ecommerce.slider.repository.SliderQueryRepository;
import com.sanedge.ecommerce.slider.service.SliderCommandService;

import io.opentelemetry.api.common.Attributes;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class SliderCommandImplService implements SliderCommandService {

    private final SliderCommandRepository sliderCommandRepository;
    private final SliderQueryRepository sliderQueryRepository;
    private final Validator validator;
    private final TracingMetrics tracingMetrics;

    @Override
    public ApiResponse<SliderResponse> createSlider(CreateSliderRequest req) {
        Attributes attrs = Attributes.builder()
                .put("slider.name", req.getNama() != null ? req.getNama() : "")
                .build();

        return tracingMetrics.traceAndMeasure("createSlider", "create_slider", attrs, () -> {
            try {
                log.info("🆕 Creating slider: {}", req.getNama());
                validateRequest(req);

                Slider slider = new Slider();
                slider.setName(req.getNama());
                slider.setImage(req.getFilePath());

                Slider saved = sliderCommandRepository.save(slider);

                return ApiResponse.<SliderResponse>builder()
                        .status("success")
                        .message("Slider created successfully")
                        .data(SliderResponse.from(saved))
                        .build();

            } catch (Exception e) {
                log.error("💥 Failed to create slider: {}", e.getMessage(), e);
                return ApiResponse.<SliderResponse>builder()
                        .status("error")
                        .message("Internal error while creating slider")
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<SliderResponse> updateSlider(UpdateSliderRequest req) {
        Attributes attrs = Attributes.builder()
                .put("slider.id", String.valueOf(req.getId()))
                .build();

        return tracingMetrics.traceAndMeasure("updateSlider", "update_slider", attrs, () -> {
            try {
                log.info("✏️ Updating slider ID: {}", req.getId());
                validateRequest(req);

                Slider slider = sliderQueryRepository.findById(String.valueOf(req.getId()))
                        .orElse(null);
                if (slider == null) {
                    return ApiResponse.<SliderResponse>builder()
                            .status("error")
                            .message("Slider not found")
                            .build();
                }

                if (req.getFilePath() != null) {
                    slider.setImage(req.getFilePath());
                }

                slider.setName(req.getNama());
                Slider updated = sliderCommandRepository.save(slider);

                return ApiResponse.<SliderResponse>builder()
                        .status("success")
                        .message("Slider updated successfully")
                        .data(SliderResponse.from(updated))
                        .build();

            } catch (Exception e) {
                log.error("💥 Failed to update slider: {}", e.getMessage(), e);
                return ApiResponse.<SliderResponse>builder()
                        .status("error")
                        .message("Internal error while updating slider")
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<SliderResponseDeleteAt> trashedSlider(Integer sliderId) {
        Attributes attrs = Attributes.builder()
                .put("slider.id", String.valueOf(sliderId))
                .build();

        return tracingMetrics.traceAndMeasure("trashedSlider", "trash_slider", attrs, () -> {
            try {
                log.info("🗑️ Trashing slider id={}", sliderId);
                Slider slider = sliderCommandRepository.trashed(String.valueOf(sliderId));

                return ApiResponse.<SliderResponseDeleteAt>builder()
                        .status("success")
                        .message("🗑️ Slider trashed successfully!")
                        .data(SliderResponseDeleteAt.from(slider))
                        .build();

            } catch (Exception e) {
                log.error("💥 Failed to trash slider id={}: {}", sliderId, e.getMessage(), e);
                return ApiResponse.<SliderResponseDeleteAt>builder()
                        .status("error")
                        .message("Failed to trash slider")
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<SliderResponseDeleteAt> restoreSlider(Integer sliderId) {
        Attributes attrs = Attributes.builder()
                .put("slider.id", String.valueOf(sliderId))
                .build();

        return tracingMetrics.traceAndMeasure("restoreSlider", "restore_slider", attrs, () -> {
            try {
                log.info("♻️ Restoring slider id={}", sliderId);
                Slider slider = sliderCommandRepository.restore(String.valueOf(sliderId));

                return ApiResponse.<SliderResponseDeleteAt>builder()
                        .status("success")
                        .message("♻️ Slider restored successfully!")
                        .data(SliderResponseDeleteAt.from(slider))
                        .build();

            } catch (Exception e) {
                log.error("💥 Failed to restore slider id={}: {}", sliderId, e.getMessage(), e);
                return ApiResponse.<SliderResponseDeleteAt>builder()
                        .status("error")
                        .message("Failed to restore slider")
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<Boolean> deleteSliderPermanent(Integer sliderId) {
        Attributes attrs = Attributes.builder()
                .put("slider.id", String.valueOf(sliderId))
                .build();

        return tracingMetrics.traceAndMeasure("deleteSliderPermanent", "delete_slider_permanent", attrs, () -> {
            try {
                log.info("🧨 Permanently deleting slider id={}", sliderId);
                sliderCommandRepository.deletePermanent(String.valueOf(sliderId));

                return ApiResponse.<Boolean>builder()
                        .status("success")
                        .message("🧨 Slider permanently deleted!")
                        .data(true)
                        .build();

            } catch (Exception e) {
                log.error("💥 Failed to permanently delete slider id={}: {}", sliderId, e.getMessage(), e);
                return ApiResponse.<Boolean>builder()
                        .status("error")
                        .message("Failed to permanently delete slider")
                        .data(false)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<Boolean> restoreAllSliders() {
        return tracingMetrics.traceAndMeasure("restoreAllSliders", "restore_all_sliders", () -> {
            try {
                log.info("🔄 Restoring ALL trashed sliders");
                sliderCommandRepository.restoreAllDeleted();

                return ApiResponse.<Boolean>builder()
                        .status("success")
                        .message("🔄 All sliders restored successfully!")
                        .data(true)
                        .build();

            } catch (Exception e) {
                log.error("💥 Failed to restore all sliders: {}", e.getMessage(), e);
                return ApiResponse.<Boolean>builder()
                        .status("error")
                        .message("Failed to restore all sliders")
                        .data(false)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<Boolean> deleteAllSlidersPermanent() {
        return tracingMetrics.traceAndMeasure("deleteAllSlidersPermanent", "delete_all_sliders_permanent", () -> {
            try {
                log.info("💣 Permanently deleting ALL trashed sliders");
                sliderCommandRepository.deleteAllDeleted();

                return ApiResponse.<Boolean>builder()
                        .status("success")
                        .message("💣 All sliders permanently deleted!")
                        .data(true)
                        .build();

            } catch (Exception e) {
                log.error("💥 Failed to delete all sliders: {}", e.getMessage(), e);
                return ApiResponse.<Boolean>builder()
                        .status("error")
                        .message("Failed to delete all sliders")
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
            log.warn("⚠️ Validation failed: {}", sb);
            throw new ConstraintViolationException("Validation failed: " + sb, violations);
        }
    }
}
