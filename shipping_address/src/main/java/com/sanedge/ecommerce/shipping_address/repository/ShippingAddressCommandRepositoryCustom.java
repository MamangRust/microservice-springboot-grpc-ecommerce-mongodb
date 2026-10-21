package com.sanedge.ecommerce.shipping_address.repository;

import com.sanedge.ecommerce.shipping_address.models.ShippingAddress;

public interface ShippingAddressCommandRepositoryCustom {
    ShippingAddress trashed(String shippingAddressId);

    ShippingAddress restore(String shippingAddressId);

    boolean deletePermanent(String shippingAddressId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}
