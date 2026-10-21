package com.sanedge.ecommerce.category.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.category.models.Category;

@Repository
public interface CategoryQueryRepository extends MongoRepository<Category, String> {

    @Query("""
            { 'deleted_at': null, '$or': [
                { 'name': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                { 'description': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                { 'slug_category': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
            ] }
            """)
    Page<Category> findCategories(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
            { 'deleted_at': null, '$or': [
                { 'name': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                { 'description': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                { 'slug_category': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
            ] }
            """)
    Page<Category> findActiveCategories(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
            { 'deleted_at': { '$ne': null }, '$or': [
                { 'name': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                { 'description': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                { 'slug_category': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
            ] }
            """)
    Page<Category> findTrashedCategories(@Param("keyword") String keyword, Pageable pageable);

    @Query("{ 'category_id': ?0, 'deleted_at': null }")
    Optional<Category> findCategoryById(@Param("categoryId") String categoryId);
}
