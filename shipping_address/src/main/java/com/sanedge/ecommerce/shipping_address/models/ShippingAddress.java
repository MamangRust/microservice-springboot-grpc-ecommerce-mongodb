package com.sanedge.ecommerce.shipping_address.models;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import com.sanedge.ecommerce.common.models.BaseModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "shipping_addresses")
public class ShippingAddress extends BaseModel {
    @Id
    @Field("shipping_address_id")
    private String shippingAddressId;

    @Field("order_id")
    private Integer orderId;

    @Field("alamat")
    private String alamat;

    @Field("provinsi")
    private String provinsi;

    @Field("negara")
    private String negara;

    @Field("kota")
    private String kota;

    @Field("courier")
    private String courier;

    @Field("shipping_method")
    private String shippingMethod;

    @Field("shipping_cost")
    private Integer shippingCost;
}