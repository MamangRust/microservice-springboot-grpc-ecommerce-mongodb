package com.sanedge.ecommerce.merchant_social_link.handler;

import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.grpc.GrpcErrorMapper;
import com.sanedge.ecommerce.common.grpc.ProtoMappers;
import com.sanedge.ecommerce.merchant_social_link.service.MerchantSocialLinkService;
import com.sanedge.ecommerce.pb.merchant_detail.MerchantSocialMediaLinkResponse;
import com.sanedge.ecommerce.pb.merchant_social_link.ApiResponseMerchantSocial;
import com.sanedge.ecommerce.pb.merchant_social_link.CreateMerchantSocialRequest;
import com.sanedge.ecommerce.pb.merchant_social_link.MerchantSocialCommandServiceGrpc;
import com.sanedge.ecommerce.pb.merchant_social_link.UpdateMerchantSocialRequest;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MerchantSocialLinkCommandHandler extends MerchantSocialCommandServiceGrpc.MerchantSocialCommandServiceImplBase {

    private final MerchantSocialLinkService merchantSocialLinkService;

    @Override
    public void create(CreateMerchantSocialRequest request, StreamObserver<ApiResponseMerchantSocial> observer) {
        try {
            com.sanedge.ecommerce.merchant_social_link.domain.requests.CreateMerchantSocialRequest req = new com.sanedge.ecommerce.merchant_social_link.domain.requests.CreateMerchantSocialRequest();
            req.setMerchantDetailId(request.getMerchantDetailId());
            req.setPlatform(request.getPlatform());
            req.setUrl(request.getUrl());

            ApiResponse<com.sanedge.ecommerce.merchant_social_link.domain.responses.MerchantSocialMediaLinkResponse> res = merchantSocialLinkService
                    .create(req);

            observer.onNext(ApiResponseMerchantSocial.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .setData(toProto(res.getData(), request.getMerchantDetailId()))
                    .build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void update(UpdateMerchantSocialRequest request, StreamObserver<ApiResponseMerchantSocial> observer) {
        try {
            com.sanedge.ecommerce.merchant_social_link.domain.requests.UpdateMerchantSocialRequest req = new com.sanedge.ecommerce.merchant_social_link.domain.requests.UpdateMerchantSocialRequest();
            req.setId(request.getId());
            req.setMerchantDetailId(request.getMerchantDetailId());
            req.setPlatform(request.getPlatform());
            req.setUrl(request.getUrl());

            ApiResponse<com.sanedge.ecommerce.merchant_social_link.domain.responses.MerchantSocialMediaLinkResponse> res = merchantSocialLinkService
                    .update(req);

            observer.onNext(ApiResponseMerchantSocial.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .setData(toProto(res.getData(), request.getMerchantDetailId()))
                    .build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    private MerchantSocialMediaLinkResponse toProto(
            com.sanedge.ecommerce.merchant_social_link.domain.responses.MerchantSocialMediaLinkResponse data,
            int merchantDetailId) {
        return MerchantSocialMediaLinkResponse.newBuilder()
                .setId(data.getId() == null ? 0 : data.getId().intValue())
                .setMerchantDetailId(merchantDetailId)
                .setPlatform(ProtoMappers.nvl(data.getPlatform()))
                .setUrl(ProtoMappers.nvl(data.getUrl()))
                .setCreatedAt(ProtoMappers.nvl(data.getCreatedAt()))
                .setUpdatedAt(ProtoMappers.nvl(data.getUpdatedAt()))
                .build();
    }
}
