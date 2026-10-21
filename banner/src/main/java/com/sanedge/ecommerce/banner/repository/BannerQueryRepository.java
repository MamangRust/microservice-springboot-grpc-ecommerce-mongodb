package com.sanedge.ecommerce.banner.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.banner.models.Banner;

@Repository
public interface BannerQueryRepository extends MongoRepository<Banner, String> {
    @Query("""
            { 'deleted_at': null, 'name': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
            """)
    Page<Banner> findBanners(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
            { 'deleted_at': null, 'is_active': true,
              'name': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
            """)
    Page<Banner> findActiveBanners(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
            { 'deleted_at': { '$ne': null },
              'name': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
            """)
    Page<Banner> findTrashedBanners(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
            { 'deleted_at': null,
              'name': { '$regex': ?#{#name == null ? '' : '^' + #name + '$'}, '$options': 'i' } }
            """)
    Optional<Banner> findByName(@Param("name") String name);
}
