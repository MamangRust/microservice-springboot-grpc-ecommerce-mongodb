package com.sanedge.ecommerce.common.adapter.shipping_address;

import com.sanedge.ecommerce.common.adapter.model.ShippingAddress;

/**
 * Port shipping address. Setara
 * {@code shipping_address.QueryRepository}/{@code CommandRepository} di Go.
 * Read path mempropagasikan status gRPC apa adanya.
 */
public interface ShippingAddressPort {

    ShippingAddress findById(int shippingId);

    ShippingAddress findByOrder(int orderId);

    ShippingAddress create(CreateData data);

    ShippingAddress update(UpdateData data);

    Boolean deleteByOrderIdPermanent(int orderId);

    Boolean deleteAll();

    record CreateData(
            int orderId,
            String alamat,
            String provinsi,
            String kota,
            String negara,
            String courier,
            String shippingMethod,
            int shippingCost) {
    }

    record UpdateData(
            int shippingId,
            String alamat,
            String provinsi,
            String kota,
            String negara,
            String courier,
            String shippingMethod,
            int shippingCost,
            int orderId) {
    }
}
