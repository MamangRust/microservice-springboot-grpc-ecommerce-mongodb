package com.sanedge.ecommerce.gateway.dto.requests.merchantdetail;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(name = "UpdateMerchantDetailRequest", description = "Request untuk memperbarui detail merchant")
public class UpdateMerchantDetailRequest {

    @NotNull(message = "ID merchant detail wajib diisi")
    @Schema(description = "ID merchant detail", required = true, example = "456")
    private Integer merchantDetailId;

    @NotBlank(message = "Nama merchant wajib diisi")
    @Schema(description = "Nama merchant", required = true, example = "Toko Saya")
    private String displayName;

    @NotNull(message = "Cover image wajib diunggah")
    @Schema(description = "File gambar cover merchant", type = "string", format = "binary", required = true)
    private MultipartFile coverImageUrl;

    @NotNull(message = "Logo wajib diunggah")
    @Schema(description = "File logo merchant", type = "string", format = "binary", required = true)
    private MultipartFile logoUrl;

    @NotBlank(message = "Deskripsi singkat wajib diisi")
    @Schema(description = "Deskripsi singkat merchant", required = true, example = "Kami menjual produk keren")
    private String shortDescription;

    @Pattern(regexp = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$", message = "Website harus berupa URL yang valid")
    @Schema(description = "Website merchant (opsional)", required = false, example = "https://contoh.com")
    private String websiteUrl;
}
