package com.sanedge.ecommerce.gateway.dto.responses.shipping;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingAddressResponse {

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

}