package com.sanedge.ecommerce.merchant_policy.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import com.sanedge.ecommerce.common.models.BaseModel;
import com.sanedge.ecommerce.merchant_policy.domain.requests.CreateMerchantPolicyRequest;
import com.sanedge.ecommerce.merchant_policy.domain.requests.UpdateMerchantPolicyRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "merchant_policies")
public class MerchantPolicy extends BaseModel {

    @Id
    @Field("merchant_policy_id")
    private String merchantPolicyId;

    @Field("merchant_id")
    private Integer merchantId;

    @Field("policy_type")
    private String policyType;

    @Field("title")
    private String title;

    @Field("description")
    private String description;

    public static MerchantPolicy fromCreateRequest(CreateMerchantPolicyRequest req) {
        MerchantPolicy entity = new MerchantPolicy();
        entity.setMerchantId(req.getMerchantId());
        entity.setPolicyType(req.getPolicyType());
        entity.setTitle(req.getTitle());
        entity.setDescription(req.getDescription());
        return entity;
    }

    public void updateFromRequest(UpdateMerchantPolicyRequest req) {
        this.setPolicyType(req.getPolicyType());
        this.setTitle(req.getTitle());
        this.setDescription(req.getDescription());
    }
}
