package com.sanedge.ecommerce.product.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.product.models.Product;

@Repository
public interface ProductCommandRepository extends MongoRepository<Product, String>, ProductCommandRepositoryCustom {
}
