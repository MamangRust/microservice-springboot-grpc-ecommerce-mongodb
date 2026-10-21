package com.sanedge.ecommerce.review_detail.models.review;

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
@Document(collection = "review_details")
public class ReviewDetail extends BaseModel {
    @Id
    @Field("review_detail_id")
    private String reviewDetailId;

    @Field("review_id")
    private Integer reviewId;

    @Field("type")
    private String type;

    @Field("url")
    private String url;

    @Field("caption")
    private String caption;
}
