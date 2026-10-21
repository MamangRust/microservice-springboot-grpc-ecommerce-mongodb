package com.sanedge.ecommerce.banner.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.banner.models.Banner;

@Repository
public interface BannerCommandRepository extends MongoRepository<Banner, String>, BannerCommandRepositoryCustom {

}
