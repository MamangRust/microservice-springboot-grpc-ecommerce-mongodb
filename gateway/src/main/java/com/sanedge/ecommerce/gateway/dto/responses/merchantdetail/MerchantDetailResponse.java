package com.sanedge.ecommerce.gateway.dto.responses.merchantdetail;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantDetailResponse {
    private Long id;
    private Integer merchantId;
    private String displayName;
    private String coverImageUrl;
    private String logoUrl;
    private String shortDescription;
    private String websiteUrl;
    private String createdAt;
    private String updatedAt;

}
