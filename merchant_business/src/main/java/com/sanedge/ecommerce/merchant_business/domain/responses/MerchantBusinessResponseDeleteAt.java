package com.sanedge.ecommerce.merchant_business.domain.responses;

import com.sanedge.ecommerce.common.utils.Ids;
import com.sanedge.ecommerce.merchant_business.models.MerchantBusinessInformation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantBusinessResponseDeleteAt {
    private Long id;
    private Integer merchantId;
    private String businessType;
    private String taxId;
    private Integer establishedYear;
    private Integer numberOfEmployees;
    private String websiteUrl;
    private String createdAt;
    private String updatedAt;
    private String deletedAt;

    public static MerchantBusinessResponseDeleteAt from(MerchantBusinessInformation entity) {
        if (entity == null) {
            return null;
        }
        return MerchantBusinessResponseDeleteAt.builder()
                .id(Ids.toLong(entity.getMerchantBusinessInfoId()))
                .merchantId(entity.getMerchantId())
                .businessType(entity.getBusinessType())
                .taxId(entity.getTaxId())
                .establishedYear(entity.getEstablishedYear())
                .numberOfEmployees(entity.getNumberOfEmployees())
                .websiteUrl(entity.getWebsiteUrl())
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null)
                .updatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null)
                .deletedAt(entity.getDeletedAt() != null ? entity.getDeletedAt().toString() : null)
                .build();
    }
}
