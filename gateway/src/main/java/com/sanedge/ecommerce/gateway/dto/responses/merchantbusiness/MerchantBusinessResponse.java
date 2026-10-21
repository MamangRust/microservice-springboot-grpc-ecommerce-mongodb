package com.sanedge.ecommerce.gateway.dto.responses.merchantbusiness;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantBusinessResponse {
    private Long id;
    private Integer merchantId;
    private String businessType;
    private String taxId;
    private Integer establishedYear;
    private Integer numberOfEmployees;
    private String websiteUrl;
    private String createdAt;
    private String updatedAt;

}