package com.sanedge.ecommerce.review_detail.repository.review_detail;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.review_detail.models.review.ReviewDetail;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReviewDetailRepositoryImpl implements ReviewDetailRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public ReviewDetail trashed(String reviewDetailId) {
        Query query = new Query(Criteria.where("review_detail_id").is(reviewDetailId)
                .and("deleted_at").isNull());

        ReviewDetail detail = mongoTemplate.findOne(query, ReviewDetail.class);
        if (detail == null) {
            return null;
        }

        mongoTemplate.updateFirst(query, new Update().set("deleted_at", LocalDateTime.now()), ReviewDetail.class);

        return mongoTemplate.findOne(new Query(Criteria.where("review_detail_id").is(reviewDetailId)),
                ReviewDetail.class);
    }

    @Override
    public ReviewDetail restore(String reviewDetailId) {
        Query query = new Query(Criteria.where("review_detail_id").is(reviewDetailId)
                .and("deleted_at").ne(null));

        ReviewDetail detail = mongoTemplate.findOne(query, ReviewDetail.class);
        if (detail == null) {
            return null;
        }

        mongoTemplate.updateFirst(query, new Update().unset("deleted_at"), ReviewDetail.class);

        return mongoTemplate.findOne(new Query(Criteria.where("review_detail_id").is(reviewDetailId)),
                ReviewDetail.class);
    }

    @Override
    public boolean deletePermanent(String reviewDetailId) {
        Query query = new Query(Criteria.where("review_detail_id").is(reviewDetailId)
                .and("deleted_at").ne(null));

        return mongoTemplate.remove(query, ReviewDetail.class).getDeletedCount() > 0;
    }

    @Override
    public boolean restoreAllDeleted() {
        return mongoTemplate
                .updateMulti(new Query(Criteria.where("deleted_at").ne(null)),
                        new Update().unset("deleted_at"), ReviewDetail.class)
                .getModifiedCount() > 0;
    }

    @Override
    public boolean deleteAllDeleted() {
        return mongoTemplate
                .remove(new Query(Criteria.where("deleted_at").ne(null)), ReviewDetail.class)
                .getDeletedCount() > 0;
    }
}
