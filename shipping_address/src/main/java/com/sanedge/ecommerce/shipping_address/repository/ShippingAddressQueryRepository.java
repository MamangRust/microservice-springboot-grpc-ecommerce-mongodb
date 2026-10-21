package com.sanedge.ecommerce.shipping_address.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.shipping_address.models.ShippingAddress;

@Repository
public interface ShippingAddressQueryRepository extends MongoRepository<ShippingAddress, String> {
    @Query("""
            { 'deleted_at': null, '$or': [
                { 'kota': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                { 'provinsi': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                { 'negara': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
            ] }
            """)
    Page<ShippingAddress> findShippingAddresses(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
            { 'deleted_at': null, '$or': [
                { 'kota': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                { 'provinsi': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                { 'negara': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
            ] }
            """)
    Page<ShippingAddress> findActiveShippingAddresses(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
            { 'deleted_at': { '$ne': null }, '$or': [
                { 'kota': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                { 'provinsi': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                { 'negara': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
            ] }
            """)
    Page<ShippingAddress> findTrashedShippingAddresses(@Param("keyword") String keyword, Pageable pageable);

    @Query("{ 'shipping_address_id': ?0, 'deleted_at': null }")
    Optional<ShippingAddress> findByIdNative(@Param("id") String id);

    @Query("{ 'order_id': ?0, 'deleted_at': null }")
    Optional<ShippingAddress> findByOrderId(@Param("orderId") Integer orderId);
}
