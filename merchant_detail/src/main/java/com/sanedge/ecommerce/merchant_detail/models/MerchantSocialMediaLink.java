package com.sanedge.ecommerce.merchant_detail.models;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Plain (non-persisted) projection of a merchant social media link, used only
 * to deserialize the {@code json_agg(json_build_object(...))} column returned by
 * the native merchant-detail query. The owning table lives in the
 * merchant_social_link module, so this module keeps no entity mapping for it.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MerchantSocialMediaLink {
    private Long merchantSocialId;
    private Integer merchantDetailId;
    private String platform;
    private String url;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
