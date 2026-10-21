package com.sanedge.ecommerce.merchant_detail.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.merchant_detail.models.MerchantDetail;

@Repository
public interface MerchantDetailCommandRepository
        extends MongoRepository<MerchantDetail, String>, MerchantDetailCommandRepositoryCustom {

}
