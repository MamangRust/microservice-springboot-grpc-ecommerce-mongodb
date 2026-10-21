package com.sanedge.ecommerce.merchant_detail.repository;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.merchant_detail.models.MerchantDetail;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MerchantDetailCommandRepositoryImpl implements MerchantDetailCommandRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public MerchantDetail trashed(String merchantDetailId) {
        Query query = new Query(
                Criteria.where("merchant_detail_id").is(merchantDetailId).and("deleted_at").isNull());

        MerchantDetail detail = mongoTemplate.findOne(query, MerchantDetail.class);
        if (detail == null) {
            return null;
        }

        mongoTemplate.updateFirst(query, new Update().set("deleted_at", LocalDateTime.now()), MerchantDetail.class);

        return mongoTemplate
                .findOne(new Query(Criteria.where("merchant_detail_id").is(merchantDetailId)), MerchantDetail.class);
    }

    @Override
    public MerchantDetail restore(String merchantDetailId) {
        Query query = new Query(
                Criteria.where("merchant_detail_id").is(merchantDetailId).and("deleted_at").ne(null));

        MerchantDetail detail = mongoTemplate.findOne(query, MerchantDetail.class);
        if (detail == null) {
            return null;
        }

        mongoTemplate.updateFirst(query, new Update().unset("deleted_at"), MerchantDetail.class);

        return mongoTemplate
                .findOne(new Query(Criteria.where("merchant_detail_id").is(merchantDetailId)), MerchantDetail.class);
    }

    @Override
    public boolean deletePermanent(String merchantDetailId) {
        Query query = new Query(
                Criteria.where("merchant_detail_id").is(merchantDetailId).and("deleted_at").ne(null));

        return mongoTemplate.remove(query, MerchantDetail.class).getDeletedCount() > 0;
    }

    @Override
    public boolean restoreAllDeleted() {
        return mongoTemplate
                .updateMulti(new Query(Criteria.where("deleted_at").ne(null)),
                        new Update().unset("deleted_at"), MerchantDetail.class)
                .getModifiedCount() > 0;
    }

    @Override
    public boolean deleteAllDeleted() {
        return mongoTemplate
                .remove(new Query(Criteria.where("deleted_at").ne(null)), MerchantDetail.class)
                .getDeletedCount() > 0;
    }
}
