package com.sanedge.ecommerce.order.models;

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
@Document(collection = "orders")
public class Order extends BaseModel {
    @Id
    @Field("order_id")
    private String orderId;

    @Field("user_id")
    private Integer userId;

    @Field("merchant_id")
    private Integer merchantId;

    @Field("total_price")
    private Integer totalPrice;
}
