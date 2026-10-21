package com.sanedge.ecommerce.merchant_business.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.merchant_business.models.MerchantBusinessInformation;

@Repository
public interface MerchantBusinessCommandRepository
                extends MongoRepository<MerchantBusinessInformation, String>,
                MerchantBusinessCommandRepositoryCustom {
}
