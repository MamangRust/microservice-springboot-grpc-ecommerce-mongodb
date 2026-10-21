package com.sanedge.ecommerce.merchant_social_link.models;

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
@Document(collection = "merchant_social_media_links")
public class MerchantSocialMediaLink extends BaseModel {
    @Id
    @Field("merchant_social_id")
    private String merchantSocialId;

    @Field("merchant_detail_id")
    private Integer merchantDetailId;

    @Field("platform")
    private String platform;

    @Field("url")
    private String url;
}
