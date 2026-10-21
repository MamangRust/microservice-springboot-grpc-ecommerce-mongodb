package com.sanedge.ecommerce.merchant_award.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.merchant_award.models.MerchantCertificationAndAward;

@Repository
public interface MerchantAwardQueryRepository
                extends MongoRepository<MerchantCertificationAndAward, String> {
        @Query("""
                        { 'deleted_at': null, '$or': [
                            { 'title': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                            { 'description': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                            { 'issued_by': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                            { 'certificate_url': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
                        ] }
                        """)
        Page<MerchantCertificationAndAward> findMerchantAwards(
                        @Param("keyword") String keyword,
                        Pageable pageable);

        @Query("""
                        { 'deleted_at': null, '$or': [
                            { 'title': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                            { 'description': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                            { 'issued_by': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                            { 'certificate_url': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
                        ] }
                        """)
        Page<MerchantCertificationAndAward> findActiveMerchantAwards(
                        @Param("keyword") String keyword,
                        Pageable pageable);

        @Query("""
                        { 'deleted_at': { '$ne': null }, '$or': [
                            { 'title': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                            { 'description': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                            { 'issued_by': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                            { 'certificate_url': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
                        ] }
                        """)
        Page<MerchantCertificationAndAward> findTrashedMerchantAwards(
                        @Param("keyword") String keyword,
                        Pageable pageable);

        @Query("{ 'merchant_certification_id': ?0, 'deleted_at': null }")
        Optional<MerchantCertificationAndAward> findMerchantAwardById(
                        @Param("merchantCertificationId") String merchantCertificationId);
}
