package com.sanedge.ecommerce.gateway.dto.requests.banner;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Pattern;

import lombok.Data;

@Data
@Schema(description = "Request body untuk mengupdate banner")
public class UpdateBannerRequest {
    @Null
    @Schema(description = "ID banner yang akan diupdate", example = "1")
    private Integer bannerID;

    @NotBlank
    @Schema(description = "Nama banner", example = "Promo Diskon 50%")
    private String name;

    @NotBlank
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}")
    @Schema(description = "Tanggal mulai banner (YYYY-MM-DD)", example = "2025-09-28")
    private String startDate;

    @NotBlank
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}")
    @Schema(description = "Tanggal akhir banner (YYYY-MM-DD)", example = "2025-10-28")
    private String endDate;

    @NotBlank
    @Pattern(regexp = "\\d{2}:\\d{2}")
    @Schema(description = "Waktu mulai banner (HH:mm)", example = "08:00")
    private String startTime;

    @NotBlank
    @Pattern(regexp = "\\d{2}:\\d{2}")
    @Schema(description = "Waktu akhir banner (HH:mm)", example = "22:00")
    private String endTime;

    @NotNull
    @Schema(description = "Status aktif banner", example = "true")
    private Boolean isActive;
}
