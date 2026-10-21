package com.sanedge.ecommerce.gateway.dto.requests.product;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request untuk memperbarui produk")
public class UpdateProductRequest {

    @NotNull
    @Schema(description = "ID produk yang akan diperbarui", example = "1")
    private Integer productId;

    @NotNull
    @Schema(description = "ID merchant", example = "1")
    private Integer merchantId;

    @NotNull
    @Schema(description = "ID kategori produk", example = "2")
    private Integer categoryId;

    @NotNull
    @Size(min = 1)
    @Schema(description = "Nama produk", example = "Kopi Arabica")
    private String name;

    @NotNull
    @Schema(description = "Deskripsi produk", example = "Kopi Arabica spesial grade")
    private String description;

    @NotNull
    @Min(0)
    @Schema(description = "Harga produk", example = "100000")
    private Integer price;

    @NotNull
    @Min(0)
    @Schema(description = "Stok produk", example = "50")
    private Integer countInStock;

    @NotNull
    @Schema(description = "Brand produk", example = "SanEdge Coffee")
    private String brand;

    @NotNull
    @Min(0)
    @Schema(description = "Berat produk dalam gram", example = "500")
    private Integer weight;

    @NotNull
    @Min(0)
    @Schema(description = "Rating produk", example = "5")
    private Integer rating;

    @NotNull
    @Schema(description = "Slug produk", example = "kopi-arabica")
    private String slugProduct;

    @Schema(description = "Gambar produk baru (opsional)", type = "string", format = "binary")
    private MultipartFile imageProduct;
}