package com.sanedge.ecommerce.merchant_policy.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.merchant_policy.models.MerchantPolicy;

@Repository
public interface MerchantPolicyQueryRepository extends MongoRepository<MerchantPolicy, String> {
    @Query("""
            { 'deleted_at': null, '$or': [
                { 'policy_type': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                { 'title': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                { 'description': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
            ] }
            """)
    Page<MerchantPolicy> findMerchantPolicies(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
            { 'deleted_at': null, '$or': [
                { 'policy_type': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                { 'title': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                { 'description': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
            ] }
            """)
    Page<MerchantPolicy> findActiveMerchantPolicies(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
            { 'deleted_at': { '$ne': null }, '$or': [
                { 'policy_type': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                { 'title': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                { 'description': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
            ] }
            """)
    Page<MerchantPolicy> findTrashedMerchantPolicies(@Param("keyword") String keyword, Pageable pageable);

    @Query("{ 'merchant_policy_id': ?0, 'deleted_at': null }")
    Optional<MerchantPolicy> findMerchantPolicyById(@Param("merchantPolicyId") String merchantPolicyId);
}
