package com.sanedge.ecommerce.user.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.user.models.User;

@Repository
public interface UserCommandRepository extends MongoRepository<User, String>, UserCommandRepositoryCustom {

}
