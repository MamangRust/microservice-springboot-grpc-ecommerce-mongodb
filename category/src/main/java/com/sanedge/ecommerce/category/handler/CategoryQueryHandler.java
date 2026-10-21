package com.sanedge.ecommerce.category.handler;

import java.util.List;

import org.springframework.stereotype.Service;

import com.google.protobuf.StringValue;
import com.sanedge.ecommerce.category.service.CategoryQueryService;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.common.grpc.GrpcErrorMapper;
import com.sanedge.ecommerce.common.grpc.ProtoMappers;
import com.sanedge.ecommerce.pb.category.ApiResponseCategory;
import com.sanedge.ecommerce.pb.category.ApiResponsePaginationCategory;
import com.sanedge.ecommerce.pb.category.ApiResponsePaginationCategoryDeleteAt;
import com.sanedge.ecommerce.pb.category.CategoryQueryServiceGrpc;
import com.sanedge.ecommerce.pb.category.CategoryResponse;
import com.sanedge.ecommerce.pb.category.CategoryResponseDeleteAt;
import com.sanedge.ecommerce.pb.category.FindAllCategoryRequest;
import com.sanedge.ecommerce.pb.category.FindByIdCategoryRequest;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryQueryHandler extends CategoryQueryServiceGrpc.CategoryQueryServiceImplBase {

    private final CategoryQueryService categoryQueryService;

    @Override
    public void findAll(FindAllCategoryRequest request, StreamObserver<ApiResponsePaginationCategory> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.category.domain.responses.CategoryResponse>> res = categoryQueryService
                    .findAll(toFindAll(request));

            ApiResponsePaginationCategory.Builder builder = ApiResponsePaginationCategory.newBuilder()
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
    public void findById(FindByIdCategoryRequest request, StreamObserver<ApiResponseCategory> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.category.domain.responses.CategoryResponse> res = categoryQueryService
                    .findById(request.getId());

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
    public void findByActive(FindAllCategoryRequest request,
            StreamObserver<ApiResponsePaginationCategoryDeleteAt> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.category.domain.responses.CategoryResponseDeleteAt>> res = categoryQueryService
                    .findByActive(toFindAll(request));

            ApiResponsePaginationCategoryDeleteAt.Builder builder = ApiResponsePaginationCategoryDeleteAt.newBuilder()
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
    public void findByTrashed(FindAllCategoryRequest request,
            StreamObserver<ApiResponsePaginationCategoryDeleteAt> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.category.domain.responses.CategoryResponseDeleteAt>> res = categoryQueryService
                    .findByTrashed(toFindAll(request));

            ApiResponsePaginationCategoryDeleteAt.Builder builder = ApiResponsePaginationCategoryDeleteAt.newBuilder()
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

    private com.sanedge.ecommerce.category.domain.requests.FindAllCategoryRequest toFindAll(
            FindAllCategoryRequest request) {
        com.sanedge.ecommerce.category.domain.requests.FindAllCategoryRequest req = new com.sanedge.ecommerce.category.domain.requests.FindAllCategoryRequest();
        req.setPage(request.getPage());
        req.setPageSize(request.getPageSize());
        req.setSearch(request.getSearch());
        return req;
    }

    private CategoryResponse toProto(com.sanedge.ecommerce.category.domain.responses.CategoryResponse r) {
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

    private CategoryResponseDeleteAt toProtoDeleteAt(
            com.sanedge.ecommerce.category.domain.responses.CategoryResponseDeleteAt r) {
        return CategoryResponseDeleteAt.newBuilder()
                .setId(r.getId() == null ? 0 : r.getId().intValue())
                .setName(ProtoMappers.nvl(r.getName()))
                .setDescription(ProtoMappers.nvl(r.getDescription()))
                .setSlugCategory(ProtoMappers.nvl(r.getSlugCategory()))
                .setImageCategory(ProtoMappers.nvl(r.getImageCategory()))
                .setCreatedAt(ProtoMappers.nvl(r.getCreatedAt()))
                .setUpdatedAt(ProtoMappers.nvl(r.getUpdatedAt()))
                .setDeletedAt(StringValue.of(ProtoMappers.nvl(r.getDeletedAt())))
                .build();
    }
}
