package com.sanedge.ecommerce.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.gateway.dto.requests.auth.AuthRequest;
import com.sanedge.ecommerce.gateway.dto.requests.auth.RegisterRequest;
import com.sanedge.ecommerce.gateway.dto.requests.refresh_token.RefreshTokenRequest;
import com.sanedge.ecommerce.gateway.dto.responses.auth.TokenResponse;
import com.sanedge.ecommerce.gateway.dto.responses.user.UserResponse;
import com.sanedge.ecommerce.gateway.mapper.ProtoMapper;
import com.sanedge.ecommerce.gateway.security.UserDetailsImpl;
import com.sanedge.ecommerce.pb.auth.AuthServiceGrpc;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthServiceGrpc.AuthServiceBlockingStub authStub;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@RequestBody RegisterRequest req) {
        var resp = authStub.registerUser(com.sanedge.ecommerce.pb.auth.RegisterRequest.newBuilder()
                .setFirstname(nvl(req.getFirstname()))
                .setLastname(nvl(req.getLastname()))
                .setEmail(nvl(req.getEmail()))
                .setPassword(nvl(req.getPassword()))
                .setConfirmPassword(nvl(req.getConfirmPassword()))
                .build());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<UserResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.user(resp.getData()))
                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@RequestBody AuthRequest req) {
        var resp = authStub.loginUser(com.sanedge.ecommerce.pb.auth.LoginRequest.newBuilder()
                .setEmail(nvl(req.getUsername()))
                .setPassword(nvl(req.getPassword()))
                .build());
        return ResponseEntity.ok(ApiResponse.<TokenResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.token(resp.getData()))
                .build());
    }

    @GetMapping("/healthchecker")
    public ResponseEntity<String> healthChecker() {
        return ResponseEntity.ok("✅ API is up and running!");
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMe() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        int userId = 0;
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl details) {
            userId = details.getId() == null ? 0 : details.getId().intValue();
        }
        var resp = authStub.getMe(com.sanedge.ecommerce.pb.auth.GetMeRequest.newBuilder()
                .setUserId(userId)
                .build());
        return ResponseEntity.ok(ApiResponse.<UserResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.user(resp.getData()))
                .build());
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<TokenResponse>> refreshToken(@RequestBody RefreshTokenRequest req) {
        var resp = authStub.refreshToken(com.sanedge.ecommerce.pb.auth.RefreshTokenRequest.newBuilder()
                .setRefreshToken(nvl(req.getRefreshToken()))
                .build());
        return ResponseEntity.ok(ApiResponse.<TokenResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.token(resp.getData()))
                .build());
    }

    private static String nvl(String value) {
        return value == null ? "" : value;
    }
}
