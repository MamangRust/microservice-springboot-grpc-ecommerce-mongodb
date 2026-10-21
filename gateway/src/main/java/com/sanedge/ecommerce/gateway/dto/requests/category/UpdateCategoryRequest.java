package com.sanedge.ecommerce.gateway.dto.requests.category;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import lombok.Data;

@Data
@Schema(description = "Request untuk memperbarui kategori")
public class UpdateCategoryRequest {

    @Null
    @Schema(description = "ID kategori yang akan diperbarui", example = "123")
    private Integer categoryId;

    @NotBlank
    @Schema(description = "Nama kategori", example = "Elektronik")
    private String name;

    @NotBlank
    @Schema(description = "Deskripsi kategori", example = "Kategori perangkat elektronik")
    private String description;

    @NotBlank
    @Schema(description = "Slug kategori", example = "elektronik")
    private String slugCategory;

    @NotBlank
    @Schema(description = "URL atau path gambar kategori", example = "/images/elektronik.png")
    private MultipartFile imageCategory;
}