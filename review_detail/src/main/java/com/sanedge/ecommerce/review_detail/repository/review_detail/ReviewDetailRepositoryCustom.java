package com.sanedge.ecommerce.review_detail.repository.review_detail;

import com.sanedge.ecommerce.review_detail.models.review.ReviewDetail;

public interface ReviewDetailRepositoryCustom {
    ReviewDetail trashed(String reviewDetailId);

    ReviewDetail restore(String reviewDetailId);

    boolean deletePermanent(String reviewDetailId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}
