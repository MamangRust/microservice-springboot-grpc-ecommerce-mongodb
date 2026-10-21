package com.sanedge.ecommerce.merchant_policy.repository;

import com.sanedge.ecommerce.merchant_policy.models.MerchantPolicy;

public interface MerchantPolicyCommandRepositoryCustom {
    MerchantPolicy trashed(String merchantPolicyId);

    MerchantPolicy restore(String merchantPolicyId);

    boolean deletePermanent(String merchantPolicyId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}
