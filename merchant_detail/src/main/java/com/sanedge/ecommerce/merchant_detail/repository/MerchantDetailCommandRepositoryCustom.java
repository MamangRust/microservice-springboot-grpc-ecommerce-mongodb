package com.sanedge.ecommerce.merchant_detail.repository;

import com.sanedge.ecommerce.merchant_detail.models.MerchantDetail;

public interface MerchantDetailCommandRepositoryCustom {
    MerchantDetail trashed(String merchantDetailId);

    MerchantDetail restore(String merchantDetailId);

    boolean deletePermanent(String merchantDetailId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}
