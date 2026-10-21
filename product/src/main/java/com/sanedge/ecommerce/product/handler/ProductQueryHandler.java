package com.sanedge.ecommerce.product.handler;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.common.grpc.GrpcErrorMapper;
import com.sanedge.ecommerce.common.grpc.ProtoMappers;
import com.sanedge.ecommerce.pb.product.ApiResponsePaginationProduct;
import com.sanedge.ecommerce.pb.product.ApiResponsePaginationProductDeleteAt;
import com.sanedge.ecommerce.pb.product.ApiResponseProduct;
import com.sanedge.ecommerce.pb.product.FindAllProductCategoryRequest;
import com.sanedge.ecommerce.pb.product.FindAllProductMerchantRequest;
import com.sanedge.ecommerce.pb.product.FindAllProductRequest;
import com.sanedge.ecommerce.pb.product.FindByIdProductRequest;
import com.sanedge.ecommerce.pb.product.ProductQueryServiceGrpc;
import com.sanedge.ecommerce.pb.product.ProductResponse;
import com.sanedge.ecommerce.pb.product.ProductResponseDeleteAt;
import com.sanedge.ecommerce.product.domain.requests.FindAllProductByCategoryRequest;
import com.sanedge.ecommerce.product.domain.requests.FindAllProductByMerchantRequest;
import com.sanedge.ecommerce.product.service.ProductQueryService;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductQueryHandler extends ProductQueryServiceGrpc.ProductQueryServiceImplBase {

    private final ProductQueryService productQueryService;

    @Override
    public void findAll(FindAllProductRequest request, StreamObserver<ApiResponsePaginationProduct> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.product.domain.responses.ProductResponse>> res = productQueryService
                    .findAll(toFindAll(request));

            ApiResponsePaginationProduct.Builder builder = ApiResponsePaginationProduct.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .setPagination(ProtoMappers.toProtoPagination(res.getPagination()));
            if (res.getData() != null) {
                res.getData().forEach(r -> builder.addData(toProto(r)));
            }

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findById(FindByIdProductRequest request, StreamObserver<ApiResponseProduct> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.product.domain.responses.ProductResponse> res = productQueryService
                    .findById((long) request.getId());

            ApiResponseProduct.Builder builder = ApiResponseProduct.newBuilder()
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
    public void findByMerchant(FindAllProductMerchantRequest request,
            StreamObserver<ApiResponsePaginationProduct> observer) {
        try {
            FindAllProductByMerchantRequest req = new FindAllProductByMerchantRequest();
            req.setMerchantId(request.getMerchantId());
            req.setSearch(request.getSearch());
            req.setCategoryId(request.getCategoryId() == 0 ? null : request.getCategoryId());
            req.setMinPrice(request.getMinPrice() == 0 ? null : request.getMinPrice());
            req.setMaxPrice(request.getMaxPrice() == 0 ? null : request.getMaxPrice());
            req.setPage(request.getPage());
            req.setPageSize(request.getPageSize());

            ApiResponsePagination<List<com.sanedge.ecommerce.product.domain.responses.ProductResponse>> res = productQueryService
                    .findByMerchant(req);

            ApiResponsePaginationProduct.Builder builder = ApiResponsePaginationProduct.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .setPagination(ProtoMappers.toProtoPagination(res.getPagination()));
            if (res.getData() != null) {
                res.getData().forEach(r -> builder.addData(toProto(r)));
            }

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findByCategory(FindAllProductCategoryRequest request,
            StreamObserver<ApiResponsePaginationProduct> observer) {
        try {
            FindAllProductByCategoryRequest req = new FindAllProductByCategoryRequest();
            req.setCategoryName(request.getCategoryName());
            req.setSearch(request.getSearch());
            req.setMinPrice(request.getMinPrice() == 0 ? null : request.getMinPrice());
            req.setMaxPrice(request.getMaxPrice() == 0 ? null : request.getMaxPrice());
            req.setPage(request.getPage());
            req.setPageSize(request.getPageSize());

            ApiResponsePagination<List<com.sanedge.ecommerce.product.domain.responses.ProductResponse>> res = productQueryService
                    .findByCategoryName(req);

            ApiResponsePaginationProduct.Builder builder = ApiResponsePaginationProduct.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .setPagination(ProtoMappers.toProtoPagination(res.getPagination()));
            if (res.getData() != null) {
                res.getData().forEach(r -> builder.addData(toProto(r)));
            }

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findByActive(FindAllProductRequest request,
            StreamObserver<ApiResponsePaginationProductDeleteAt> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.product.domain.responses.ProductResponseDeleteAt>> res = productQueryService
                    .findActiveProducts(toFindAll(request));

            ApiResponsePaginationProductDeleteAt.Builder builder = ApiResponsePaginationProductDeleteAt.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .setPagination(ProtoMappers.toProtoPagination(res.getPagination()));
            if (res.getData() != null) {
                res.getData().forEach(r -> builder.addData(toProtoDeleteAt(r)));
            }

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findByTrashed(FindAllProductRequest request,
            StreamObserver<ApiResponsePaginationProductDeleteAt> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.product.domain.responses.ProductResponseDeleteAt>> res = productQueryService
                    .findTrashedProducts(toFindAll(request));

            ApiResponsePaginationProductDeleteAt.Builder builder = ApiResponsePaginationProductDeleteAt.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .setPagination(ProtoMappers.toProtoPagination(res.getPagination()));
            if (res.getData() != null) {
                res.getData().forEach(r -> builder.addData(toProtoDeleteAt(r)));
            }

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    private com.sanedge.ecommerce.product.domain.requests.FindAllProductRequest toFindAll(
            FindAllProductRequest request) {
        com.sanedge.ecommerce.product.domain.requests.FindAllProductRequest req = new com.sanedge.ecommerce.product.domain.requests.FindAllProductRequest();
        req.setPage(request.getPage());
        req.setPageSize(request.getPageSize());
        req.setSearch(request.getSearch());
        return req;
    }

    private ProductResponse toProto(com.sanedge.ecommerce.product.domain.responses.ProductResponse r) {
        return ProductResponse.newBuilder()
                .setId(r.getId() == null ? 0 : r.getId().intValue())
                .setMerchantId(r.getMerchantId() == null ? 0 : r.getMerchantId())
                .setCategoryId(r.getCategoryId() == null ? 0 : r.getCategoryId())
                .setName(ProtoMappers.nvl(r.getName()))
                .setDescription(ProtoMappers.nvl(r.getDescription()))
                .setPrice(r.getPrice() == null ? 0 : r.getPrice())
                .setCountInStock(r.getCountInStock() == null ? 0 : r.getCountInStock())
                .setBrand(ProtoMappers.nvl(r.getBrand()))
                .setWeight(r.getWeight() == null ? 0 : r.getWeight())
                .setRating(r.getRating() == null ? 0f : r.getRating())
                .setSlugProduct(ProtoMappers.nvl(r.getSlugProduct()))
                .setImageProduct(ProtoMappers.nvl(r.getImageProduct()))
                .setCreatedAt(ProtoMappers.nvl(r.getCreatedAt()))
                .setUpdatedAt(ProtoMappers.nvl(r.getUpdatedAt()))
                .build();
    }

    private ProductResponseDeleteAt toProtoDeleteAt(
            com.sanedge.ecommerce.product.domain.responses.ProductResponseDeleteAt r) {
        return ProductResponseDeleteAt.newBuilder()
                .setId(r.getId() == null ? 0 : r.getId().intValue())
                .setMerchantId(r.getMerchantId() == null ? 0 : r.getMerchantId())
                .setCategoryId(r.getCategoryId() == null ? 0 : r.getCategoryId())
                .setName(ProtoMappers.nvl(r.getName()))
                .setDescription(ProtoMappers.nvl(r.getDescription()))
                .setPrice(r.getPrice() == null ? 0 : r.getPrice())
                .setCountInStock(r.getCountInStock() == null ? 0 : r.getCountInStock())
                .setBrand(ProtoMappers.nvl(r.getBrand()))
                .setWeight(r.getWeight() == null ? 0 : r.getWeight())
                .setRating(r.getRating() == null ? 0f : r.getRating())
                .setSlugProduct(ProtoMappers.nvl(r.getSlugProduct()))
                .setImageProduct(ProtoMappers.nvl(r.getImageProduct()))
                .setCreatedAt(ProtoMappers.nvl(r.getCreatedAt()))
                .setUpdatedAt(ProtoMappers.nvl(r.getUpdatedAt()))
                .setDeletedAt(com.google.protobuf.StringValue.of(ProtoMappers.nvl(r.getDeletedAt())))
                .build();
    }
}
