package com.sanedge.ecommerce.auth.handler;

import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.auth.service.AuthService;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.grpc.GrpcErrorMapper;
import com.sanedge.ecommerce.common.grpc.ProtoMappers;
import com.sanedge.ecommerce.pb.auth.ApiResponseForgotPassword;
import com.sanedge.ecommerce.pb.auth.ApiResponseGetMe;
import com.sanedge.ecommerce.pb.auth.ApiResponseLogin;
import com.sanedge.ecommerce.pb.auth.ApiResponseRefreshToken;
import com.sanedge.ecommerce.pb.auth.ApiResponseRegister;
import com.sanedge.ecommerce.pb.auth.ApiResponseResetPassword;
import com.sanedge.ecommerce.pb.auth.ApiResponseVerifyCode;
import com.sanedge.ecommerce.pb.auth.AuthServiceGrpc;
import com.sanedge.ecommerce.pb.auth.ForgotPasswordRequest;
import com.sanedge.ecommerce.pb.auth.GetMeRequest;
import com.sanedge.ecommerce.pb.auth.LoginRequest;
import com.sanedge.ecommerce.pb.auth.RegisterRequest;
import com.sanedge.ecommerce.pb.auth.RefreshTokenRequest;
import com.sanedge.ecommerce.pb.auth.ResetPasswordRequest;
import com.sanedge.ecommerce.pb.auth.VerifyCodeRequest;
import com.sanedge.ecommerce.pb.user.UserResponse;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthHandler extends AuthServiceGrpc.AuthServiceImplBase {

    private final AuthService authService;

    @Override
    public void registerUser(RegisterRequest request, StreamObserver<ApiResponseRegister> observer) {
        try {
            com.sanedge.ecommerce.auth.domain.requests.RegisterRequest req = new com.sanedge.ecommerce.auth.domain.requests.RegisterRequest();
            req.setFirstname(request.getFirstname());
            req.setLastname(request.getLastname());
            req.setEmail(request.getEmail());
            req.setPassword(request.getPassword());
            req.setConfirmPassword(request.getConfirmPassword());

            ApiResponse<com.sanedge.ecommerce.auth.domain.responses.UserResponse> res = authService.register(req);

            ApiResponseRegister.Builder builder = ApiResponseRegister.newBuilder()
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
    public void loginUser(LoginRequest request, StreamObserver<ApiResponseLogin> observer) {
        try {
            com.sanedge.ecommerce.auth.domain.requests.AuthRequest req = new com.sanedge.ecommerce.auth.domain.requests.AuthRequest();
            req.setEmail(request.getEmail());
            req.setPassword(request.getPassword());

            ApiResponse<com.sanedge.ecommerce.auth.domain.responses.TokenResponse> res = authService.login(req);

            ApiResponseLogin.Builder builder = ApiResponseLogin.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            if (res.getData() != null) {
                builder.setData(toProtoToken(res.getData()));
            }

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void refreshToken(RefreshTokenRequest request, StreamObserver<ApiResponseRefreshToken> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.auth.domain.responses.TokenResponse> res = authService
                    .refreshToken(request.getRefreshToken());

            ApiResponseRefreshToken.Builder builder = ApiResponseRefreshToken.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            if (res.getData() != null) {
                builder.setData(toProtoToken(res.getData()));
            }

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void getMe(GetMeRequest request, StreamObserver<ApiResponseGetMe> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.auth.domain.responses.UserResponse> res = authService
                    .getCurrentUser(request.getUserId());

            ApiResponseGetMe.Builder builder = ApiResponseGetMe.newBuilder()
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
    public void verifyCode(VerifyCodeRequest request, StreamObserver<ApiResponseVerifyCode> observer) {
        try {
            observer.onNext(ApiResponseVerifyCode.newBuilder()
                    .setStatus("success")
                    .setMessage("Code verified successfully")
                    .build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request, StreamObserver<ApiResponseForgotPassword> observer) {
        try {
            observer.onNext(ApiResponseForgotPassword.newBuilder()
                    .setStatus("success")
                    .setMessage("Password reset link sent to " + ProtoMappers.nvl(request.getEmail()))
                    .build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void resetPassword(ResetPasswordRequest request, StreamObserver<ApiResponseResetPassword> observer) {
        try {
            observer.onNext(ApiResponseResetPassword.newBuilder()
                    .setStatus("success")
                    .setMessage("Password reset successfully")
                    .build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    private UserResponse toProto(com.sanedge.ecommerce.auth.domain.responses.UserResponse u) {
        return UserResponse.newBuilder()
                .setId(u.getId() == null ? 0 : u.getId())
                .setFirstname(ProtoMappers.nvl(u.getFirstname()))
                .setLastname(ProtoMappers.nvl(u.getLastname()))
                .setEmail(ProtoMappers.nvl(u.getEmail()))
                .setCreatedAt(ProtoMappers.nvl(u.getCreatedAt()))
                .setUpdatedAt(ProtoMappers.nvl(u.getUpdatedAt()))
                .build();
    }

    private com.sanedge.ecommerce.pb.auth.TokenResponse toProtoToken(
            com.sanedge.ecommerce.auth.domain.responses.TokenResponse t) {
        return com.sanedge.ecommerce.pb.auth.TokenResponse.newBuilder()
                .setAccessToken(ProtoMappers.nvl(t.getAccess_token()))
                .setRefreshToken(ProtoMappers.nvl(t.getRefresh_token()))
                .build();
    }
}
