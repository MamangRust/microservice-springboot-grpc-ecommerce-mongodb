package com.sanedge.ecommerce.gateway.dto.requests.merchantdetail;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(name = "CreateMerchantDetailRequest", description = "Request untuk membuat detail merchant baru")
public class CreateMerchantDetailRequest {

    @NotNull(message = "ID merchant wajib diisi")
    @Schema(description = "ID merchant", required = true, example = "123")
    private Integer merchantId;

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