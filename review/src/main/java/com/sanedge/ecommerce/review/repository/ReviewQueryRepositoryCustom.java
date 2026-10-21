package com.sanedge.ecommerce.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sanedge.ecommerce.review.models.ReviewRelationsDetail;

public interface ReviewQueryRepositoryCustom {
    Page<ReviewRelationsDetail> findByMerchantId(Integer merchantId, Integer rating, String search, Pageable pageable);

    Page<ReviewRelationsDetail> findByProductId(Integer productId, Integer rating, String search, Pageable pageable);
}
