package com.sanedge.ecommerce.merchant_award.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.merchant_award.models.MerchantCertificationAndAward;

@Repository
public interface MerchantAwardCommandRepository
        extends MongoRepository<MerchantCertificationAndAward, String>,
        MerchantAwardCommandRepositoryCustom {
}
