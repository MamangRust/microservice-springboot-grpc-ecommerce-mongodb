package com.sanedge.ecommerce.slider.handler;

import java.util.List;

import org.springframework.stereotype.Service;

import com.google.protobuf.StringValue;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.common.grpc.GrpcErrorMapper;
import com.sanedge.ecommerce.common.grpc.ProtoMappers;
import com.sanedge.ecommerce.pb.slider.ApiResponsePaginationSlider;
import com.sanedge.ecommerce.pb.slider.ApiResponsePaginationSliderDeleteAt;
import com.sanedge.ecommerce.pb.slider.ApiResponseSlider;
import com.sanedge.ecommerce.pb.slider.FindAllSliderRequest;
import com.sanedge.ecommerce.pb.slider.FindByIdSliderRequest;
import com.sanedge.ecommerce.pb.slider.SliderQueryServiceGrpc;
import com.sanedge.ecommerce.pb.slider.SliderResponse;
import com.sanedge.ecommerce.pb.slider.SliderResponseDeleteAt;
import com.sanedge.ecommerce.slider.service.SliderQueryService;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SliderQueryHandler extends SliderQueryServiceGrpc.SliderQueryServiceImplBase {

    private final SliderQueryService sliderQueryService;

    @Override
    public void findAll(FindAllSliderRequest request, StreamObserver<ApiResponsePaginationSlider> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.slider.domain.responses.SliderResponse>> res = sliderQueryService
                    .findAll(toFindAll(request));

            ApiResponsePaginationSlider.Builder builder = ApiResponsePaginationSlider.newBuilder()
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
    public void findById(FindByIdSliderRequest request, StreamObserver<ApiResponseSlider> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.slider.domain.responses.SliderResponse> res = sliderQueryService
                    .findById(request.getId());

            ApiResponseSlider.Builder builder = ApiResponseSlider.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            if (res.getData() != null) {
                builder.setData(toProto(res.getData()));
            }

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findByTrashed(FindAllSliderRequest request, StreamObserver<ApiResponsePaginationSliderDeleteAt> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.slider.domain.responses.SliderResponseDeleteAt>> res = sliderQueryService
                    .findByTrashed(toFindAll(request));

            ApiResponsePaginationSliderDeleteAt.Builder builder = ApiResponsePaginationSliderDeleteAt.newBuilder()
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
    public void findByActive(FindAllSliderRequest request, StreamObserver<ApiResponsePaginationSliderDeleteAt> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.slider.domain.responses.SliderResponseDeleteAt>> res = sliderQueryService
                    .findByActive(toFindAll(request));

            ApiResponsePaginationSliderDeleteAt.Builder builder = ApiResponsePaginationSliderDeleteAt.newBuilder()
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

    private com.sanedge.ecommerce.slider.domain.requests.FindAllSliderRequest toFindAll(FindAllSliderRequest request) {
        com.sanedge.ecommerce.slider.domain.requests.FindAllSliderRequest req = new com.sanedge.ecommerce.slider.domain.requests.FindAllSliderRequest();
        req.setPage(request.getPage());
        req.setPageSize(request.getPageSize());
        req.setSearch(request.getSearch());
        return req;
    }

    private SliderResponse toProto(com.sanedge.ecommerce.slider.domain.responses.SliderResponse r) {
        return SliderResponse.newBuilder()
                .setId(r.getId() == null ? 0 : r.getId().intValue())
                .setName(ProtoMappers.nvl(r.getName()))
                .setImage(ProtoMappers.nvl(r.getImage()))
                .setCreatedAt(ProtoMappers.nvl(r.getCreatedAt()))
                .setUpdatedAt(ProtoMappers.nvl(r.getUpdatedAt()))
                .build();
    }

    private SliderResponseDeleteAt toProtoDeleteAt(
            com.sanedge.ecommerce.slider.domain.responses.SliderResponseDeleteAt r) {
        return SliderResponseDeleteAt.newBuilder()
                .setId(r.getId() == null ? 0 : r.getId().intValue())
                .setName(ProtoMappers.nvl(r.getName()))
                .setImage(ProtoMappers.nvl(r.getImage()))
                .setCreatedAt(ProtoMappers.nvl(r.getCreatedAt()))
                .setUpdatedAt(ProtoMappers.nvl(r.getUpdatedAt()))
                .setDeletedAt(StringValue.of(ProtoMappers.nvl(r.getDeletedAt())))
                .build();
    }
}
