package com.sanedge.ecommerce.slider.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.slider.models.Slider;

@Repository
public interface SliderCommandRepository extends MongoRepository<Slider, String>, SliderCommandRepositoryCustom {
}
