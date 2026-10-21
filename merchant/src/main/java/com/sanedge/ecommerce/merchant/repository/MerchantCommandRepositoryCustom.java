package com.sanedge.ecommerce.merchant.repository;

import com.sanedge.ecommerce.merchant.models.Merchant;

public interface MerchantCommandRepositoryCustom {
    Merchant trashed(String merchantId);

    Merchant restore(String merchantId);

    boolean deletePermanent(String merchantId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}
