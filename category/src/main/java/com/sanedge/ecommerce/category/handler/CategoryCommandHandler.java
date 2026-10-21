package com.sanedge.ecommerce.category.handler;

import org.springframework.stereotype.Service;

import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import com.sanedge.ecommerce.category.service.CategoryCommandService;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.grpc.GrpcErrorMapper;
import com.sanedge.ecommerce.common.grpc.ProtoMappers;
import com.sanedge.ecommerce.pb.category.ApiResponseCategory;
import com.sanedge.ecommerce.pb.category.ApiResponseCategoryAll;
import com.sanedge.ecommerce.pb.category.ApiResponseCategoryDelete;
import com.sanedge.ecommerce.pb.category.ApiResponseCategoryDeleteAt;
import com.sanedge.ecommerce.pb.category.CategoryCommandServiceGrpc;
import com.sanedge.ecommerce.pb.category.CategoryResponse;
import com.sanedge.ecommerce.pb.category.CategoryResponseDeleteAt;
import com.sanedge.ecommerce.pb.category.CreateCategoryRequest;
import com.sanedge.ecommerce.pb.category.FindByIdCategoryRequest;
import com.sanedge.ecommerce.pb.category.UpdateCategoryRequest;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryCommandHandler extends CategoryCommandServiceGrpc.CategoryCommandServiceImplBase {

    private final CategoryCommandService categoryCommandService;

    @Override
    public void create(CreateCategoryRequest request, StreamObserver<ApiResponseCategory> observer) {
        try {
            com.sanedge.ecommerce.category.domain.requests.CreateCategoryRequest req = new com.sanedge.ecommerce.category.domain.requests.CreateCategoryRequest();
            req.setName(request.getName());
            req.setDescription(request.getDescription());
            req.setSlugCategory(request.getSlugCategory());
            req.setImageCategory(request.getImageCategory());

            ApiResponse<com.sanedge.ecommerce.category.domain.responses.CategoryResponse> res = categoryCommandService
                    .createCategory(req);

            observer.onNext(ApiResponseCategory.newBuilder()
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
    public void update(UpdateCategoryRequest request, StreamObserver<ApiResponseCategory> observer) {
        try {
            com.sanedge.ecommerce.category.domain.requests.UpdateCategoryRequest req = new com.sanedge.ecommerce.category.domain.requests.UpdateCategoryRequest();
            req.setCategoryId(request.getCategoryId());
            req.setName(request.getName());
            req.setDescription(request.getDescription());
            req.setSlugCategory(request.getSlugCategory());
            req.setImageCategory(request.getImageCategory());

            ApiResponse<com.sanedge.ecommerce.category.domain.responses.CategoryResponse> res = categoryCommandService
                    .updateCategory(req);

            observer.onNext(ApiResponseCategory.newBuilder()
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
    public void trashedCategory(FindByIdCategoryRequest request, StreamObserver<ApiResponseCategoryDeleteAt> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.category.domain.responses.CategoryResponseDeleteAt> res = categoryCommandService
                    .trashedCategory(request.getId());
            observer.onNext(toDeleteAt(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void restoreCategory(FindByIdCategoryRequest request, StreamObserver<ApiResponseCategoryDeleteAt> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.category.domain.responses.CategoryResponseDeleteAt> res = categoryCommandService
                    .restoreCategory(request.getId());
            observer.onNext(toDeleteAt(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void deleteCategoryPermanent(FindByIdCategoryRequest request,
            StreamObserver<ApiResponseCategoryDelete> observer) {
        try {
            ApiResponse<Boolean> res = categoryCommandService.deleteCategoryPermanent(request.getId());
            observer.onNext(ApiResponseCategoryDelete.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void restoreAllCategory(Empty request, StreamObserver<ApiResponseCategoryAll> observer) {
        try {
            ApiResponse<Boolean> res = categoryCommandService.restoreAllCategories();
            observer.onNext(toAll(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void deleteAllCategoryPermanent(Empty request, StreamObserver<ApiResponseCategoryAll> observer) {
        try {
            ApiResponse<Boolean> res = categoryCommandService.deleteAllCategoriesPermanent();
            observer.onNext(toAll(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    private CategoryResponse toProto(com.sanedge.ecommerce.category.domain.responses.CategoryResponse r) {
        if (r == null) {
            return CategoryResponse.getDefaultInstance();
        }
        return CategoryResponse.newBuilder()
                .setId(r.getId() == null ? 0 : r.getId().intValue())
                .setName(ProtoMappers.nvl(r.getName()))
                .setDescription(ProtoMappers.nvl(r.getDescription()))
                .setSlugCategory(ProtoMappers.nvl(r.getSlugCategory()))
                .setImageCategory(ProtoMappers.nvl(r.getImageCategory()))
                .setCreatedAt(ProtoMappers.nvl(r.getCreatedAt()))
                .setUpdatedAt(ProtoMappers.nvl(r.getUpdatedAt()))
                .build();
    }

    private ApiResponseCategoryDeleteAt toDeleteAt(
            ApiResponse<com.sanedge.ecommerce.category.domain.responses.CategoryResponseDeleteAt> res) {
        CategoryResponseDeleteAt data = CategoryResponseDeleteAt.newBuilder()
                .setId(res.getData().getId() == null ? 0 : res.getData().getId().intValue())
                .setName(ProtoMappers.nvl(res.getData().getName()))
                .setDescription(ProtoMappers.nvl(res.getData().getDescription()))
                .setSlugCategory(ProtoMappers.nvl(res.getData().getSlugCategory()))
                .setImageCategory(ProtoMappers.nvl(res.getData().getImageCategory()))
                .setCreatedAt(ProtoMappers.nvl(res.getData().getCreatedAt()))
                .setUpdatedAt(ProtoMappers.nvl(res.getData().getUpdatedAt()))
                .setDeletedAt(StringValue.of(ProtoMappers.nvl(res.getData().getDeletedAt())))
                .build();
        return ApiResponseCategoryDeleteAt.newBuilder()
                .setStatus(ProtoMappers.nvl(res.getStatus()))
                .setMessage(ProtoMappers.nvl(res.getMessage()))
                .setData(data)
                .build();
    }

    private ApiResponseCategoryAll toAll(ApiResponse<Boolean> res) {
        return ApiResponseCategoryAll.newBuilder()
                .setStatus(ProtoMappers.nvl(res.getStatus()))
                .setMessage(ProtoMappers.nvl(res.getMessage()))
                .build();
    }
}
