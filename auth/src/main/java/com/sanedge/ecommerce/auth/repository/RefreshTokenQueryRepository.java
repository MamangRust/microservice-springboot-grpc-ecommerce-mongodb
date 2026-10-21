package com.sanedge.ecommerce.auth.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.auth.models.RefreshToken;

@Repository
public interface RefreshTokenQueryRepository extends MongoRepository<RefreshToken, String> {

        @Query(value = "{ 'user_id': ?0, 'deleted_at': null }", sort = "{ 'created_at': -1 }")
        Optional<RefreshToken> findByUserId(@Param("userId") Long userId);

        @Query(value = "{ 'token': ?0, 'deleted_at': null }")
        Optional<RefreshToken> findByToken(@Param("token") String token);
}
