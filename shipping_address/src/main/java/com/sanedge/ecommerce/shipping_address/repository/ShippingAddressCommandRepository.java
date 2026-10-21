package com.sanedge.ecommerce.shipping_address.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.shipping_address.models.ShippingAddress;

@Repository
public interface ShippingAddressCommandRepository
                extends MongoRepository<ShippingAddress, String>, ShippingAddressCommandRepositoryCustom {

}
