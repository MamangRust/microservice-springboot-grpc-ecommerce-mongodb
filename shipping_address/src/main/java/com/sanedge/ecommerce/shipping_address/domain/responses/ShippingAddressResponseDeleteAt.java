package com.sanedge.ecommerce.shipping_address.domain.responses;

import com.sanedge.ecommerce.common.utils.Ids;
import com.sanedge.ecommerce.shipping_address.models.ShippingAddress;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingAddressResponseDeleteAt {
    private Long id;
    private Integer orderId;
    private String alamat;
    private String provinsi;
    private String negara;
    private String kota;
    private String shippingMethod;
    private Integer shippingCost;
    private String createdAt;
    private String updatedAt;
    private String deletedAt;

    public static ShippingAddressResponseDeleteAt from(ShippingAddress entity) {
        return ShippingAddressResponseDeleteAt.builder()
                .id(Ids.toLong(entity.getShippingAddressId()))
                .orderId(entity.getOrderId())
                .alamat(entity.getAlamat())
                .provinsi(entity.getProvinsi())
                .negara(entity.getNegara())
                .kota(entity.getKota())
                .shippingMethod(entity.getShippingMethod())
                .shippingCost(entity.getShippingCost())
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null)
                .updatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null)
                .deletedAt(entity.getDeletedAt() != null ? entity.getDeletedAt().toString() : null)
                .build();
    }
}
