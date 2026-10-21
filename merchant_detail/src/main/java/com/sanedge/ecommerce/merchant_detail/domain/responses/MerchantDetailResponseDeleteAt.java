package com.sanedge.ecommerce.merchant_detail.domain.responses;

import com.sanedge.ecommerce.common.utils.Ids;
import com.sanedge.ecommerce.merchant_detail.models.MerchantDetail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantDetailResponseDeleteAt {
    private Long id;
    private Integer merchantId;
    private String displayName;
    private String coverImageUrl;
    private String logoUrl;
    private String shortDescription;
    private String websiteUrl;
    private String createdAt;
    private String updatedAt;
    private String deletedAt;

    public static MerchantDetailResponseDeleteAt from(MerchantDetail entity) {
        return MerchantDetailResponseDeleteAt.builder()
                .id(Ids.toLong(entity.getMerchantDetailId()))
                .merchantId(entity.getMerchantId())
                .displayName(entity.getDisplayName())
                .coverImageUrl(entity.getCoverImageUrl())
                .logoUrl(entity.getLogoUrl())
                .shortDescription(entity.getShortDescription())
                .websiteUrl(entity.getWebsiteUrl())
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null)
                .updatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null)
                .deletedAt(entity.getDeletedAt() != null ? entity.getDeletedAt().toString() : null)
                .build();
    }
}
