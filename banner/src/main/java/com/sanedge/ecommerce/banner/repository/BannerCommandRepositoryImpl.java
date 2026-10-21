package com.sanedge.ecommerce.banner.repository;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.banner.models.Banner;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BannerCommandRepositoryImpl implements BannerCommandRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public Banner trashed(String bannerId) {
        Query query = new Query(Criteria.where("banner_id").is(bannerId).and("deleted_at").isNull());

        Banner banner = mongoTemplate.findOne(query, Banner.class);
        if (banner == null) {
            return null;
        }

        mongoTemplate.updateFirst(query, new Update().set("deleted_at", LocalDateTime.now()), Banner.class);

        return mongoTemplate.findOne(new Query(Criteria.where("banner_id").is(bannerId)), Banner.class);
    }

    @Override
    public Banner restore(String bannerId) {
        Query query = new Query(Criteria.where("banner_id").is(bannerId).and("deleted_at").ne(null));

        Banner banner = mongoTemplate.findOne(query, Banner.class);
        if (banner == null) {
            return null;
        }

        mongoTemplate.updateFirst(query, new Update().unset("deleted_at"), Banner.class);

        return mongoTemplate.findOne(new Query(Criteria.where("banner_id").is(bannerId)), Banner.class);
    }

    @Override
    public boolean deletePermanent(String bannerId) {
        Query query = new Query(Criteria.where("banner_id").is(bannerId).and("deleted_at").ne(null));

        return mongoTemplate.remove(query, Banner.class).getDeletedCount() > 0;
    }

    @Override
    public boolean restoreAllDeleted() {
        return mongoTemplate
                .updateMulti(new Query(Criteria.where("deleted_at").ne(null)),
                        new Update().unset("deleted_at"), Banner.class)
                .getModifiedCount() > 0;
    }

    @Override
    public boolean deleteAllDeleted() {
        return mongoTemplate
                .remove(new Query(Criteria.where("deleted_at").ne(null)), Banner.class)
                .getDeletedCount() > 0;
    }
}
