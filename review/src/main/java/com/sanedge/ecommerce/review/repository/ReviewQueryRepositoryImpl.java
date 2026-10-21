package com.sanedge.ecommerce.review.repository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.review.models.ReviewDetail;
import com.sanedge.ecommerce.review.models.ReviewRelationsDetail;

import lombok.RequiredArgsConstructor;

/**
 * Query review dengan relasi antar koleksi (reviews → products, reviews →
 * review_details) digabung secara in-memory karena tipe id antar koleksi
 * berbeda (PK String vs FK Integer) sehingga $lookup tidak bisa dipakai
 * langsung.
 */
@Repository
@RequiredArgsConstructor
public class ReviewQueryRepositoryImpl implements ReviewQueryRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public Page<ReviewRelationsDetail> findByMerchantId(Integer merchantId, Integer rating, String search,
            Pageable pageable) {
        Set<Integer> productIds = findProductIdsByMerchant(merchantId);
        if (productIds.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        return findReviewPages(productIds, rating, search, pageable);
    }

    @Override
    public Page<ReviewRelationsDetail> findByProductId(Integer productId, Integer rating, String search,
            Pageable pageable) {
        Set<Integer> productIds = new HashSet<>();
        if (productId != null) {
            productIds.add(productId);
        }
        if (productIds.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        return findReviewPages(productIds, rating, search, pageable);
    }

    private Set<Integer> findProductIdsByMerchant(Integer merchantId) {
        if (merchantId == null) {
            return Set.of();
        }

        List<Document> products = mongoTemplate.find(
                new Query(Criteria.where("merchant_id").is(merchantId).and("deleted_at").isNull()),
                Document.class, "products");

        Set<Integer> productIds = new HashSet<>();
        for (Document product : products) {
            Integer productId = asInt(product.get("product_id"));
            if (productId != null) {
                productIds.add(productId);
            }
        }
        return productIds;
    }

    private Page<ReviewRelationsDetail> findReviewPages(Set<Integer> productIds, Integer rating, String search,
            Pageable pageable) {
        Criteria criteria = Criteria.where("deleted_at").isNull().and("product_id").in(productIds);
        if (rating != null) {
            criteria = criteria.and("rating").is(rating);
        }
        if (search != null && !search.isBlank()) {
            String regex = ".*" + Pattern.quote(search) + ".*";
            criteria = criteria.orOperator(
                    Criteria.where("name").regex(regex, "i"),
                    Criteria.where("comment").regex(regex, "i"));
        }

        long total = mongoTemplate.count(new Query(criteria), "reviews");
        if (total == 0) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        Query dataQuery = new Query(criteria)
                .with(pageable)
                .with(Sort.by(Sort.Direction.DESC, "created_at"));
        List<Document> reviews = mongoTemplate.find(dataQuery, Document.class, "reviews");

        return new PageImpl<>(mapReviews(reviews), pageable, total);
    }

    private List<ReviewRelationsDetail> mapReviews(List<Document> reviews) {
        Set<Integer> reviewIds = new HashSet<>();
        for (Document review : reviews) {
            Integer reviewId = asInt(review.get("review_id"));
            if (reviewId != null) {
                reviewIds.add(reviewId);
            }
        }

        Map<Integer, List<ReviewDetail>> detailsByReviewId = findDetailsByReviewIds(reviewIds);

        List<ReviewRelationsDetail> list = new ArrayList<>();
        for (Document review : reviews) {
            ReviewRelationsDetail dto = new ReviewRelationsDetail();
            Integer reviewId = asInt(review.get("review_id"));
            dto.setId(reviewId != null ? reviewId : 0);
            Integer userId = asInt(review.get("user_id"));
            dto.setUserId(userId != null ? userId : 0);
            Integer productId = asInt(review.get("product_id"));
            dto.setProductId(productId != null ? productId : 0);
            dto.setName(review.getString("name"));
            dto.setComment(review.getString("comment"));
            Integer rating = asInt(review.get("rating"));
            dto.setRating(rating != null ? rating : 0);
            dto.setCreatedAt(toLocalDateTime(review.getDate("created_at")) != null
                    ? toLocalDateTime(review.getDate("created_at")).toString()
                    : null);
            dto.setUpdatedAt(toLocalDateTime(review.getDate("updated_at")) != null
                    ? toLocalDateTime(review.getDate("updated_at")).toString()
                    : null);
            dto.setDeletedAt(toLocalDateTime(review.getDate("deleted_at")) != null
                    ? toLocalDateTime(review.getDate("deleted_at")).toString()
                    : null);

            List<ReviewDetail> details = reviewId != null ? detailsByReviewId.get(reviewId) : null;
            dto.setReviewDetail(details != null ? details : List.of());

            list.add(dto);
        }

        return list;
    }

    private Map<Integer, List<ReviewDetail>> findDetailsByReviewIds(Set<Integer> reviewIds) {
        Map<Integer, List<ReviewDetail>> result = new HashMap<>();
        if (reviewIds.isEmpty()) {
            return result;
        }

        List<Document> details = mongoTemplate.find(
                new Query(Criteria.where("review_id").in(reviewIds)),
                Document.class, "review_details");

        for (Document detail : details) {
            Integer reviewId = asInt(detail.get("review_id"));
            if (reviewId == null) {
                continue;
            }

            ReviewDetail dto = new ReviewDetail(
                    detail.getString("review_detail_id"),
                    reviewId,
                    detail.getString("type"),
                    detail.getString("url"),
                    detail.getString("caption"));

            result.computeIfAbsent(reviewId, key -> new ArrayList<>()).add(dto);
        }

        return result;
    }

    private static Integer asInt(Object value) {
        if (value instanceof Integer i) {
            return i;
        }
        if (value instanceof Number n) {
            return n.intValue();
        }
        if (value instanceof String s) {
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private static LocalDateTime toLocalDateTime(java.util.Date date) {
        return date == null ? null : LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }
}
