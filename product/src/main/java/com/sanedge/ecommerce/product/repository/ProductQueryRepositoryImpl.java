package com.sanedge.ecommerce.product.repository;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.product.models.Product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ProductQueryRepositoryImpl implements ProductQueryRepositoryCustom {

        private static final int DEFAULT_MIN_PRICE = 0;
        private static final int DEFAULT_MAX_PRICE = 999_999_999;

        private final MongoTemplate mongoTemplate;

        @Override
        public Page<Product> findProductsByMerchantNative(
                        Integer merchantId,
                        String keyword,
                        Integer categoryId,
                        Integer minPrice,
                        Integer maxPrice,
                        Pageable pageable) {
                List<Criteria> orKeyword = keywordCriterias(keyword);

                List<Criteria> ands = new ArrayList<>();
                ands.add(Criteria.where("deleted_at").isNull());
                ands.add(Criteria.where("merchant_id").is(merchantId));
                if (!orKeyword.isEmpty()) {
                        ands.add(new Criteria().orOperator(orKeyword.toArray(new Criteria[0])));
                }
                if (categoryId != null && categoryId != 0) {
                        ands.add(Criteria.where("category_id").is(categoryId));
                }
                ands.add(priceCriteria(minPrice, maxPrice));

                Criteria criteria = new Criteria().andOperator(ands.toArray(new Criteria[0]));
                return findPage(criteria, pageable);
        }

        @Override
        public Page<Product> findProductsByCategoryNameNative(
                        String categoryName,
                        String keyword,
                        Integer minPrice,
                        Integer maxPrice,
                        Pageable pageable) {
                List<Integer> categoryIds = findCategoryIdsByName(categoryName);
                if (categoryIds.isEmpty()) {
                        log.warn("⚠️ No category found with name '{}'", categoryName);
                        return new PageImpl<>(List.of(), pageable, 0);
                }

                List<Criteria> orKeyword = keywordCriterias(keyword);

                List<Criteria> ands = new ArrayList<>();
                ands.add(Criteria.where("deleted_at").isNull());
                ands.add(Criteria.where("category_id").in(categoryIds));
                if (!orKeyword.isEmpty()) {
                        ands.add(new Criteria().orOperator(orKeyword.toArray(new Criteria[0])));
                }
                ands.add(priceCriteria(minPrice, maxPrice));

                Criteria criteria = new Criteria().andOperator(ands.toArray(new Criteria[0]));
                return findPage(criteria, pageable);
        }

        private Page<Product> findPage(Criteria criteria, Pageable pageable) {
                Query countQuery = new Query(criteria).skip(-1).limit(-1);
                long total = mongoTemplate.count(countQuery, Product.class);

                Query query = new Query(criteria)
                                .with(pageable)
                                .with(Sort.by(Sort.Direction.DESC, "created_at"));
                List<Product> products = mongoTemplate.find(query, Product.class);

                return new PageImpl<>(products, pageable, total);
        }

        private List<Criteria> keywordCriterias(String keyword) {
                List<Criteria> criterias = new ArrayList<>();
                if (keyword != null && !keyword.isEmpty()) {
                        String regex = java.util.regex.Pattern.quote(keyword);
                        criterias.add(Criteria.where("name").regex(regex, "i"));
                        criterias.add(Criteria.where("description").regex(regex, "i"));
                        criterias.add(Criteria.where("slug_product").regex(regex, "i"));
                }
                return criterias;
        }

        private Criteria priceCriteria(Integer minPrice, Integer maxPrice) {
                int min = minPrice != null ? minPrice : DEFAULT_MIN_PRICE;
                int max = maxPrice != null ? maxPrice : DEFAULT_MAX_PRICE;
                return Criteria.where("price").gte(min).lte(max);
        }

        /**
         * Join in-memory products → categories: tipe id beda (PK String vs FK Integer)
         * sehingga $lookup tidak bisa dipakai.
         */
        private List<Integer> findCategoryIdsByName(String categoryName) {
                List<Integer> ids = new ArrayList<>();
                if (categoryName == null || categoryName.isEmpty()) {
                        return ids;
                }

                Query categoryQuery = new Query(
                                Criteria.where("name").is(categoryName).and("deleted_at").isNull());
                List<Document> categories = mongoTemplate.find(categoryQuery, Document.class, "categories");

                for (Document category : categories) {
                        Object categoryId = category.get("category_id");
                        if (categoryId == null) {
                                continue;
                        }
                        try {
                                ids.add(Integer.parseInt(String.valueOf(categoryId)));
                        } catch (NumberFormatException e) {
                                log.warn("⚠️ Skipping non-numeric category_id '{}'", categoryId);
                        }
                }
                return ids;
        }
}
