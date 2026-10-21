package com.sanedge.ecommerce.merchant_policy.repository;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.merchant_policy.models.MerchantPolicy;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MerchantPolicyCommandRepositoryImpl implements MerchantPolicyCommandRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public MerchantPolicy trashed(String merchantPolicyId) {
        Query query = new Query(
                Criteria.where("merchant_policy_id").is(merchantPolicyId).and("deleted_at").isNull());

        MerchantPolicy policy = mongoTemplate.findOne(query, MerchantPolicy.class);
        if (policy == null) {
            return null;
        }

        mongoTemplate.updateFirst(query, new Update().set("deleted_at", LocalDateTime.now()), MerchantPolicy.class);

        return mongoTemplate.findOne(
                new Query(Criteria.where("merchant_policy_id").is(merchantPolicyId)), MerchantPolicy.class);
    }

    @Override
    public MerchantPolicy restore(String merchantPolicyId) {
        Query query = new Query(
                Criteria.where("merchant_policy_id").is(merchantPolicyId).and("deleted_at").ne(null));

        MerchantPolicy policy = mongoTemplate.findOne(query, MerchantPolicy.class);
        if (policy == null) {
            return null;
        }

        mongoTemplate.updateFirst(query, new Update().unset("deleted_at"), MerchantPolicy.class);

        return mongoTemplate.findOne(
                new Query(Criteria.where("merchant_policy_id").is(merchantPolicyId)), MerchantPolicy.class);
    }

    @Override
    public boolean deletePermanent(String merchantPolicyId) {
        Query query = new Query(
                Criteria.where("merchant_policy_id").is(merchantPolicyId).and("deleted_at").ne(null));

        return mongoTemplate.remove(query, MerchantPolicy.class).getDeletedCount() > 0;
    }

    @Override
    public boolean restoreAllDeleted() {
        return mongoTemplate
                .updateMulti(new Query(Criteria.where("deleted_at").ne(null)),
                        new Update().unset("deleted_at"), MerchantPolicy.class)
                .getModifiedCount() > 0;
    }

    @Override
    public boolean deleteAllDeleted() {
        return mongoTemplate
                .remove(new Query(Criteria.where("deleted_at").ne(null)), MerchantPolicy.class)
                .getDeletedCount() > 0;
    }
}
