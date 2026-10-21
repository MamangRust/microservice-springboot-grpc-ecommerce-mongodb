package com.sanedge.ecommerce.product.domain.responses;

import com.sanedge.ecommerce.common.utils.Ids;
import com.sanedge.ecommerce.product.models.Product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDeleteAt {
    private Long id;
    private Integer merchantId;
    private Integer categoryId;
    private String name;
    private String description;
    private Integer price;
    private Integer countInStock;
    private String brand;
    private Integer weight;
    private Float rating;
    private String slugProduct;
    private String imageProduct;
    private String createdAt;
    private String updatedAt;
    private String deletedAt;

    public static ProductResponseDeleteAt from(Product entity) {
        return ProductResponseDeleteAt.builder()
                .id(Ids.toLong(entity.getProductId()))
                .merchantId(entity.getMerchantId())
                .categoryId(entity.getCategoryId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .countInStock(entity.getCountInStock())
                .brand(entity.getBrand())
                .weight(entity.getWeight())
                .rating(entity.getRating())
                .slugProduct(entity.getSlugProduct())
                .imageProduct(entity.getImageProduct())
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null)
                .updatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null)
                .deletedAt(entity.getDeletedAt() != null ? entity.getDeletedAt().toString() : null)
                .build();
    }
}
