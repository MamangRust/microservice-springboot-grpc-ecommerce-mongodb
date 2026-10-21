package com.sanedge.ecommerce.merchant_business.repository;

import com.sanedge.ecommerce.merchant_business.models.MerchantBusinessInformation;

public interface MerchantBusinessCommandRepositoryCustom {
    MerchantBusinessInformation trashed(String merchantBusinessInfoId);

    MerchantBusinessInformation restore(String merchantBusinessInfoId);

    boolean deletePermanent(String merchantBusinessInfoId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}
