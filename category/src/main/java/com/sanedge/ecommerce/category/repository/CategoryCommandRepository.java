package com.sanedge.ecommerce.category.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.category.models.Category;

@Repository
public interface CategoryCommandRepository extends MongoRepository<Category, String>, CategoryCommandRepositoryCustom {

}
