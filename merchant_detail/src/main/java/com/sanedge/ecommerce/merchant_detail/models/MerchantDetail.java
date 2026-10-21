package com.sanedge.ecommerce.merchant_detail.models;

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
@Document(collection = "merchant_details")
public class MerchantDetail extends BaseModel {

    @Id
    @Field("merchant_detail_id")
    private String merchantDetailId;

    @Field("merchant_id")
    private Integer merchantId;

    @Field("display_name")
    private String displayName;

    @Field("cover_image_url")
    private String coverImageUrl;

    @Field("logo_url")
    private String logoUrl;

    @Field("short_description")
    private String shortDescription;

    @Field("website_url")
    private String websiteUrl;
}
