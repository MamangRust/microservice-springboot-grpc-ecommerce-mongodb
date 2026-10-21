package com.sanedge.ecommerce.user.handler;

import org.springframework.stereotype.Service;

import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.grpc.GrpcErrorMapper;
import com.sanedge.ecommerce.common.grpc.ProtoMappers;
import com.sanedge.ecommerce.pb.user.ApiResponseUser;
import com.sanedge.ecommerce.pb.user.ApiResponseUserAll;
import com.sanedge.ecommerce.pb.user.ApiResponseUserDelete;
import com.sanedge.ecommerce.pb.user.ApiResponseUserDeleteAt;
import com.sanedge.ecommerce.pb.user.CreateUserRequest;
import com.sanedge.ecommerce.pb.user.FindByIdUserRequest;
import com.sanedge.ecommerce.pb.user.UpdateUserPasswordRequest;
import com.sanedge.ecommerce.pb.user.UpdateUserRequest;
import com.sanedge.ecommerce.pb.user.UserCommandServiceGrpc;
import com.sanedge.ecommerce.pb.user.UserResponse;
import com.sanedge.ecommerce.pb.user.UserResponseDeleteAt;
import com.sanedge.ecommerce.pb.user.VerifyPasswordRequest;
import com.sanedge.ecommerce.pb.user.VerifyPasswordResponse;
import com.sanedge.ecommerce.user.service.UserCommandService;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserCommandHandler extends UserCommandServiceGrpc.UserCommandServiceImplBase {

    private final UserCommandService userCommandService;

    @Override
    public void create(CreateUserRequest request, StreamObserver<ApiResponseUser> observer) {
        try {
            com.sanedge.ecommerce.user.domain.requests.CreateUserRequest req = new com.sanedge.ecommerce.user.domain.requests.CreateUserRequest();
            req.setFirstname(request.getFirstname());
            req.setLastname(request.getLastname());
            req.setEmail(request.getEmail());
            req.setPassword(request.getPassword());
            req.setConfirmPassword(request.getConfirmPassword());

            ApiResponse<com.sanedge.ecommerce.user.domain.responses.UserResponse> res = userCommandService
                    .create(req);

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
    public void update(UpdateUserRequest request, StreamObserver<ApiResponseUser> observer) {
        try {
            com.sanedge.ecommerce.user.domain.requests.UpdateUserRequest req = new com.sanedge.ecommerce.user.domain.requests.UpdateUserRequest();
            req.setId(request.getId());
            req.setFirstname(request.getFirstname());
            req.setLastname(request.getLastname());
            req.setEmail(request.getEmail());
            req.setPassword(request.getPassword());
            req.setConfirmPassword(request.getConfirmPassword());

            ApiResponse<com.sanedge.ecommerce.user.domain.responses.UserResponse> res = userCommandService
                    .update(req);

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
    public void updatePassword(UpdateUserPasswordRequest request, StreamObserver<ApiResponseUser> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.user.domain.responses.UserResponse> res = userCommandService
                    .updatePassword(request.getId(), request.getPassword());

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
    public void trashedUser(FindByIdUserRequest request, StreamObserver<ApiResponseUserDeleteAt> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.user.domain.responses.UserResponseDeleteAt> res = userCommandService
                    .trashed(request.getId());
            observer.onNext(toDeleteAt(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void restoreUser(FindByIdUserRequest request, StreamObserver<ApiResponseUserDeleteAt> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.user.domain.responses.UserResponseDeleteAt> res = userCommandService
                    .restore(request.getId());
            observer.onNext(toDeleteAt(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void deleteUserPermanent(FindByIdUserRequest request, StreamObserver<ApiResponseUserDelete> observer) {
        try {
            ApiResponse<Boolean> res = userCommandService.deletePermanent(request.getId());
            observer.onNext(ApiResponseUserDelete.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void restoreAllUser(Empty request, StreamObserver<ApiResponseUserAll> observer) {
        try {
            ApiResponse<Boolean> res = userCommandService.restoreAll();
            observer.onNext(toAll(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void deleteAllUserPermanent(Empty request, StreamObserver<ApiResponseUserAll> observer) {
        try {
            ApiResponse<Boolean> res = userCommandService.deleteAll();
            observer.onNext(toAll(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void verifyPassword(VerifyPasswordRequest request, StreamObserver<VerifyPasswordResponse> observer) {
        try {
            com.sanedge.ecommerce.user.domain.responses.VerifyPasswordResponse res = userCommandService
                    .verifyPassword(request.getEmail(), request.getPassword());

            VerifyPasswordResponse.Builder builder = VerifyPasswordResponse.newBuilder()
                    .setValid(res.isValid());
            if (res.getUser() != null) {
                builder.setUser(toProto(res.getUser()));
            }

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
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

    private ApiResponseUserDeleteAt toDeleteAt(
            ApiResponse<com.sanedge.ecommerce.user.domain.responses.UserResponseDeleteAt> res) {
        UserResponseDeleteAt data = UserResponseDeleteAt.newBuilder()
                .setId(res.getData().getId() == null ? 0 : res.getData().getId())
                .setFirstname(ProtoMappers.nvl(res.getData().getFirstname()))
                .setLastname(ProtoMappers.nvl(res.getData().getLastname()))
                .setEmail(ProtoMappers.nvl(res.getData().getEmail()))
                .setCreatedAt(ProtoMappers.nvl(res.getData().getCreatedAt()))
                .setUpdatedAt(ProtoMappers.nvl(res.getData().getUpdatedAt()))
                .setDeletedAt(StringValue.of(ProtoMappers.nvl(res.getData().getDeletedAt())))
                .build();
        return ApiResponseUserDeleteAt.newBuilder()
                .setStatus(ProtoMappers.nvl(res.getStatus()))
                .setMessage(ProtoMappers.nvl(res.getMessage()))
                .setData(data)
                .build();
    }

    private ApiResponseUserAll toAll(ApiResponse<Boolean> res) {
        return ApiResponseUserAll.newBuilder()
                .setStatus(ProtoMappers.nvl(res.getStatus()))
                .setMessage(ProtoMappers.nvl(res.getMessage()))
                .build();
    }
}
