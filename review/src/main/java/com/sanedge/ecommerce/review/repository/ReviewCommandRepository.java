package com.sanedge.ecommerce.review.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.review.models.Review;

@Repository
public interface ReviewCommandRepository extends MongoRepository<Review, String>, ReviewCommandRepositoryCustom {
}
