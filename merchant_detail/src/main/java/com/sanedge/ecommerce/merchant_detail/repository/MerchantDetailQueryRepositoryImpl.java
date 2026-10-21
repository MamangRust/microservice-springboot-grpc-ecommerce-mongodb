package com.sanedge.ecommerce.merchant_detail.repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.merchant_detail.models.MerchantDetail;
import com.sanedge.ecommerce.merchant_detail.models.MerchantDetailsRelation;
import com.sanedge.ecommerce.merchant_detail.models.MerchantSocialMediaLink;

import lombok.RequiredArgsConstructor;

/**
 * Menggabungkan merchant_details → merchants → merchant_social_media_links
 * secara in-memory: tipe id antar koleksi berbeda (PK String vs FK Integer)
 * sehingga $lookup tidak bisa dipakai langsung.
 */
@Repository
@RequiredArgsConstructor
public class MerchantDetailQueryRepositoryImpl implements MerchantDetailQueryRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public Page<MerchantDetailsRelation> findAllWithSocialLinks(String keyword, Pageable pageable) {
        return queryWithSocialLinks(keyword, null, null, pageable);
    }

    @Override
    public Page<MerchantDetailsRelation> findActiveWithSocialLinks(String keyword, Pageable pageable) {
        return queryWithSocialLinks(keyword, Boolean.TRUE, null, pageable);
    }

    @Override
    public Page<MerchantDetailsRelation> findTrashedWithSocialLinks(String keyword, Pageable pageable) {
        return queryWithSocialLinks(keyword, Boolean.FALSE, null, pageable);
    }

    @Override
    public Optional<MerchantDetailsRelation> findByIdWithSocialLinks(String merchantDetailId) {
        List<MerchantDetailsRelation> results = queryWithSocialLinks(null, null, merchantDetailId,
                Pageable.unpaged()).getContent();
        if (results.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(results.get(0));
    }

    private Page<MerchantDetailsRelation> queryWithSocialLinks(String keyword, Boolean isActive, String id,
            Pageable pageable) {

        Set<Integer> merchantIds = null;
        if (isActive != null || (keyword != null && !keyword.isEmpty())) {
            List<Criteria> merchantFilters = new ArrayList<>();
            if (Boolean.TRUE.equals(isActive)) {
                merchantFilters.add(Criteria.where("deleted_at").isNull());
            } else if (isActive != null) {
                merchantFilters.add(Criteria.where("deleted_at").ne(null));
            }
            if (keyword != null && !keyword.isEmpty()) {
                merchantFilters.add(Criteria.where("name").regex(keyword, "i"));
            }

            List<Document> merchants = mongoTemplate
                    .find(new Query(new Criteria().andOperator(merchantFilters.toArray(new Criteria[0]))),
                            Document.class, "merchants");
            merchantIds = new HashSet<>();
            for (Document merchant : merchants) {
                Integer merchantId = asInt(merchant.get("merchant_id"));
                if (merchantId != null) {
                    merchantIds.add(merchantId);
                }
            }
            if (merchantIds.isEmpty()) {
                return new PageImpl<>(List.of(), pageable, 0);
            }
        }

        Criteria criteria = buildDetailCriteria(merchantIds, id);
        long total = mongoTemplate.count(new Query(criteria), MerchantDetail.class);

        Query dataQuery = new Query(buildDetailCriteria(merchantIds, id))
                .with(Sort.by(Sort.Direction.DESC, "createdAt"));
        if (pageable.isPaged()) {
            dataQuery.with(pageable);
        }
        List<MerchantDetail> details = mongoTemplate.find(dataQuery, MerchantDetail.class);
        if (details.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, total);
        }

        Map<Integer, List<MerchantSocialMediaLink>> linksByDetailId = findSocialLinks(details);

        List<MerchantDetailsRelation> relations = new ArrayList<>();
        for (MerchantDetail detail : details) {
            relations.add(toRelation(detail, linksByDetailId));
        }

        return new PageImpl<>(relations, pageable, total);
    }

    private Criteria buildDetailCriteria(Set<Integer> merchantIds, String id) {
        List<Criteria> filters = new ArrayList<>();
        if (merchantIds != null) {
            filters.add(Criteria.where("merchant_id").in(merchantIds));
        }
        if (id != null) {
            filters.add(Criteria.where("merchant_detail_id").is(id));
        }
        return filters.isEmpty() ? new Criteria() : new Criteria().andOperator(filters.toArray(new Criteria[0]));
    }

    private Map<Integer, List<MerchantSocialMediaLink>> findSocialLinks(List<MerchantDetail> details) {
        Set<Integer> detailIds = new HashSet<>();
        for (MerchantDetail detail : details) {
            Integer detailId = asInt(detail.getMerchantDetailId());
            if (detailId != null) {
                detailIds.add(detailId);
            }
        }

        Map<Integer, List<MerchantSocialMediaLink>> linksByDetailId = new HashMap<>();
        if (detailIds.isEmpty()) {
            return linksByDetailId;
        }

        List<Document> links = mongoTemplate.find(
                new Query(Criteria.where("merchant_detail_id").in(detailIds)),
                Document.class, "merchant_social_media_links");
        for (Document link : links) {
            Integer detailId = asInt(link.get("merchant_detail_id"));
            if (detailId == null) {
                continue;
            }
            linksByDetailId.computeIfAbsent(detailId, key -> new ArrayList<>()).add(toSocialLink(link));
        }
        return linksByDetailId;
    }

    private MerchantDetailsRelation toRelation(MerchantDetail detail,
            Map<Integer, List<MerchantSocialMediaLink>> linksByDetailId) {
        Integer detailId = asInt(detail.getMerchantDetailId());

        MerchantDetailsRelation dto = new MerchantDetailsRelation();
        dto.setId(detailId != null ? detailId : 0);
        dto.setMerchantId(detail.getMerchantId());
        dto.setDisplayName(detail.getDisplayName());
        dto.setCoverImageUrl(detail.getCoverImageUrl());
        dto.setLogoUrl(detail.getLogoUrl());
        dto.setShortDescription(detail.getShortDescription());
        dto.setWebsiteUrl(detail.getWebsiteUrl());
        dto.setCreatedAt(detail.getCreatedAt() != null ? detail.getCreatedAt().toString() : null);
        dto.setUpdatedAt(detail.getUpdatedAt() != null ? detail.getUpdatedAt().toString() : null);
        dto.setDeletedAt(detail.getDeletedAt() != null ? detail.getDeletedAt().toString() : null);

        List<MerchantSocialMediaLink> links = detailId != null
                ? linksByDetailId.getOrDefault(detailId, List.of())
                : List.of();
        dto.setSocialMediaLinks(links);
        return dto;
    }

    private MerchantSocialMediaLink toSocialLink(Document doc) {
        MerchantSocialMediaLink link = new MerchantSocialMediaLink();
        link.setMerchantSocialId(asLong(doc.get("merchant_social_id")));
        link.setMerchantDetailId(asInt(doc.get("merchant_detail_id")));
        link.setPlatform(doc.getString("platform"));
        link.setUrl(doc.getString("url"));
        link.setCreatedAt(toTimestamp(doc.getDate("created_at")));
        link.setUpdatedAt(toTimestamp(doc.getDate("updated_at")));
        return link;
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

    private static Long asLong(Object value) {
        if (value instanceof Long l) {
            return l;
        }
        if (value instanceof Number n) {
            return n.longValue();
        }
        if (value instanceof String s) {
            try {
                return Long.parseLong(s);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private static Timestamp toTimestamp(Date date) {
        return date == null ? null : new Timestamp(date.getTime());
    }
}
