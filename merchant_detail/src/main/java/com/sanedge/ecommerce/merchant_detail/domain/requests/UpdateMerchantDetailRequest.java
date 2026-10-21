package com.sanedge.ecommerce.merchant_detail.domain.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateMerchantDetailRequest {

    @NotNull(message = "ID merchant detail wajib diisi")
    private Integer merchantDetailId;

    @NotBlank(message = "Nama merchant wajib diisi")
    private String displayName;

    @NotBlank(message = "Cover image wajib diunggah")
    private String coverImageUrl;

    @NotBlank(message = "Logo wajib diunggah")
    private String logoUrl;

    @NotBlank(message = "Deskripsi singkat wajib diisi")
    private String shortDescription;

    @Pattern(regexp = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$", message = "Website harus berupa URL yang valid")
    private String websiteUrl;
}
