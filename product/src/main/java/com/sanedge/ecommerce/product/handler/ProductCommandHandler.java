package com.sanedge.ecommerce.product.handler;

import org.springframework.stereotype.Service;

import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.grpc.GrpcErrorMapper;
import com.sanedge.ecommerce.common.grpc.ProtoMappers;
import com.sanedge.ecommerce.pb.product.ApiResponseProduct;
import com.sanedge.ecommerce.pb.product.ApiResponseProductAll;
import com.sanedge.ecommerce.pb.product.ApiResponseProductDelete;
import com.sanedge.ecommerce.pb.product.ApiResponseProductDeleteAt;
import com.sanedge.ecommerce.pb.product.CreateProductRequest;
import com.sanedge.ecommerce.pb.product.FindByIdProductRequest;
import com.sanedge.ecommerce.pb.product.ProductCommandServiceGrpc;
import com.sanedge.ecommerce.pb.product.ProductResponse;
import com.sanedge.ecommerce.pb.product.ProductResponseDeleteAt;
import com.sanedge.ecommerce.pb.product.UpdateProductRequest;
import com.sanedge.ecommerce.product.service.ProductCommandService;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductCommandHandler extends ProductCommandServiceGrpc.ProductCommandServiceImplBase {

    private final ProductCommandService productCommandService;

    @Override
    public void create(CreateProductRequest request, StreamObserver<ApiResponseProduct> observer) {
        try {
            com.sanedge.ecommerce.product.domain.requests.CreateProductRequest req = new com.sanedge.ecommerce.product.domain.requests.CreateProductRequest();
            req.setMerchantId(request.getMerchantId());
            req.setCategoryId(request.getCategoryId());
            req.setName(request.getName());
            req.setDescription(request.getDescription());
            req.setPrice(request.getPrice());
            req.setCountInStock(request.getCountInStock());
            req.setBrand(request.getBrand());
            req.setWeight(request.getWeight());
            req.setRating(request.getRating());
            req.setSlugProduct(request.getSlugProduct());
            req.setImageProduct(request.getImageProduct());

            ApiResponse<com.sanedge.ecommerce.product.domain.responses.ProductResponse> res = productCommandService
                    .createProduct(req);

            observer.onNext(toProduct(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void update(UpdateProductRequest request, StreamObserver<ApiResponseProduct> observer) {
        try {
            com.sanedge.ecommerce.product.domain.requests.UpdateProductRequest req = new com.sanedge.ecommerce.product.domain.requests.UpdateProductRequest();
            req.setProductId(request.getProductId());
            req.setMerchantId(request.getMerchantId());
            req.setCategoryId(request.getCategoryId());
            req.setName(request.getName());
            req.setDescription(request.getDescription());
            req.setPrice(request.getPrice());
            req.setCountInStock(request.getCountInStock());
            req.setBrand(request.getBrand());
            req.setWeight(request.getWeight());
            req.setRating(request.getRating());
            req.setSlugProduct(request.getSlugProduct());
            req.setImageProduct(request.getImageProduct());

            ApiResponse<com.sanedge.ecommerce.product.domain.responses.ProductResponse> res = productCommandService
                    .updateProduct(req);

            observer.onNext(toProduct(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void trashedProduct(FindByIdProductRequest request, StreamObserver<ApiResponseProductDeleteAt> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.product.domain.responses.ProductResponseDeleteAt> res = productCommandService
                    .trashedProduct(request.getId());
            observer.onNext(toDeleteAt(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void restoreProduct(FindByIdProductRequest request, StreamObserver<ApiResponseProductDeleteAt> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.product.domain.responses.ProductResponseDeleteAt> res = productCommandService
                    .restoreProduct(request.getId());
            observer.onNext(toDeleteAt(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void deleteProductPermanent(FindByIdProductRequest request, StreamObserver<ApiResponseProductDelete> observer) {
        try {
            ApiResponse<Boolean> res = productCommandService.deleteProductPermanent(request.getId());
            observer.onNext(ApiResponseProductDelete.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void restoreAllProduct(Empty request, StreamObserver<ApiResponseProductAll> observer) {
        try {
            ApiResponse<Boolean> res = productCommandService.restoreAllProducts();
            observer.onNext(toAll(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void deleteAllProductPermanent(Empty request, StreamObserver<ApiResponseProductAll> observer) {
        try {
            ApiResponse<Boolean> res = productCommandService.deleteAllProductsPermanent();
            observer.onNext(toAll(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    private ApiResponseProduct toProduct(
            ApiResponse<com.sanedge.ecommerce.product.domain.responses.ProductResponse> res) {
        ApiResponseProduct.Builder builder = ApiResponseProduct.newBuilder()
                .setStatus(ProtoMappers.nvl(res.getStatus()))
                .setMessage(ProtoMappers.nvl(res.getMessage()));
        if (res.getData() != null) {
            builder.setData(ProductResponse.newBuilder()
                    .setId(res.getData().getId() == null ? 0 : res.getData().getId().intValue())
                    .setMerchantId(res.getData().getMerchantId() == null ? 0 : res.getData().getMerchantId())
                    .setCategoryId(res.getData().getCategoryId() == null ? 0 : res.getData().getCategoryId())
                    .setName(ProtoMappers.nvl(res.getData().getName()))
                    .setDescription(ProtoMappers.nvl(res.getData().getDescription()))
                    .setPrice(res.getData().getPrice() == null ? 0 : res.getData().getPrice())
                    .setCountInStock(res.getData().getCountInStock() == null ? 0 : res.getData().getCountInStock())
                    .setBrand(ProtoMappers.nvl(res.getData().getBrand()))
                    .setWeight(res.getData().getWeight() == null ? 0 : res.getData().getWeight())
                    .setRating(res.getData().getRating() == null ? 0f : res.getData().getRating())
                    .setSlugProduct(ProtoMappers.nvl(res.getData().getSlugProduct()))
                    .setImageProduct(ProtoMappers.nvl(res.getData().getImageProduct()))
                    .setCreatedAt(ProtoMappers.nvl(res.getData().getCreatedAt()))
                    .setUpdatedAt(ProtoMappers.nvl(res.getData().getUpdatedAt()))
                    .build());
        }
        return builder.build();
    }

    private ApiResponseProductDeleteAt toDeleteAt(
            ApiResponse<com.sanedge.ecommerce.product.domain.responses.ProductResponseDeleteAt> res) {
        ApiResponseProductDeleteAt.Builder builder = ApiResponseProductDeleteAt.newBuilder()
                .setStatus(ProtoMappers.nvl(res.getStatus()))
                .setMessage(ProtoMappers.nvl(res.getMessage()));
        if (res.getData() != null) {
            builder.setData(ProductResponseDeleteAt.newBuilder()
                    .setId(res.getData().getId() == null ? 0 : res.getData().getId().intValue())
                    .setMerchantId(res.getData().getMerchantId() == null ? 0 : res.getData().getMerchantId())
                    .setCategoryId(res.getData().getCategoryId() == null ? 0 : res.getData().getCategoryId())
                    .setName(ProtoMappers.nvl(res.getData().getName()))
                    .setDescription(ProtoMappers.nvl(res.getData().getDescription()))
                    .setPrice(res.getData().getPrice() == null ? 0 : res.getData().getPrice())
                    .setCountInStock(res.getData().getCountInStock() == null ? 0 : res.getData().getCountInStock())
                    .setBrand(ProtoMappers.nvl(res.getData().getBrand()))
                    .setWeight(res.getData().getWeight() == null ? 0 : res.getData().getWeight())
                    .setRating(res.getData().getRating() == null ? 0f : res.getData().getRating())
                    .setSlugProduct(ProtoMappers.nvl(res.getData().getSlugProduct()))
                    .setImageProduct(ProtoMappers.nvl(res.getData().getImageProduct()))
                    .setCreatedAt(ProtoMappers.nvl(res.getData().getCreatedAt()))
                    .setUpdatedAt(ProtoMappers.nvl(res.getData().getUpdatedAt()))
                    .setDeletedAt(StringValue.of(ProtoMappers.nvl(res.getData().getDeletedAt())))
                    .build());
        }
        return builder.build();
    }

    private ApiResponseProductAll toAll(ApiResponse<Boolean> res) {
        return ApiResponseProductAll.newBuilder()
                .setStatus(ProtoMappers.nvl(res.getStatus()))
                .setMessage(ProtoMappers.nvl(res.getMessage()))
                .build();
    }
}
