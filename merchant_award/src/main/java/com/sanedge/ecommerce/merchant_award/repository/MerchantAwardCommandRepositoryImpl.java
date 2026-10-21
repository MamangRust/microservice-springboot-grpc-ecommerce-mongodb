package com.sanedge.ecommerce.merchant_award.repository;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.merchant_award.models.MerchantCertificationAndAward;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MerchantAwardCommandRepositoryImpl
        implements MerchantAwardCommandRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public MerchantCertificationAndAward trashed(String merchantCertificationId) {
        Query query = new Query(Criteria.where("merchant_certification_id").is(merchantCertificationId)
                .and("deleted_at").isNull());

        MerchantCertificationAndAward award = mongoTemplate.findOne(query, MerchantCertificationAndAward.class);
        if (award == null) {
            return null;
        }

        mongoTemplate.updateFirst(query, new Update().set("deleted_at", LocalDateTime.now()),
                MerchantCertificationAndAward.class);

        return mongoTemplate.findOne(
                new Query(Criteria.where("merchant_certification_id").is(merchantCertificationId)),
                MerchantCertificationAndAward.class);
    }

    @Override
    public MerchantCertificationAndAward restore(String merchantCertificationId) {
        Query query = new Query(Criteria.where("merchant_certification_id").is(merchantCertificationId)
                .and("deleted_at").ne(null));

        MerchantCertificationAndAward award = mongoTemplate.findOne(query, MerchantCertificationAndAward.class);
        if (award == null) {
            return null;
        }

        mongoTemplate.updateFirst(query, new Update().unset("deleted_at"), MerchantCertificationAndAward.class);

        return mongoTemplate.findOne(
                new Query(Criteria.where("merchant_certification_id").is(merchantCertificationId)),
                MerchantCertificationAndAward.class);
    }

    @Override
    public boolean deletePermanent(String merchantCertificationId) {
        Query query = new Query(Criteria.where("merchant_certification_id").is(merchantCertificationId)
                .and("deleted_at").ne(null));

        return mongoTemplate.remove(query, MerchantCertificationAndAward.class).getDeletedCount() > 0;
    }

    @Override
    public boolean restoreAllDeleted() {
        return mongoTemplate
                .updateMulti(new Query(Criteria.where("deleted_at").ne(null)),
                        new Update().unset("deleted_at"), MerchantCertificationAndAward.class)
                .getModifiedCount() > 0;
    }

    @Override
    public boolean deleteAllDeleted() {
        return mongoTemplate
                .remove(new Query(Criteria.where("deleted_at").ne(null)), MerchantCertificationAndAward.class)
                .getDeletedCount() > 0;
    }
}
