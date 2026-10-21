package com.sanedge.ecommerce.cart.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.sanedge.ecommerce.common.models.BaseModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "carts")
public class Cart extends BaseModel {

    @Id
    @Field("cart_id")
    private String cartId;

    @Field("user_id")
    private Integer userId;

    @Field("product_id")
    private Integer productId;

    @Field("name")
    private String name;

    @Field("price")
    private Integer price;

    @Field("image")
    private String image;

    @Field("quantity")
    private Integer quantity;

    @Field("weight")
    private Integer weight;
}
