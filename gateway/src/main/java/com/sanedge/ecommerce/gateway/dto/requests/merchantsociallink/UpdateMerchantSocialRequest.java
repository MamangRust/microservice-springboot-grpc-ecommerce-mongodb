package com.sanedge.ecommerce.gateway.dto.requests.merchantsociallink;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(name = "UpdateMerchantSocialRequest", description = "Request untuk mengupdate akun sosial media merchant")
public class UpdateMerchantSocialRequest {

    @NotNull(message = "ID akun sosial wajib diisi")
    @Schema(description = "ID akun sosial yang akan diupdate", required = true, example = "456")
    private Integer id;

    @NotNull(message = "ID detail merchant wajib diisi")
    @Schema(description = "ID detail merchant", required = true, example = "123")
    private Integer merchantDetailId;

    @NotBlank(message = "Nama platform wajib diisi")
    @Schema(description = "Nama platform sosial media", required = true, example = "Instagram")
    private String platform;

    @NotBlank(message = "URL wajib diisi")
    @Pattern(regexp = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$", message = "URL harus berupa tautan yang valid")
    @Schema(description = "URL akun sosial media merchant", required = true, example = "https://instagram.com/myshop")
    private String url;
}
