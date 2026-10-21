package com.sanedge.ecommerce.merchant_detail.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sanedge.ecommerce.merchant_detail.models.MerchantDetailsRelation;

public interface MerchantDetailQueryRepositoryCustom {
    Page<MerchantDetailsRelation> findAllWithSocialLinks(String keyword, Pageable pageable);

    Page<MerchantDetailsRelation> findActiveWithSocialLinks(String keyword, Pageable pageable);

    Page<MerchantDetailsRelation> findTrashedWithSocialLinks(String keyword, Pageable pageable);

    Optional<MerchantDetailsRelation> findByIdWithSocialLinks(String merchantDetailId);
}
