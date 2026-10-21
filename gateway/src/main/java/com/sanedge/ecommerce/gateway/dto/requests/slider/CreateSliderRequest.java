package com.sanedge.ecommerce.gateway.dto.requests.slider;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Request untuk membuat slider baru")
public class CreateSliderRequest {

    @NotNull
    @Schema(description = "Nama slider", example = "Promo Akhir Tahun")
    private String nama;

    @NotNull
    @Schema(description = "Gambar slider", type = "string", format = "binary")
    private MultipartFile filePath;
}