package com.sanedge.ecommerce.gateway.dto.responses.merchantaward;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantAwardResponse {

    private Long id;
    private Integer merchantId;
    private String title;
    private String description;
    private String issuedBy;
    private String issueDate;
    private String expiryDate;
    private String certificateUrl;
    private String createdAt;
    private String updatedAt;

}