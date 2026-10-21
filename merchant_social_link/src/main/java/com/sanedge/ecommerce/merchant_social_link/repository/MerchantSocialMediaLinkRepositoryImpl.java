package com.sanedge.ecommerce.merchant_social_link.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.merchant_social_link.models.MerchantSocialMediaLink;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MerchantSocialMediaLinkRepositoryImpl implements MerchantSocialMediaLinkRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public MerchantSocialMediaLink trashed(String merchantSocialId) {
        Query query = new Query(
                Criteria.where("merchant_social_id").is(merchantSocialId).and("deleted_at").isNull());

        MerchantSocialMediaLink socialLink = mongoTemplate.findOne(query, MerchantSocialMediaLink.class);
        if (socialLink == null) {
            return null;
        }

        mongoTemplate.updateFirst(query, new Update().set("deleted_at", LocalDateTime.now()),
                MerchantSocialMediaLink.class);

        return mongoTemplate.findOne(
                new Query(Criteria.where("merchant_social_id").is(merchantSocialId)), MerchantSocialMediaLink.class);
    }

    @Override
    public MerchantSocialMediaLink restore(String merchantSocialId) {
        Query query = new Query(
                Criteria.where("merchant_social_id").is(merchantSocialId).and("deleted_at").ne(null));

        MerchantSocialMediaLink socialLink = mongoTemplate.findOne(query, MerchantSocialMediaLink.class);
        if (socialLink == null) {
            return null;
        }

        mongoTemplate.updateFirst(query, new Update().unset("deleted_at"), MerchantSocialMediaLink.class);

        return mongoTemplate.findOne(
                new Query(Criteria.where("merchant_social_id").is(merchantSocialId)), MerchantSocialMediaLink.class);
    }

    @Override
    public boolean deletePermanent(String merchantSocialId) {
        Query query = new Query(
                Criteria.where("merchant_social_id").is(merchantSocialId).and("deleted_at").ne(null));

        return mongoTemplate.remove(query, MerchantSocialMediaLink.class).getDeletedCount() > 0;
    }

    @Override
    public boolean restoreAllDeleted() {
        return mongoTemplate
                .updateMulti(new Query(Criteria.where("deleted_at").ne(null)),
                        new Update().unset("deleted_at"), MerchantSocialMediaLink.class)
                .getModifiedCount() > 0;
    }

    @Override
    public boolean deleteAllDeleted() {
        return mongoTemplate
                .remove(new Query(Criteria.where("deleted_at").ne(null)), MerchantSocialMediaLink.class)
                .getDeletedCount() > 0;
    }

    @Override
    public Optional<MerchantSocialMediaLink> findByMerchantDetailIdAndPlatform(Integer merchantDetailId,
            String platform) {
        Query query = new Query(Criteria.where("merchant_detail_id").is(merchantDetailId)
                .and("platform").is(platform)
                .and("deleted_at").isNull());

        MerchantSocialMediaLink result = mongoTemplate.findOne(query, MerchantSocialMediaLink.class);
        return Optional.ofNullable(result);
    }
}
