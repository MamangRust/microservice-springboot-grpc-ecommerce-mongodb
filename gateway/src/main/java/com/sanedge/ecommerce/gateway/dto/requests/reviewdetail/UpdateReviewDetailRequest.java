package com.sanedge.ecommerce.gateway.dto.requests.reviewdetail;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Request untuk memperbarui detail review yang sudah ada")
public class UpdateReviewDetailRequest {

    @NotNull(message = "ID detail review wajib diisi")
    @Schema(description = "ID detail review", example = "303", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer reviewDetailId;

    @NotBlank(message = "Tipe media wajib diisi")
    @Schema(description = "Tipe media", allowableValues = { "image",
            "video" }, example = "video", requiredMode = Schema.RequiredMode.REQUIRED)
    private String type;

    @NotNull(message = "File wajib diunggah")
    @Schema(description = "File media (gambar atau video)", type = "string", format = "binary", requiredMode = Schema.RequiredMode.REQUIRED)
    private MultipartFile file;

    @NotBlank(message = "Caption wajib diisi")
    @Schema(description = "Keterangan media", example = "Video unboxing", requiredMode = Schema.RequiredMode.REQUIRED)
    private String caption;
}