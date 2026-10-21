package com.sanedge.ecommerce.review.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.review.models.Review;

@Repository
public interface ReviewQueryRepository extends MongoRepository<Review, String>, ReviewQueryRepositoryCustom {
    @Query("""
            { 'deleted_at': null, '$or': [
                { 'name': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                { 'comment': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
            ] }
            """)
    Page<Review> findReviews(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
            { 'deleted_at': null, '$or': [
                { 'name': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                { 'comment': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
            ] }
            """)
    Page<Review> findActiveReviews(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
            { 'deleted_at': { '$ne': null }, '$or': [
                { 'name': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                { 'comment': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
            ] }
            """)
    Page<Review> findTrashedReviews(@Param("keyword") String keyword, Pageable pageable);

    @Query("{ 'review_id': ?0, 'deleted_at': null }")
    Optional<Review> findReviewById(@Param("reviewId") String reviewId);
}
