package com.sanedge.ecommerce.merchant_business.repository;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.merchant_business.models.MerchantBusinessInformation;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MerchantBusinessCommandRepositoryImpl
        implements MerchantBusinessCommandRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public MerchantBusinessInformation trashed(String merchantBusinessInfoId) {
        Query query = new Query(Criteria.where("merchant_business_info_id").is(merchantBusinessInfoId)
                .and("deleted_at").isNull());

        MerchantBusinessInformation info = mongoTemplate.findOne(query, MerchantBusinessInformation.class);
        if (info == null) {
            return null;
        }

        mongoTemplate.updateFirst(query, new Update().set("deleted_at", LocalDateTime.now()),
                MerchantBusinessInformation.class);

        return mongoTemplate.findOne(
                new Query(Criteria.where("merchant_business_info_id").is(merchantBusinessInfoId)),
                MerchantBusinessInformation.class);
    }

    @Override
    public MerchantBusinessInformation restore(String merchantBusinessInfoId) {
        Query query = new Query(Criteria.where("merchant_business_info_id").is(merchantBusinessInfoId)
                .and("deleted_at").ne(null));

        MerchantBusinessInformation info = mongoTemplate.findOne(query, MerchantBusinessInformation.class);
        if (info == null) {
            return null;
        }

        mongoTemplate.updateFirst(query, new Update().unset("deleted_at"), MerchantBusinessInformation.class);

        return mongoTemplate.findOne(
                new Query(Criteria.where("merchant_business_info_id").is(merchantBusinessInfoId)),
                MerchantBusinessInformation.class);
    }

    @Override
    public boolean deletePermanent(String merchantBusinessInfoId) {
        Query query = new Query(Criteria.where("merchant_business_info_id").is(merchantBusinessInfoId)
                .and("deleted_at").ne(null));

        return mongoTemplate.remove(query, MerchantBusinessInformation.class).getDeletedCount() > 0;
    }

    @Override
    public boolean restoreAllDeleted() {
        return mongoTemplate
                .updateMulti(new Query(Criteria.where("deleted_at").ne(null)),
                        new Update().unset("deleted_at"), MerchantBusinessInformation.class)
                .getModifiedCount() > 0;
    }

    @Override
    public boolean deleteAllDeleted() {
        return mongoTemplate
                .remove(new Query(Criteria.where("deleted_at").ne(null)), MerchantBusinessInformation.class)
                .getDeletedCount() > 0;
    }
}
