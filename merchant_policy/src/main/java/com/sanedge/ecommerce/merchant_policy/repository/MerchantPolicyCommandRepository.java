package com.sanedge.ecommerce.merchant_policy.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.merchant_policy.models.MerchantPolicy;

@Repository
public interface MerchantPolicyCommandRepository
                extends MongoRepository<MerchantPolicy, String>, MerchantPolicyCommandRepositoryCustom {

}
