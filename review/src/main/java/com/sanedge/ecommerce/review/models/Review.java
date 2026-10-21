package com.sanedge.ecommerce.review.models;

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
@Document(collection = "reviews")
public class Review extends BaseModel {

    @Id
    @Field("review_id")
    private String reviewId;

    @Field("user_id")
    private Integer userId;

    @Field("product_id")
    private Integer productId;

    @Field("name")
    private String name;

    @Field("comment")
    private String comment;

    @Field("rating")
    private Integer rating;
}