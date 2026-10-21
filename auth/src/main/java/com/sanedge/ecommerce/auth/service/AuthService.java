package com.sanedge.ecommerce.auth.service;

import com.sanedge.ecommerce.auth.domain.requests.AuthRequest;
import com.sanedge.ecommerce.auth.domain.requests.RegisterRequest;
import com.sanedge.ecommerce.auth.domain.responses.TokenResponse;
import com.sanedge.ecommerce.auth.domain.responses.UserResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;

public interface AuthService {
    ApiResponse<TokenResponse> login(AuthRequest loginRequest);

    ApiResponse<UserResponse> register(RegisterRequest registerRequest);

    ApiResponse<TokenResponse> refreshToken(String refreshToken);

    ApiResponse<UserResponse> getCurrentUser(Integer userId);
}
