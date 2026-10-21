package com.sanedge.ecommerce.cart.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.cart.models.Cart;

@Repository
public interface CartQueryRepository extends MongoRepository<Cart, String> {

    @Query("""
            { 'deleted_at': null, 'user_id': ?0, '$or': [
                { 'name': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                { '$expr': { '$regexMatch': { 'input': { '$toString': { '$ifNull': ['$price', ''] } }, 'regex': ?#{#keyword == null ? '' : #keyword}, 'options': 'i' } } }
            ] }
            """)
    Page<Cart> findCartsByUser(
            @Param("userId") Integer userId,
            @Param("keyword") String keyword,
            Pageable pageable);
}
