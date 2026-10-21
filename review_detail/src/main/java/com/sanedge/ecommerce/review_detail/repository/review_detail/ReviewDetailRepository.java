package com.sanedge.ecommerce.review_detail.repository.review_detail;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.review_detail.models.review.ReviewDetail;

@Repository
public interface ReviewDetailRepository
        extends MongoRepository<ReviewDetail, String>, ReviewDetailRepositoryCustom {

    @Query("""
                { '$or': [
                    { 'caption': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                    { 'url': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
                ] }
                """)
    Page<ReviewDetail> findDetails(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
                { 'deleted_at': null, '$or': [
                    { 'caption': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                    { 'url': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
                ] }
                """)
    Page<ReviewDetail> findActiveDetails(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
                { 'deleted_at': { '$ne': null }, '$or': [
                    { 'caption': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                    { 'url': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
                ] }
                """)
    Page<ReviewDetail> findTrashedDetails(@Param("keyword") String keyword, Pageable pageable);
}
