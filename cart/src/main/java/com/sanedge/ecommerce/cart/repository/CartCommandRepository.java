package com.sanedge.ecommerce.cart.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.cart.models.Cart;

@Repository
public interface CartCommandRepository extends MongoRepository<Cart, String> {

    void deleteByCartId(@Param("cartId") String cartId);

    @Query("{ 'cart_id': { '$in': ?0 } }")
    void deleteCartsByIds(@Param("cartIds") List<String> cartIds);
}
