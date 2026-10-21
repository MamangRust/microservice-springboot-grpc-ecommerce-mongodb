package com.sanedge.ecommerce.slider.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.slider.models.Slider;

@Repository
public interface SliderQueryRepository extends MongoRepository<Slider, String> {
        @Query("""
                        { 'deleted_at': null,
                          'name': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
                        """)
        Page<Slider> findSliders(@Param("keyword") String keyword, Pageable pageable);

        @Query("""
                        { 'deleted_at': null,
                          'name': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
                        """)
        Page<Slider> findActiveSliders(@Param("keyword") String keyword, Pageable pageable);

        @Query("""
                        { 'deleted_at': { '$ne': null },
                          'name': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
                        """)
        Page<Slider> findTrashedSliders(@Param("keyword") String keyword, Pageable pageable);
}
