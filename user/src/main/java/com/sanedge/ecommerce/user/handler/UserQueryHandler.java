package com.sanedge.ecommerce.user.handler;

import java.util.List;

import org.springframework.stereotype.Service;

import com.google.protobuf.StringValue;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.common.grpc.GrpcErrorMapper;
import com.sanedge.ecommerce.common.grpc.ProtoMappers;
import com.sanedge.ecommerce.pb.user.ApiResponsePaginationUser;
import com.sanedge.ecommerce.pb.user.ApiResponsePaginationUserDeleteAt;
import com.sanedge.ecommerce.pb.user.ApiResponseUser;
import com.sanedge.ecommerce.pb.user.ApiResponseUserWithPassword;
import com.sanedge.ecommerce.pb.user.FindAllUserRequest;
import com.sanedge.ecommerce.pb.user.FindByEmailRequest;
import com.sanedge.ecommerce.pb.user.FindByIdUserRequest;
import com.sanedge.ecommerce.pb.user.UserQueryServiceGrpc;
import com.sanedge.ecommerce.pb.user.UserResponse;
import com.sanedge.ecommerce.pb.user.UserResponseDeleteAt;
import com.sanedge.ecommerce.pb.user.UserResponseWithPassword;
import com.sanedge.ecommerce.user.service.UserQueryService;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserQueryHandler extends UserQueryServiceGrpc.UserQueryServiceImplBase {

    private final UserQueryService userQueryService;

    @Override
    public void findAll(FindAllUserRequest request, StreamObserver<ApiResponsePaginationUser> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.user.domain.responses.UserResponse>> res = userQueryService
                    .findAll(toFindAll(request));

            ApiResponsePaginationUser.Builder builder = ApiResponsePaginationUser.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .setPagination(ProtoMappers.toProtoPagination(res.getPagination()));
            res.getData().forEach(u -> builder.addData(toProto(u)));

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findById(FindByIdUserRequest request, StreamObserver<ApiResponseUser> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.user.domain.responses.UserResponse> res = userQueryService
                    .findById(request.getId());

            observer.onNext(ApiResponseUser.newBuilder()
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
    public void findByEmail(FindByEmailRequest request, StreamObserver<ApiResponseUserWithPassword> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.user.domain.responses.UserResponseWithPassword> res = userQueryService
                    .findByEmail(request.getEmail());

            observer.onNext(ApiResponseUserWithPassword.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .setData(toProtoWithPassword(res.getData()))
                    .build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findByActive(FindAllUserRequest request, StreamObserver<ApiResponsePaginationUserDeleteAt> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.user.domain.responses.UserResponseDeleteAt>> res = userQueryService
                    .findByActive(toFindAll(request));

            ApiResponsePaginationUserDeleteAt.Builder builder = ApiResponsePaginationUserDeleteAt.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .setPagination(ProtoMappers.toProtoPagination(res.getPagination()));
            res.getData().forEach(u -> builder.addData(toProtoDeleteAt(u)));

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findByTrashed(FindAllUserRequest request, StreamObserver<ApiResponsePaginationUserDeleteAt> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.user.domain.responses.UserResponseDeleteAt>> res = userQueryService
                    .findByTrashed(toFindAll(request));

            ApiResponsePaginationUserDeleteAt.Builder builder = ApiResponsePaginationUserDeleteAt.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .setPagination(ProtoMappers.toProtoPagination(res.getPagination()));
            res.getData().forEach(u -> builder.addData(toProtoDeleteAt(u)));

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    private com.sanedge.ecommerce.user.domain.requests.FindAllUserRequest toFindAll(FindAllUserRequest request) {
        com.sanedge.ecommerce.user.domain.requests.FindAllUserRequest req = new com.sanedge.ecommerce.user.domain.requests.FindAllUserRequest();
        req.setPage(request.getPage());
        req.setPageSize(request.getPageSize());
        req.setSearch(request.getSearch());
        return req;
    }

    private UserResponse toProto(com.sanedge.ecommerce.user.domain.responses.UserResponse u) {
        return UserResponse.newBuilder()
                .setId(u.getId() == null ? 0 : u.getId())
                .setFirstname(ProtoMappers.nvl(u.getFirstname()))
                .setLastname(ProtoMappers.nvl(u.getLastname()))
                .setEmail(ProtoMappers.nvl(u.getEmail()))
                .setCreatedAt(ProtoMappers.nvl(u.getCreatedAt()))
                .setUpdatedAt(ProtoMappers.nvl(u.getUpdatedAt()))
                .build();
    }

    private UserResponseWithPassword toProtoWithPassword(
            com.sanedge.ecommerce.user.domain.responses.UserResponseWithPassword u) {
        return UserResponseWithPassword.newBuilder()
                .setId(u.getId() == null ? 0 : u.getId())
                .setFirstname(ProtoMappers.nvl(u.getFirstname()))
                .setLastname(ProtoMappers.nvl(u.getLastname()))
                .setEmail(ProtoMappers.nvl(u.getEmail()))
                .setPassword(ProtoMappers.nvl(u.getPassword()))
                .setCreatedAt(ProtoMappers.nvl(u.getCreatedAt()))
                .setUpdatedAt(ProtoMappers.nvl(u.getUpdatedAt()))
                .build();
    }

    private UserResponseDeleteAt toProtoDeleteAt(
            com.sanedge.ecommerce.user.domain.responses.UserResponseDeleteAt u) {
        return UserResponseDeleteAt.newBuilder()
                .setId(u.getId() == null ? 0 : u.getId())
                .setFirstname(ProtoMappers.nvl(u.getFirstname()))
                .setLastname(ProtoMappers.nvl(u.getLastname()))
                .setEmail(ProtoMappers.nvl(u.getEmail()))
                .setCreatedAt(ProtoMappers.nvl(u.getCreatedAt()))
                .setUpdatedAt(ProtoMappers.nvl(u.getUpdatedAt()))
                .setDeletedAt(StringValue.of(ProtoMappers.nvl(u.getDeletedAt())))
                .build();
    }
}
