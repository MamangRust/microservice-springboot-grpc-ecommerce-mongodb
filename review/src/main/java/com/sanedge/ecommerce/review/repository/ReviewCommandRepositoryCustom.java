package com.sanedge.ecommerce.review.repository;

import com.sanedge.ecommerce.review.models.Review;

public interface ReviewCommandRepositoryCustom {
    Review trashed(String reviewId);

    Review restore(String reviewId);

    boolean deletePermanent(String reviewId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}
