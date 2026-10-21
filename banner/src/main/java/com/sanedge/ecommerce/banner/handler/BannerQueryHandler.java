package com.sanedge.ecommerce.banner.handler;

import java.util.List;

import org.springframework.stereotype.Service;

import com.google.protobuf.StringValue;
import com.sanedge.ecommerce.banner.service.BannerQueryService;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.common.grpc.GrpcErrorMapper;
import com.sanedge.ecommerce.common.grpc.ProtoMappers;
import com.sanedge.ecommerce.pb.banner.ApiResponseBanner;
import com.sanedge.ecommerce.pb.banner.ApiResponsePaginationBanner;
import com.sanedge.ecommerce.pb.banner.ApiResponsePaginationBannerDeleteAt;
import com.sanedge.ecommerce.pb.banner.BannerQueryServiceGrpc;
import com.sanedge.ecommerce.pb.banner.BannerResponse;
import com.sanedge.ecommerce.pb.banner.BannerResponseDeleteAt;
import com.sanedge.ecommerce.pb.banner.FindAllBannerRequest;
import com.sanedge.ecommerce.pb.banner.FindByIdBannerRequest;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BannerQueryHandler extends BannerQueryServiceGrpc.BannerQueryServiceImplBase {

    private final BannerQueryService bannerQueryService;

    @Override
    public void findAll(FindAllBannerRequest request, StreamObserver<ApiResponsePaginationBanner> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.banner.domain.responses.BannerResponse>> res = bannerQueryService
                    .findAll(toFindAll(request));

            ApiResponsePaginationBanner.Builder builder = ApiResponsePaginationBanner.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .setPagination(ProtoMappers.toProtoPagination(res.getPagination()));
            res.getData().forEach(r -> builder.addData(toProto(r)));

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findById(FindByIdBannerRequest request, StreamObserver<ApiResponseBanner> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.banner.domain.responses.BannerResponse> res = bannerQueryService
                    .findById(request.getId());

            observer.onNext(ApiResponseBanner.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .setData(toProto(res.getData()))
                    .build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findByActive(FindAllBannerRequest request, StreamObserver<ApiResponsePaginationBannerDeleteAt> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.banner.domain.responses.BannerResponseDeleteAt>> res = bannerQueryService
                    .findByActive(toFindAll(request));

            ApiResponsePaginationBannerDeleteAt.Builder builder = ApiResponsePaginationBannerDeleteAt.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .setPagination(ProtoMappers.toProtoPagination(res.getPagination()));
            res.getData().forEach(r -> builder.addData(toProtoDeleteAt(r)));

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findByTrashed(FindAllBannerRequest request, StreamObserver<ApiResponsePaginationBannerDeleteAt> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.banner.domain.responses.BannerResponseDeleteAt>> res = bannerQueryService
                    .findByTrashed(toFindAll(request));

            ApiResponsePaginationBannerDeleteAt.Builder builder = ApiResponsePaginationBannerDeleteAt.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .setPagination(ProtoMappers.toProtoPagination(res.getPagination()));
            res.getData().forEach(r -> builder.addData(toProtoDeleteAt(r)));

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    private com.sanedge.ecommerce.banner.domain.requests.FindAllBannerRequest toFindAll(FindAllBannerRequest request) {
        com.sanedge.ecommerce.banner.domain.requests.FindAllBannerRequest req = new com.sanedge.ecommerce.banner.domain.requests.FindAllBannerRequest();
        req.setPage(request.getPage());
        req.setPageSize(request.getPageSize());
        req.setSearch(request.getSearch());
        return req;
    }

    private BannerResponse toProto(com.sanedge.ecommerce.banner.domain.responses.BannerResponse r) {
        if (r == null) {
            return BannerResponse.getDefaultInstance();
        }
        return BannerResponse.newBuilder()
                .setBannerId(r.getId() == null ? 0 : r.getId().intValue())
                .setName(ProtoMappers.nvl(r.getName()))
                .setStartDate(ProtoMappers.nvl(r.getStartDate()))
                .setEndDate(ProtoMappers.nvl(r.getEndDate()))
                .setStartTime(ProtoMappers.nvl(r.getStartTime()))
                .setEndTime(ProtoMappers.nvl(r.getEndTime()))
                .setIsActive(r.getIsActive() != null && r.getIsActive())
                .setCreatedAt(ProtoMappers.nvl(r.getCreatedAt()))
                .setUpdatedAt(ProtoMappers.nvl(r.getUpdatedAt()))
                .build();
    }

    private BannerResponseDeleteAt toProtoDeleteAt(
            com.sanedge.ecommerce.banner.domain.responses.BannerResponseDeleteAt r) {
        if (r == null) {
            return BannerResponseDeleteAt.getDefaultInstance();
        }
        return BannerResponseDeleteAt.newBuilder()
                .setBannerId(r.getId() == null ? 0 : r.getId().intValue())
                .setName(ProtoMappers.nvl(r.getName()))
                .setStartDate(ProtoMappers.nvl(r.getStartDate()))
                .setEndDate(ProtoMappers.nvl(r.getEndDate()))
                .setStartTime(ProtoMappers.nvl(r.getStartTime()))
                .setEndTime(ProtoMappers.nvl(r.getEndTime()))
                .setIsActive(r.getIsActive() != null && r.getIsActive())
                .setCreatedAt(ProtoMappers.nvl(r.getCreatedAt()))
                .setUpdatedAt(ProtoMappers.nvl(r.getUpdatedAt()))
                .setDeletedAt(StringValue.of(ProtoMappers.nvl(r.getDeletedAt())))
                .build();
    }
}
