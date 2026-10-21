package com.sanedge.ecommerce.shipping_address.repository;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.shipping_address.models.ShippingAddress;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ShippingAddressCommandRepositoryImpl implements ShippingAddressCommandRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public ShippingAddress trashed(String shippingAddressId) {
        Query query = new Query(Criteria.where("shipping_address_id").is(shippingAddressId)
                .and("deleted_at").isNull());

        ShippingAddress address = mongoTemplate.findOne(query, ShippingAddress.class);
        if (address == null) {
            return null;
        }

        mongoTemplate.updateFirst(query, new Update().set("deleted_at", LocalDateTime.now()), ShippingAddress.class);

        return mongoTemplate.findOne(new Query(Criteria.where("shipping_address_id").is(shippingAddressId)),
                ShippingAddress.class);
    }

    @Override
    public ShippingAddress restore(String shippingAddressId) {
        Query query = new Query(Criteria.where("shipping_address_id").is(shippingAddressId)
                .and("deleted_at").ne(null));

        ShippingAddress address = mongoTemplate.findOne(query, ShippingAddress.class);
        if (address == null) {
            return null;
        }

        mongoTemplate.updateFirst(query, new Update().unset("deleted_at"), ShippingAddress.class);

        return mongoTemplate.findOne(new Query(Criteria.where("shipping_address_id").is(shippingAddressId)),
                ShippingAddress.class);
    }

    @Override
    public boolean deletePermanent(String shippingAddressId) {
        Query query = new Query(Criteria.where("shipping_address_id").is(shippingAddressId)
                .and("deleted_at").ne(null));

        return mongoTemplate.remove(query, ShippingAddress.class).getDeletedCount() > 0;
    }

    @Override
    public boolean restoreAllDeleted() {
        return mongoTemplate
                .updateMulti(new Query(Criteria.where("deleted_at").ne(null)),
                        new Update().unset("deleted_at"), ShippingAddress.class)
                .getModifiedCount() > 0;
    }

    @Override
    public boolean deleteAllDeleted() {
        return mongoTemplate
                .remove(new Query(Criteria.where("deleted_at").ne(null)), ShippingAddress.class)
                .getDeletedCount() > 0;
    }
}
