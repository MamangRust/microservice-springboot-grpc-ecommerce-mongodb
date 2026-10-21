package com.sanedge.ecommerce.merchant_social_link.repository;

import java.util.Optional;

import com.sanedge.ecommerce.merchant_social_link.models.MerchantSocialMediaLink;

public interface MerchantSocialMediaLinkRepositoryCustom {
    MerchantSocialMediaLink trashed(String merchantSocialId);

    MerchantSocialMediaLink restore(String merchantSocialId);

    boolean deletePermanent(String merchantSocialId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();

    Optional<MerchantSocialMediaLink> findByMerchantDetailIdAndPlatform(Integer merchantDetailId,
            String platform);
}
