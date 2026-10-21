package com.sanedge.ecommerce.slider.handler;

import org.springframework.stereotype.Service;

import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.grpc.GrpcErrorMapper;
import com.sanedge.ecommerce.common.grpc.ProtoMappers;
import com.sanedge.ecommerce.pb.slider.ApiResponseSlider;
import com.sanedge.ecommerce.pb.slider.ApiResponseSliderAll;
import com.sanedge.ecommerce.pb.slider.ApiResponseSliderDelete;
import com.sanedge.ecommerce.pb.slider.ApiResponseSliderDeleteAt;
import com.sanedge.ecommerce.pb.slider.CreateSliderRequest;
import com.sanedge.ecommerce.pb.slider.FindByIdSliderRequest;
import com.sanedge.ecommerce.pb.slider.SliderCommandServiceGrpc;
import com.sanedge.ecommerce.pb.slider.SliderResponse;
import com.sanedge.ecommerce.pb.slider.SliderResponseDeleteAt;
import com.sanedge.ecommerce.pb.slider.UpdateSliderRequest;
import com.sanedge.ecommerce.slider.service.SliderCommandService;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SliderCommandHandler extends SliderCommandServiceGrpc.SliderCommandServiceImplBase {

    private final SliderCommandService sliderCommandService;

    @Override
    public void create(CreateSliderRequest request, StreamObserver<ApiResponseSlider> observer) {
        try {
            com.sanedge.ecommerce.slider.domain.requests.CreateSliderRequest req = new com.sanedge.ecommerce.slider.domain.requests.CreateSliderRequest();
            req.setNama(request.getName());
            req.setFilePath(request.getImage());

            ApiResponse<com.sanedge.ecommerce.slider.domain.responses.SliderResponse> res = sliderCommandService
                    .createSlider(req);

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
    public void update(UpdateSliderRequest request, StreamObserver<ApiResponseSlider> observer) {
        try {
            com.sanedge.ecommerce.slider.domain.requests.UpdateSliderRequest req = new com.sanedge.ecommerce.slider.domain.requests.UpdateSliderRequest();
            req.setId(request.getId());
            req.setNama(request.getName());
            req.setFilePath(request.getImage());

            ApiResponse<com.sanedge.ecommerce.slider.domain.responses.SliderResponse> res = sliderCommandService
                    .updateSlider(req);

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
    public void trashedSlider(FindByIdSliderRequest request, StreamObserver<ApiResponseSliderDeleteAt> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.slider.domain.responses.SliderResponseDeleteAt> res = sliderCommandService
                    .trashedSlider(request.getId());
            observer.onNext(toDeleteAt(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void restoreSlider(FindByIdSliderRequest request, StreamObserver<ApiResponseSliderDeleteAt> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.slider.domain.responses.SliderResponseDeleteAt> res = sliderCommandService
                    .restoreSlider(request.getId());
            observer.onNext(toDeleteAt(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void deleteSliderPermanent(FindByIdSliderRequest request, StreamObserver<ApiResponseSliderDelete> observer) {
        try {
            ApiResponse<Boolean> res = sliderCommandService.deleteSliderPermanent(request.getId());
            observer.onNext(ApiResponseSliderDelete.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void restoreAllSlider(Empty request, StreamObserver<ApiResponseSliderAll> observer) {
        try {
            ApiResponse<Boolean> res = sliderCommandService.restoreAllSliders();
            observer.onNext(toAll(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void deleteAllSliderPermanent(Empty request, StreamObserver<ApiResponseSliderAll> observer) {
        try {
            ApiResponse<Boolean> res = sliderCommandService.deleteAllSlidersPermanent();
            observer.onNext(toAll(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
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

    private ApiResponseSliderDeleteAt toDeleteAt(
            ApiResponse<com.sanedge.ecommerce.slider.domain.responses.SliderResponseDeleteAt> res) {
        ApiResponseSliderDeleteAt.Builder builder = ApiResponseSliderDeleteAt.newBuilder()
                .setStatus(ProtoMappers.nvl(res.getStatus()))
                .setMessage(ProtoMappers.nvl(res.getMessage()));
        if (res.getData() != null) {
            com.sanedge.ecommerce.slider.domain.responses.SliderResponseDeleteAt d = res.getData();
            builder.setData(SliderResponseDeleteAt.newBuilder()
                    .setId(d.getId() == null ? 0 : d.getId().intValue())
                    .setName(ProtoMappers.nvl(d.getName()))
                    .setImage(ProtoMappers.nvl(d.getImage()))
                    .setCreatedAt(ProtoMappers.nvl(d.getCreatedAt()))
                    .setUpdatedAt(ProtoMappers.nvl(d.getUpdatedAt()))
                    .setDeletedAt(StringValue.of(ProtoMappers.nvl(d.getDeletedAt())))
                    .build());
        }
        return builder.build();
    }

    private ApiResponseSliderAll toAll(ApiResponse<Boolean> res) {
        return ApiResponseSliderAll.newBuilder()
                .setStatus(ProtoMappers.nvl(res.getStatus()))
                .setMessage(ProtoMappers.nvl(res.getMessage()))
                .build();
    }
}
