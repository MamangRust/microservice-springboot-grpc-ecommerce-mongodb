package com.sanedge.ecommerce.common.adapter.merchant;

import com.sanedge.ecommerce.common.adapter.model.Merchant;
import com.sanedge.ecommerce.common.adapter.support.ProtoTime;
import com.sanedge.ecommerce.common.exception.ResourceNotFoundException;
import com.sanedge.ecommerce.pb.merchant.ApiResponseMerchant;
import com.sanedge.ecommerce.pb.merchant.FindByIdMerchantRequest;
import com.sanedge.ecommerce.pb.merchant.MerchantQueryServiceGrpc.MerchantQueryServiceBlockingStub;
import com.sanedge.ecommerce.pb.merchant.MerchantResponse;

public class MerchantAdapter implements MerchantPort {

    private final MerchantQueryServiceBlockingStub client;

    public MerchantAdapter(MerchantQueryServiceBlockingStub client) {
        this.client = client;
    }

    @Override
    public Merchant findById(int merchantId) {
        var resp = client.findById(FindByIdMerchantRequest.newBuilder().setId(merchantId).build());
        if (!resp.hasData()) {
            throw new ResourceNotFoundException("Merchant not found: " + merchantId);
        }
        return toModel(resp.getData());
    }

    static Merchant toModel(MerchantResponse m) {
        if (m == null) {
            return null;
        }
        return new Merchant(
                m.getId(),
                m.getUserId(),
                m.getName(),
                m.getDescription(),
                m.getAddress(),
                m.getContactEmail(),
                m.getContactPhone(),
                m.getStatus(),
                ProtoTime.parse(m.getCreatedAt()),
                ProtoTime.parse(m.getUpdatedAt()));
    }

    static Merchant toModel(ApiResponseMerchant resp) {
        return resp != null && resp.hasData() ? toModel(resp.getData()) : null;
    }
}
