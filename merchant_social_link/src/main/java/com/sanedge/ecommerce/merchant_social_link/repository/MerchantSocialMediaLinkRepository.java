package com.sanedge.ecommerce.merchant_social_link.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.merchant_social_link.models.MerchantSocialMediaLink;

@Repository
public interface MerchantSocialMediaLinkRepository
        extends MongoRepository<MerchantSocialMediaLink, String>,
        MerchantSocialMediaLinkRepositoryCustom {
}
