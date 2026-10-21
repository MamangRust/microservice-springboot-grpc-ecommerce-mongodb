package com.sanedge.ecommerce.review.repository;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.review.models.Review;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReviewCommandRepositoryImpl implements ReviewCommandRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public Review trashed(String reviewId) {
        Query query = new Query(Criteria.where("review_id").is(reviewId).and("deleted_at").isNull());

        Review review = mongoTemplate.findOne(query, Review.class);
        if (review == null) {
            return null;
        }

        mongoTemplate.updateFirst(query, new Update().set("deleted_at", LocalDateTime.now()), Review.class);

        return mongoTemplate.findOne(new Query(Criteria.where("review_id").is(reviewId)), Review.class);
    }

    @Override
    public Review restore(String reviewId) {
        Query query = new Query(Criteria.where("review_id").is(reviewId).and("deleted_at").ne(null));

        Review review = mongoTemplate.findOne(query, Review.class);
        if (review == null) {
            return null;
        }

        mongoTemplate.updateFirst(query, new Update().unset("deleted_at"), Review.class);

        return mongoTemplate.findOne(new Query(Criteria.where("review_id").is(reviewId)), Review.class);
    }

    @Override
    public boolean deletePermanent(String reviewId) {
        Query query = new Query(Criteria.where("review_id").is(reviewId).and("deleted_at").ne(null));

        return mongoTemplate.remove(query, Review.class).getDeletedCount() > 0;
    }

    @Override
    public boolean restoreAllDeleted() {
        return mongoTemplate
                .updateMulti(new Query(Criteria.where("deleted_at").ne(null)),
                        new Update().unset("deleted_at"), Review.class)
                .getModifiedCount() > 0;
    }

    @Override
    public boolean deleteAllDeleted() {
        return mongoTemplate
                .remove(new Query(Criteria.where("deleted_at").ne(null)), Review.class)
                .getDeletedCount() > 0;
    }
}
