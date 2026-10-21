package com.sanedge.ecommerce.merchant_business.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.merchant_business.models.MerchantBusinessInformation;

@Repository
public interface MerchantBusinessQueryRepository
        extends MongoRepository<MerchantBusinessInformation, String> {
    @Query("""
            { 'deleted_at': null, '$or': [
                { 'business_type': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                { 'tax_id': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                { 'website_url': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
            ] }
            """)
    Page<MerchantBusinessInformation> findMerchantBusinessInformation(
            @Param("keyword") String keyword,
            Pageable pageable);

    @Query("""
            { 'deleted_at': null, '$or': [
                { 'business_type': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                { 'tax_id': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                { 'website_url': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
            ] }
            """)
    Page<MerchantBusinessInformation> findActiveMerchantBusinessInformation(
            @Param("keyword") String keyword,
            Pageable pageable);

    @Query("""
            { 'deleted_at': { '$ne': null }, '$or': [
                { 'business_type': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                { 'tax_id': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                { 'website_url': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
            ] }
            """)
    Page<MerchantBusinessInformation> findTrashedMerchantBusinessInformation(
            @Param("keyword") String keyword,
            Pageable pageable);

    @Query("{ 'merchant_business_info_id': ?0, 'deleted_at': null }")
    Optional<MerchantBusinessInformation> findMerchantBusinessInformationById(
            @Param("merchantBusinessInfoId") String merchantBusinessInfoId);
}
