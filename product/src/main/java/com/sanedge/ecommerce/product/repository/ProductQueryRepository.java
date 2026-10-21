package com.sanedge.ecommerce.product.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.product.models.Product;

@Repository
public interface ProductQueryRepository
                extends MongoRepository<Product, String>, ProductQueryRepositoryCustom {
        @Query("""
                        { 'deleted_at': null, '$or': [
                            { 'name': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                            { 'description': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                            { 'slug_product': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
                        ] }
                        """)
        Page<Product> findProducts(@Param("keyword") String keyword, Pageable pageable);

        @Query("""
                        { 'deleted_at': null, '$or': [
                            { 'name': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                            { 'description': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                            { 'slug_product': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
                        ] }
                        """)
        Page<Product> findActiveProducts(@Param("keyword") String keyword, Pageable pageable);

        @Query("""
                        { 'deleted_at': { '$ne': null }, '$or': [
                            { 'name': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                            { 'description': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                            { 'slug_product': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
                        ] }
                        """)
        Page<Product> findTrashedProducts(@Param("keyword") String keyword, Pageable pageable);

        @Query("""
                        { 'deleted_at': null, 'merchant_id': ?0, '$or': [
                            { 'name': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                            { 'description': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                            { 'slug_product': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
                        ] }
                        """)
        Page<Product> findByMerchant(
                        @Param("merchantId") Integer merchantId,
                        @Param("keyword") String keyword,
                        Pageable pageable);

        @Query("""
                        { 'deleted_at': null, 'category_id': ?0, '$or': [
                            { 'name': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                            { 'description': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                            { 'slug_product': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
                        ] }
                        """)
        Page<Product> findByCategory(
                        @Param("categoryId") Integer categoryId,
                        @Param("keyword") String keyword,
                        Pageable pageable);

        @Query("{ 'product_id': ?0, 'deleted_at': null }")
        Optional<Product> findProductById(@Param("productId") String productId);
}
