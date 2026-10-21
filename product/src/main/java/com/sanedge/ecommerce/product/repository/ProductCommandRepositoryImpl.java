package com.sanedge.ecommerce.product.repository;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.product.models.Product;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductCommandRepositoryImpl implements ProductCommandRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public Product trashed(String productId) {
        Query query = new Query(Criteria.where("product_id").is(productId).and("deleted_at").isNull());

        Product product = mongoTemplate.findOne(query, Product.class);
        if (product == null) {
            return null;
        }

        mongoTemplate.updateFirst(query, new Update().set("deleted_at", LocalDateTime.now()), Product.class);

        return mongoTemplate.findOne(new Query(Criteria.where("product_id").is(productId)), Product.class);
    }

    @Override
    public Product restore(String productId) {
        Query query = new Query(Criteria.where("product_id").is(productId).and("deleted_at").ne(null));

        Product product = mongoTemplate.findOne(query, Product.class);
        if (product == null) {
            return null;
        }

        mongoTemplate.updateFirst(query, new Update().unset("deleted_at"), Product.class);

        return mongoTemplate.findOne(new Query(Criteria.where("product_id").is(productId)), Product.class);
    }

    @Override
    public boolean deletePermanent(String productId) {
        Query query = new Query(Criteria.where("product_id").is(productId).and("deleted_at").ne(null));

        return mongoTemplate.remove(query, Product.class).getDeletedCount() > 0;
    }

    @Override
    public boolean restoreAllDeleted() {
        return mongoTemplate
                .updateMulti(new Query(Criteria.where("deleted_at").ne(null)),
                        new Update().unset("deleted_at"), Product.class)
                .getModifiedCount() > 0;
    }

    @Override
    public boolean deleteAllDeleted() {
        return mongoTemplate
                .remove(new Query(Criteria.where("deleted_at").ne(null)), Product.class)
                .getDeletedCount() > 0;
    }

    @Override
    public Product updateCountInStock(String productId, Integer count) {
        Query query = new Query(Criteria.where("product_id").is(productId).and("deleted_at").isNull());

        Product product = mongoTemplate.findOne(query, Product.class);
        if (product == null) {
            return null;
        }

        mongoTemplate.updateFirst(query, new Update().set("count_in_stock", count), Product.class);

        return mongoTemplate.findOne(new Query(Criteria.where("product_id").is(productId)), Product.class);
    }
}
