package com.sanedge.ecommerce.order_item.repository;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.order_item.models.OrderItem;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderItemCommandRepositoryImpl implements OrderItemCommandRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public OrderItem trashed(String orderItemId) {
        Query query = new Query(Criteria.where("order_item_id").is(orderItemId).and("deleted_at").isNull());

        OrderItem orderItem = mongoTemplate.findOne(query, OrderItem.class);
        if (orderItem == null) {
            return null;
        }

        mongoTemplate.updateFirst(query, new Update().set("deleted_at", LocalDateTime.now()), OrderItem.class);

        return mongoTemplate.findOne(new Query(Criteria.where("order_item_id").is(orderItemId)), OrderItem.class);
    }

    @Override
    public OrderItem restore(String orderItemId) {
        Query query = new Query(Criteria.where("order_item_id").is(orderItemId).and("deleted_at").ne(null));

        OrderItem orderItem = mongoTemplate.findOne(query, OrderItem.class);
        if (orderItem == null) {
            return null;
        }

        mongoTemplate.updateFirst(query, new Update().unset("deleted_at"), OrderItem.class);

        return mongoTemplate.findOne(new Query(Criteria.where("order_item_id").is(orderItemId)), OrderItem.class);
    }

    @Override
    public OrderItem deletePermanent(String orderItemId) {
        Query query = new Query(Criteria.where("order_item_id").is(orderItemId).and("deleted_at").ne(null));

        OrderItem orderItem = mongoTemplate.findOne(query, OrderItem.class);
        if (orderItem == null) {
            return null;
        }

        mongoTemplate.remove(query, OrderItem.class);

        return orderItem;
    }

    @Override
    public boolean restoreAllDeleted() {
        return mongoTemplate
                .updateMulti(new Query(Criteria.where("deleted_at").ne(null)),
                        new Update().unset("deleted_at"), OrderItem.class)
                .getModifiedCount() > 0;
    }

    @Override
    public boolean deleteAllDeleted() {
        return mongoTemplate
                .remove(new Query(Criteria.where("deleted_at").ne(null)), OrderItem.class)
                .getDeletedCount() > 0;
    }

    @Override
    public boolean deleteByOrderPermanent(Long orderId) {
        Query query = new Query(Criteria.where("order_id").is(toOrderId(orderId)).and("deleted_at").ne(null));

        return mongoTemplate.remove(query, OrderItem.class).getDeletedCount() > 0;
    }

    @Override
    public boolean deleteByOrderRollback(Long orderId) {
        Query query = new Query(Criteria.where("order_id").is(toOrderId(orderId)));

        return mongoTemplate.remove(query, OrderItem.class).getDeletedCount() > 0;
    }

    /** FK order_id di order_items tetap Integer; nilai Long dicocokkan numerik. */
    private static Integer toOrderId(Long orderId) {
        return orderId == null ? null : orderId.intValue();
    }
}
