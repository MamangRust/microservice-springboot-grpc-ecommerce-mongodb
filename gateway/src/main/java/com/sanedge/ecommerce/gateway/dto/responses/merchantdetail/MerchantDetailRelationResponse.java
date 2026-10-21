package com.sanedge.ecommerce.gateway.dto.responses.merchantdetail;

import java.util.List;

import com.sanedge.ecommerce.gateway.dto.responses.merchantsociallink.MerchantSocialMediaLinkResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantDetailRelationResponse {
    private Long id;
    private Integer merchantId;
    private String displayName;
    private String coverImageUrl;
    private String logoUrl;
    private String shortDescription;
    private String websiteUrl;
    private List<MerchantSocialMediaLinkResponse> socialMediaLinks;
    private String createdAt;
    private String updatedAt;

}