package com.sanedge.ecommerce.merchant_social_link.domain.responses;

import com.sanedge.ecommerce.common.utils.Ids;
import com.sanedge.ecommerce.merchant_social_link.models.MerchantSocialMediaLink;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantSocialMediaLinkResponseDeleteAt {
    private Long id;
    private String platform;
    private String url;
    private String createdAt;
    private String updatedAt;
    private String deletedAt;

    public static MerchantSocialMediaLinkResponseDeleteAt from(MerchantSocialMediaLink entity) {
        return MerchantSocialMediaLinkResponseDeleteAt.builder()
                .id(Ids.toLong(entity.getMerchantSocialId()))
                .platform(entity.getPlatform())
                .url(entity.getUrl())
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null)
                .updatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null)
                .deletedAt(entity.getDeletedAt() != null ? entity.getDeletedAt().toString() : null)
                .build();
    }
}
