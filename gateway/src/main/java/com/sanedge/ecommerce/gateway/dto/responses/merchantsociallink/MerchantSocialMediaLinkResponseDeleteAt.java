package com.sanedge.ecommerce.gateway.dto.responses.merchantsociallink;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantSocialMediaLinkResponseDeleteAt {
    private Long id;
    private String platform;
    private String url;
    private String createdAt;
    private String updatedAt;
    private String deletedAt;

}
