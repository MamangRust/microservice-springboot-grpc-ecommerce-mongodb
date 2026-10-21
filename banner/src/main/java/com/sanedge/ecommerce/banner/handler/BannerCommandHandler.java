package com.sanedge.ecommerce.banner.handler;

import org.springframework.stereotype.Service;

import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import com.sanedge.ecommerce.banner.service.BannerCommandService;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.grpc.GrpcErrorMapper;
import com.sanedge.ecommerce.common.grpc.ProtoMappers;
import com.sanedge.ecommerce.pb.banner.ApiResponseBanner;
import com.sanedge.ecommerce.pb.banner.ApiResponseBannerAll;
import com.sanedge.ecommerce.pb.banner.ApiResponseBannerDelete;
import com.sanedge.ecommerce.pb.banner.ApiResponseBannerDeleteAt;
import com.sanedge.ecommerce.pb.banner.BannerCommandServiceGrpc;
import com.sanedge.ecommerce.pb.banner.BannerResponse;
import com.sanedge.ecommerce.pb.banner.BannerResponseDeleteAt;
import com.sanedge.ecommerce.pb.banner.CreateBannerRequest;
import com.sanedge.ecommerce.pb.banner.FindByIdBannerRequest;
import com.sanedge.ecommerce.pb.banner.UpdateBannerRequest;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BannerCommandHandler extends BannerCommandServiceGrpc.BannerCommandServiceImplBase {

    private final BannerCommandService bannerCommandService;

    @Override
    public void create(CreateBannerRequest request, StreamObserver<ApiResponseBanner> observer) {
        try {
            com.sanedge.ecommerce.banner.domain.requests.CreateBannerRequest req = new com.sanedge.ecommerce.banner.domain.requests.CreateBannerRequest();
            req.setName(request.getName());
            req.setStartDate(request.getStartDate());
            req.setEndDate(request.getEndDate());
            req.setStartTime(request.getStartTime());
            req.setEndTime(request.getEndTime());
            req.setIsActive(request.getIsActive());

            ApiResponse<com.sanedge.ecommerce.banner.domain.responses.BannerResponse> res = bannerCommandService
                    .createBanner(req);

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
    public void update(UpdateBannerRequest request, StreamObserver<ApiResponseBanner> observer) {
        try {
            com.sanedge.ecommerce.banner.domain.requests.UpdateBannerRequest req = new com.sanedge.ecommerce.banner.domain.requests.UpdateBannerRequest();
            req.setBannerID(request.getBannerId());
            req.setName(request.getName());
            req.setStartDate(request.getStartDate());
            req.setEndDate(request.getEndDate());
            req.setStartTime(request.getStartTime());
            req.setEndTime(request.getEndTime());
            req.setIsActive(request.getIsActive());

            ApiResponse<com.sanedge.ecommerce.banner.domain.responses.BannerResponse> res = bannerCommandService
                    .updateBanner(req);

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
    public void trash(FindByIdBannerRequest request, StreamObserver<ApiResponseBannerDeleteAt> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.banner.domain.responses.BannerResponseDeleteAt> res = bannerCommandService
                    .trashedBanner(request.getId());
            observer.onNext(toDeleteAt(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void restore(FindByIdBannerRequest request, StreamObserver<ApiResponseBannerDeleteAt> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.banner.domain.responses.BannerResponseDeleteAt> res = bannerCommandService
                    .restoreBanner(request.getId());
            observer.onNext(toDeleteAt(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void deletePermanent(FindByIdBannerRequest request, StreamObserver<ApiResponseBannerDelete> observer) {
        try {
            ApiResponse<Boolean> res = bannerCommandService.deleteBannerPermanent(request.getId());
            observer.onNext(ApiResponseBannerDelete.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void restoreAll(Empty request, StreamObserver<ApiResponseBannerAll> observer) {
        try {
            ApiResponse<Boolean> res = bannerCommandService.restoreAllBanner();
            observer.onNext(toAll(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void deleteAll(Empty request, StreamObserver<ApiResponseBannerAll> observer) {
        try {
            ApiResponse<Boolean> res = bannerCommandService.deleteAllBannerPermanent();
            observer.onNext(toAll(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
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

    private ApiResponseBannerDeleteAt toDeleteAt(
            ApiResponse<com.sanedge.ecommerce.banner.domain.responses.BannerResponseDeleteAt> res) {
        com.sanedge.ecommerce.banner.domain.responses.BannerResponseDeleteAt d = res.getData();
        BannerResponseDeleteAt data = d == null ? BannerResponseDeleteAt.getDefaultInstance()
                : BannerResponseDeleteAt.newBuilder()
                        .setBannerId(d.getId() == null ? 0 : d.getId().intValue())
                        .setName(ProtoMappers.nvl(d.getName()))
                        .setStartDate(ProtoMappers.nvl(d.getStartDate()))
                        .setEndDate(ProtoMappers.nvl(d.getEndDate()))
                        .setStartTime(ProtoMappers.nvl(d.getStartTime()))
                        .setEndTime(ProtoMappers.nvl(d.getEndTime()))
                        .setIsActive(d.getIsActive() != null && d.getIsActive())
                        .setCreatedAt(ProtoMappers.nvl(d.getCreatedAt()))
                        .setUpdatedAt(ProtoMappers.nvl(d.getUpdatedAt()))
                        .setDeletedAt(StringValue.of(ProtoMappers.nvl(d.getDeletedAt())))
                        .build();
        return ApiResponseBannerDeleteAt.newBuilder()
                .setStatus(ProtoMappers.nvl(res.getStatus()))
                .setMessage(ProtoMappers.nvl(res.getMessage()))
                .setData(data)
                .build();
    }

    private ApiResponseBannerAll toAll(ApiResponse<Boolean> res) {
        return ApiResponseBannerAll.newBuilder()
                .setStatus(ProtoMappers.nvl(res.getStatus()))
                .setMessage(ProtoMappers.nvl(res.getMessage()))
                .build();
    }
}
