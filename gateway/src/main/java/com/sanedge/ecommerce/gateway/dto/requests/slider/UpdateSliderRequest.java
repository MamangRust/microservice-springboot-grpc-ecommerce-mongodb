package com.sanedge.ecommerce.gateway.dto.requests.slider;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Request untuk memperbarui slider")
public class UpdateSliderRequest {

    @NotNull
    @Schema(description = "ID slider yang akan diperbarui", example = "1")
    private Integer id;

    @NotNull
    @Schema(description = "Nama slider", example = "Promo Akhir Tahun")
    private String nama;

    @Schema(description = "Gambar slider baru (opsional)", type = "string", format = "binary")
    private MultipartFile filePath;
}