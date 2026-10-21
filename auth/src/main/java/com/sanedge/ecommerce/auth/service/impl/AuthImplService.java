package com.sanedge.ecommerce.auth.service.impl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.auth.domain.requests.AuthRequest;
import com.sanedge.ecommerce.auth.domain.requests.RegisterRequest;
import com.sanedge.ecommerce.auth.domain.responses.TokenResponse;
import com.sanedge.ecommerce.auth.domain.responses.UserResponse;
import com.sanedge.ecommerce.auth.models.RefreshToken;
import com.sanedge.ecommerce.auth.repository.RefreshTokenCommandRepository;
import com.sanedge.ecommerce.auth.repository.RefreshTokenQueryRepository;
import com.sanedge.ecommerce.auth.service.AuthService;
import com.sanedge.ecommerce.common.adapter.model.User;
import com.sanedge.ecommerce.common.adapter.model.VerifyPasswordResult;
import com.sanedge.ecommerce.common.adapter.role.RolePort;
import com.sanedge.ecommerce.common.adapter.support.AdapterException;
import com.sanedge.ecommerce.common.adapter.user.UserPort;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.exception.ResourceNotFoundException;
import com.sanedge.ecommerce.common.observability.TracingMetrics;
import com.sanedge.ecommerce.common.security.JwtProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Auth domain logic. Password verification and user/role lookups are delegated
 * to the {@code user} and {@code role} services through their ports — this
 * module owns no {@code AuthenticationManager}/{@code UserDetailsService} and
 * stores only the refresh tokens locally.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class AuthImplService implements AuthService {

    private static final String DEFAULT_ROLE = "ROLE_ADMIN";

    private final UserPort userPort;
    private final RolePort rolePort;
    private final JwtProvider jwtProvider;
    private final RefreshTokenQueryRepository refreshTokenQueryRepository;
    private final RefreshTokenCommandRepository refreshTokenCommandRepository;
    private final TracingMetrics tracingMetrics;

    @Override
    public ApiResponse<TokenResponse> login(AuthRequest loginRequest) {
        return tracingMetrics.traceAndMeasure("loginUser", "login", () -> {
            VerifyPasswordResult verification = userPort.verifyPassword(
                    loginRequest.getEmail(), loginRequest.getPassword());

            if (!verification.valid() || verification.user() == null) {
                throw new IllegalArgumentException("Invalid email or password");
            }

            User user = verification.user();
            String email = user.email();
            Long userId = (long) user.id();

            String accessToken = jwtProvider.generateAccessToken(email);
            String refreshTokenStr = jwtProvider.generateRefreshToken(email);

            RefreshToken refreshTokenEntity = refreshTokenQueryRepository.findByUserId(userId)
                    .orElseGet(RefreshToken::new);
            refreshTokenEntity.setUserId(userId);
            refreshTokenEntity.setToken(refreshTokenStr);
            refreshTokenEntity.setExpiryDate(
                    LocalDateTime.now().plus(jwtProvider.getJwtRefreshExpirationMs(), ChronoUnit.MILLIS));
            refreshTokenEntity = refreshTokenCommandRepository.save(refreshTokenEntity);

            TokenResponse tokenResponse = TokenResponse.builder()
                    .access_token(accessToken)
                    .refresh_token(refreshTokenEntity.getToken())
                    .build();

            return ApiResponse.<TokenResponse>builder()
                    .status("success")
                    .message("Login successful")
                    .data(tokenResponse)
                    .build();
        });
    }

    @Override
    public ApiResponse<UserResponse> register(RegisterRequest registerRequest) {
        return tracingMetrics.traceAndMeasure("registerUser", "register", () -> {
            if (emailAlreadyRegistered(registerRequest.getEmail())) {
                log.error("❌ [REGISTER] Email already taken | Email: {}", registerRequest.getEmail());
                throw new IllegalArgumentException("Email already registered");
            }

            // Memastikan role default ada; melempar ResourceNotFoundException bila tidak.
            rolePort.findByName(DEFAULT_ROLE);

            User created;
            try {
                created = userPort.createUser(new UserPort.RegisterData(
                        registerRequest.getFirstname(),
                        registerRequest.getLastname(),
                        registerRequest.getEmail(),
                        registerRequest.getPassword(),
                        registerRequest.getConfirmPassword()));
            } catch (AdapterException e) {
                throw new IllegalArgumentException(e.getMessage());
            }

            return ApiResponse.<UserResponse>builder()
                    .status("success")
                    .message("User registered successfully")
                    .data(UserResponse.from(created))
                    .build();
        });
    }

    @Override
    public ApiResponse<TokenResponse> refreshToken(String refreshToken) {
        return tracingMetrics.traceAndMeasure("refreshToken", "refresh", () -> {
            RefreshToken storedToken = refreshTokenQueryRepository.findByToken(refreshToken)
                    .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));

            if (storedToken.getExpiryDate() == null || storedToken.getExpiryDate().isBefore(LocalDateTime.now())) {
                log.warn("❌ [REFRESH TOKEN] Token expired | Token: {}", refreshToken);
                throw new IllegalArgumentException("Refresh token expired");
            }

            User user = userPort.findById(storedToken.getUserId().intValue());

            String newAccessToken = jwtProvider.generateAccessToken(user.email());

            LocalDateTime newExpiration = LocalDateTime.now().plus(30, ChronoUnit.DAYS);
            storedToken.setExpiryDate(newExpiration);
            refreshTokenCommandRepository.save(storedToken);

            TokenResponse tokenResponse = TokenResponse.builder()
                    .access_token(newAccessToken)
                    .refresh_token(refreshToken)
                    .build();

            return ApiResponse.<TokenResponse>builder()
                    .status("success")
                    .message("Access token refreshed successfully")
                    .data(tokenResponse)
                    .build();
        });
    }

    @Override
    public ApiResponse<UserResponse> getCurrentUser(Integer userId) {
        return tracingMetrics.traceAndMeasure("getMe", "get_me", () -> {
            User user = userPort.findById(userId);

            return ApiResponse.<UserResponse>builder()
                    .status("success")
                    .message("Current user fetched successfully")
                    .data(UserResponse.from(user))
                    .build();
        });
    }

    private boolean emailAlreadyRegistered(String email) {
        try {
            userPort.findByEmail(email);
            return true;
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }
}
