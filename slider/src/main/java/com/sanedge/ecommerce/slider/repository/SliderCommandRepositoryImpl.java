package com.sanedge.ecommerce.slider.repository;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.slider.models.Slider;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SliderCommandRepositoryImpl implements SliderCommandRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public Slider trashed(String sliderId) {
        Query query = new Query(Criteria.where("slider_id").is(sliderId).and("deleted_at").isNull());

        Slider slider = mongoTemplate.findOne(query, Slider.class);
        if (slider == null) {
            return null;
        }

        mongoTemplate.updateFirst(query, new Update().set("deleted_at", LocalDateTime.now()), Slider.class);

        return mongoTemplate.findOne(new Query(Criteria.where("slider_id").is(sliderId)), Slider.class);
    }

    @Override
    public Slider restore(String sliderId) {
        Query query = new Query(Criteria.where("slider_id").is(sliderId).and("deleted_at").ne(null));

        Slider slider = mongoTemplate.findOne(query, Slider.class);
        if (slider == null) {
            return null;
        }

        mongoTemplate.updateFirst(query, new Update().unset("deleted_at"), Slider.class);

        return mongoTemplate.findOne(new Query(Criteria.where("slider_id").is(sliderId)), Slider.class);
    }

    @Override
    public boolean deletePermanent(String sliderId) {
        Query query = new Query(Criteria.where("slider_id").is(sliderId).and("deleted_at").ne(null));

        return mongoTemplate.remove(query, Slider.class).getDeletedCount() > 0;
    }

    @Override
    public boolean restoreAllDeleted() {
        return mongoTemplate
                .updateMulti(new Query(Criteria.where("deleted_at").ne(null)),
                        new Update().unset("deleted_at"), Slider.class)
                .getModifiedCount() > 0;
    }

    @Override
    public boolean deleteAllDeleted() {
        return mongoTemplate
                .remove(new Query(Criteria.where("deleted_at").ne(null)), Slider.class)
                .getDeletedCount() > 0;
    }
}
