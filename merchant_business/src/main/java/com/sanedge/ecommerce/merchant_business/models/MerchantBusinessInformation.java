package com.sanedge.ecommerce.merchant_business.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import com.sanedge.ecommerce.common.models.BaseModel;
import com.sanedge.ecommerce.merchant_business.domain.requests.CreateMerchantBusinessRequest;
import com.sanedge.ecommerce.merchant_business.domain.requests.UpdateMerchantBusinessRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "merchant_business_information")
public class MerchantBusinessInformation extends BaseModel {

    @Id
    @Field("merchant_business_info_id")
    private String merchantBusinessInfoId;

    @Field("merchant_id")
    private Integer merchantId;

    @Field("business_type")
    private String businessType;

    @Field("tax_id")
    private String taxId;

    @Field("established_year")
    private Integer establishedYear;

    @Field("number_of_employees")
    private Integer numberOfEmployees;

    @Field("website_url")
    private String websiteUrl;

    public static MerchantBusinessInformation fromCreateRequest(CreateMerchantBusinessRequest req) {
        MerchantBusinessInformation mbi = new MerchantBusinessInformation();
        mbi.setMerchantId(req.getMerchantId());
        mbi.setBusinessType(req.getBusinessType());
        mbi.setTaxId(req.getTaxId());
        mbi.setEstablishedYear(req.getEstablishedYear());
        mbi.setNumberOfEmployees(req.getNumberOfEmployees());
        mbi.setWebsiteUrl(req.getWebsiteUrl());
        return mbi;
    }

    public static MerchantBusinessInformation fromUpdateRequest(UpdateMerchantBusinessRequest req) {
        MerchantBusinessInformation mbi = new MerchantBusinessInformation();
        mbi.setMerchantBusinessInfoId(String.valueOf(req.getMerchantBusinessInfoId()));
        mbi.setBusinessType(req.getBusinessType());
        mbi.setTaxId(req.getTaxId());
        mbi.setEstablishedYear(req.getEstablishedYear());
        mbi.setNumberOfEmployees(req.getNumberOfEmployees());
        mbi.setWebsiteUrl(req.getWebsiteUrl());
        return mbi;
    }
}
