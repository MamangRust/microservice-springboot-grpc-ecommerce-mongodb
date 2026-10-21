package com.sanedge.ecommerce.merchant_detail.handler;

import java.util.List;

import org.springframework.stereotype.Service;

import com.google.protobuf.StringValue;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.common.grpc.GrpcErrorMapper;
import com.sanedge.ecommerce.common.grpc.ProtoMappers;
import com.sanedge.ecommerce.merchant_detail.domain.requests.FindAllMerchantDetailRequest;
import com.sanedge.ecommerce.merchant_detail.service.MerchantDetailQueryService;
import com.sanedge.ecommerce.pb.merchant.FindAllMerchantRequest;
import com.sanedge.ecommerce.pb.merchant_detail.ApiResponseMerchantDetail;
import com.sanedge.ecommerce.pb.merchant_detail.ApiResponsePaginationMerchantDetail;
import com.sanedge.ecommerce.pb.merchant_detail.ApiResponsePaginationMerchantDetailDeleteAt;
import com.sanedge.ecommerce.pb.merchant_detail.FindByIdMerchantDetailRequest;
import com.sanedge.ecommerce.pb.merchant_detail.MerchantDetailQueryServiceGrpc;
import com.sanedge.ecommerce.pb.merchant_detail.MerchantDetailResponse;
import com.sanedge.ecommerce.pb.merchant_detail.MerchantDetailResponseDeleteAt;
import com.sanedge.ecommerce.pb.merchant_detail.MerchantSocialMediaLinkResponse;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MerchantDetailQueryHandler extends MerchantDetailQueryServiceGrpc.MerchantDetailQueryServiceImplBase {

    private final MerchantDetailQueryService merchantDetailQueryService;

    @Override
    public void findAll(FindAllMerchantRequest request, StreamObserver<ApiResponsePaginationMerchantDetail> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.merchant_detail.domain.responses.MerchantDetailRelationResponse>> res = merchantDetailQueryService
                    .findAll(toRequest(request));

            ApiResponsePaginationMerchantDetail.Builder builder = ApiResponsePaginationMerchantDetail.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .setPagination(ProtoMappers.toProtoPagination(res.getPagination()));
            res.getData().forEach(r -> builder.addData(toProtoRelation(r)));

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findById(FindByIdMerchantDetailRequest request, StreamObserver<ApiResponseMerchantDetail> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.merchant_detail.domain.responses.MerchantDetailRelationResponse> res = merchantDetailQueryService
                    .findById(request.getId());

            observer.onNext(ApiResponseMerchantDetail.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .setData(toProtoRelation(res.getData()))
                    .build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findByActive(FindAllMerchantRequest request,
            StreamObserver<ApiResponsePaginationMerchantDetailDeleteAt> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.merchant_detail.domain.responses.MerchantDetailRelationResponseDeleteAt>> res = merchantDetailQueryService
                    .findByActive(toRequest(request));

            ApiResponsePaginationMerchantDetailDeleteAt.Builder builder = ApiResponsePaginationMerchantDetailDeleteAt
                    .newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .setPagination(ProtoMappers.toProtoPagination(res.getPagination()));
            res.getData().forEach(r -> builder.addData(toProtoRelationDeleteAt(r)));

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findByTrashed(FindAllMerchantRequest request,
            StreamObserver<ApiResponsePaginationMerchantDetailDeleteAt> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.merchant_detail.domain.responses.MerchantDetailRelationResponseDeleteAt>> res = merchantDetailQueryService
                    .findByTrashed(toRequest(request));

            ApiResponsePaginationMerchantDetailDeleteAt.Builder builder = ApiResponsePaginationMerchantDetailDeleteAt
                    .newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .setPagination(ProtoMappers.toProtoPagination(res.getPagination()));
            res.getData().forEach(r -> builder.addData(toProtoRelationDeleteAt(r)));

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    private FindAllMerchantDetailRequest toRequest(FindAllMerchantRequest request) {
        FindAllMerchantDetailRequest req = new FindAllMerchantDetailRequest();
        req.setPage(request.getPage());
        req.setPageSize(request.getPageSize());
        req.setSearch(request.getSearch());
        return req;
    }

    private MerchantDetailResponse toProtoRelation(
            com.sanedge.ecommerce.merchant_detail.domain.responses.MerchantDetailRelationResponse r) {
        MerchantDetailResponse.Builder builder = MerchantDetailResponse.newBuilder()
                .setId(r.getId() == null ? 0 : r.getId().intValue())
                .setMerchantId(r.getMerchantId() == null ? 0 : r.getMerchantId())
                .setDisplayName(ProtoMappers.nvl(r.getDisplayName()))
                .setCoverImageUrl(ProtoMappers.nvl(r.getCoverImageUrl()))
                .setLogoUrl(ProtoMappers.nvl(r.getLogoUrl()))
                .setShortDescription(ProtoMappers.nvl(r.getShortDescription()))
                .setWebsiteUrl(ProtoMappers.nvl(r.getWebsiteUrl()))
                .setCreatedAt(ProtoMappers.nvl(r.getCreatedAt()))
                .setUpdatedAt(ProtoMappers.nvl(r.getUpdatedAt()));
        if (r.getSocialMediaLinks() != null) {
            r.getSocialMediaLinks().forEach(link -> builder.addSocialMediaLinks(MerchantSocialMediaLinkResponse
                    .newBuilder()
                    .setId(link.getId() == null ? 0 : link.getId().intValue())
                    .setPlatform(ProtoMappers.nvl(link.getPlatform()))
                    .setUrl(ProtoMappers.nvl(link.getUrl()))
                    .setCreatedAt(ProtoMappers.nvl(link.getCreatedAt()))
                    .setUpdatedAt(ProtoMappers.nvl(link.getUpdatedAt()))
                    .build()));
        }
        return builder.build();
    }

    private MerchantDetailResponseDeleteAt toProtoRelationDeleteAt(
            com.sanedge.ecommerce.merchant_detail.domain.responses.MerchantDetailRelationResponseDeleteAt r) {
        MerchantDetailResponseDeleteAt.Builder builder = MerchantDetailResponseDeleteAt.newBuilder()
                .setId(r.getId() == null ? 0 : r.getId().intValue())
                .setMerchantId(r.getMerchantId() == null ? 0 : r.getMerchantId())
                .setDisplayName(ProtoMappers.nvl(r.getDisplayName()))
                .setCoverImageUrl(ProtoMappers.nvl(r.getCoverImageUrl()))
                .setLogoUrl(ProtoMappers.nvl(r.getLogoUrl()))
                .setShortDescription(ProtoMappers.nvl(r.getShortDescription()))
                .setWebsiteUrl(ProtoMappers.nvl(r.getWebsiteUrl()))
                .setCreatedAt(ProtoMappers.nvl(r.getCreatedAt()))
                .setUpdatedAt(ProtoMappers.nvl(r.getUpdatedAt()))
                .setDeletedAt(StringValue.of(ProtoMappers.nvl(r.getDeletedAt())));
        if (r.getSocialMediaLinks() != null) {
            r.getSocialMediaLinks().forEach(link -> builder.addSocialMediaLinks(MerchantSocialMediaLinkResponse
                    .newBuilder()
                    .setId(link.getId() == null ? 0 : link.getId().intValue())
                    .setPlatform(ProtoMappers.nvl(link.getPlatform()))
                    .setUrl(ProtoMappers.nvl(link.getUrl()))
                    .setCreatedAt(ProtoMappers.nvl(link.getCreatedAt()))
                    .setUpdatedAt(ProtoMappers.nvl(link.getUpdatedAt()))
                    .build()));
        }
        return builder.build();
    }
}
