package com.sanedge.ecommerce.merchant_award.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.time.LocalDate;

import com.sanedge.ecommerce.common.models.BaseModel;
import com.sanedge.ecommerce.merchant_award.domain.requests.CreateMerchantAwardRequest;
import com.sanedge.ecommerce.merchant_award.domain.requests.UpdateMerchantAwardRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "merchant_certifications_and_awards")
public class MerchantCertificationAndAward extends BaseModel {

    @Id
    @Field("merchant_certification_id")
    private String merchantCertificationId;

    @Field("merchant_id")
    private Integer merchantId;

    @Field("title")
    private String title;

    @Field("description")
    private String description;

    @Field("issued_by")
    private String issuedBy;

    @Field("issue_date")
    private LocalDate issueDate;

    @Field("expiry_date")
    private LocalDate expiryDate;

    @Field("certificate_url")
    private String certificateUrl;

    public static MerchantCertificationAndAward fromCreateRequest(CreateMerchantAwardRequest req) {
        MerchantCertificationAndAward award = new MerchantCertificationAndAward();
        award.setMerchantId(req.getMerchantId());
        award.setTitle(req.getTitle());
        award.setDescription(req.getDescription());
        award.setIssuedBy(req.getIssuedBy());
        award.setIssueDate(parseDate(req.getIssueDate()));
        award.setExpiryDate(parseDate(req.getExpiryDate()));
        award.setCertificateUrl(req.getCertificateUrl());
        return award;
    }

    public void updateFromRequest(UpdateMerchantAwardRequest req) {
        this.title = req.getTitle();
        this.description = req.getDescription();
        this.issuedBy = req.getIssuedBy();
        this.issueDate = parseDate(req.getIssueDate());
        this.expiryDate = parseDate(req.getExpiryDate());
        this.certificateUrl = req.getCertificateUrl();
    }

    private static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return null;
        return LocalDate.parse(dateStr);
    }
}
