package com.sanedge.ecommerce.auth.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.auth.models.RefreshToken;

@Repository
public interface RefreshTokenCommandRepository extends MongoRepository<RefreshToken, String> {

        long deleteByToken(String token);

        long deleteByUserId(Long userId);
}
