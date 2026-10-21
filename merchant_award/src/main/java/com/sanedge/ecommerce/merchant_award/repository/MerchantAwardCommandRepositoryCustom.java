package com.sanedge.ecommerce.merchant_award.repository;

import com.sanedge.ecommerce.merchant_award.models.MerchantCertificationAndAward;

public interface MerchantAwardCommandRepositoryCustom {
    MerchantCertificationAndAward trashed(String merchantCertificationId);

    MerchantCertificationAndAward restore(String merchantCertificationId);

    boolean deletePermanent(String merchantCertificationId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}
