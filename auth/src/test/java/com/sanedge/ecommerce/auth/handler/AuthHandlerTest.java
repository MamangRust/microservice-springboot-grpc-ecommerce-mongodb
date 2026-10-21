package com.sanedge.ecommerce.auth.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sanedge.ecommerce.auth.domain.responses.TokenResponse;
import com.sanedge.ecommerce.auth.domain.responses.UserResponse;
import com.sanedge.ecommerce.auth.service.AuthService;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.exception.ResourceNotFoundException;
import com.sanedge.ecommerce.pb.auth.GetMeRequest;
import com.sanedge.ecommerce.pb.auth.LoginRequest;
import com.sanedge.ecommerce.pb.auth.RegisterRequest;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;

@ExtendWith(MockitoExtension.class)
class AuthHandlerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthHandler handler;

    private static UserResponse user() {
        return UserResponse.builder()
                .id(1).firstname("Alice").lastname("Doe")
                .email("alice@example.com")
                .createdAt("2024-01-01T00:00:00").updatedAt("2024-01-02T00:00:00").build();
    }

    private static TokenResponse token() {
        return TokenResponse.builder()
                .access_token("access-token").refresh_token("refresh-token").build();
    }

    @Test
    void registerUser_mapsData() {
        when(authService.register(any())).thenReturn(ApiResponse.<UserResponse>builder()
                .status("success").message("registered").data(user()).build());

        TestStreamObserver<com.sanedge.ecommerce.pb.auth.ApiResponseRegister> observer = new TestStreamObserver<>();
        handler.registerUser(RegisterRequest.newBuilder()
                .setFirstname("Alice").setLastname("Doe")
                .setEmail("alice@example.com").setPassword("secret1").setConfirmPassword("secret1").build(), observer);

        assertThat(observer.error()).isNull();
        assertThat(observer.isCompleted()).isTrue();
        assertThat(observer.value().getStatus()).isEqualTo("success");
        assertThat(observer.value().getMessage()).isEqualTo("registered");
        assertThat(observer.value().getData().getEmail()).isEqualTo("alice@example.com");
    }

    @Test
    void loginUser_mapsToken() {
        when(authService.login(any())).thenReturn(ApiResponse.<TokenResponse>builder()
                .status("success").message("logged in").data(token()).build());

        TestStreamObserver<com.sanedge.ecommerce.pb.auth.ApiResponseLogin> observer = new TestStreamObserver<>();
        handler.loginUser(LoginRequest.newBuilder().setEmail("alice@example.com").setPassword("secret1").build(),
                observer);

        assertThat(observer.error()).isNull();
        assertThat(observer.value().getStatus()).isEqualTo("success");
        assertThat(observer.value().getData().getAccessToken()).isEqualTo("access-token");
        assertThat(observer.value().getData().getRefreshToken()).isEqualTo("refresh-token");
    }

    @Test
    void refreshToken_mapsToken() {
        when(authService.refreshToken("refresh-token")).thenReturn(ApiResponse.<TokenResponse>builder()
                .status("success").message("refreshed").data(token()).build());

        TestStreamObserver<com.sanedge.ecommerce.pb.auth.ApiResponseRefreshToken> observer = new TestStreamObserver<>();
        handler.refreshToken(com.sanedge.ecommerce.pb.auth.RefreshTokenRequest.newBuilder()
                .setRefreshToken("refresh-token").build(), observer);

        assertThat(observer.error()).isNull();
        assertThat(observer.value().getData().getRefreshToken()).isEqualTo("refresh-token");
    }

    @Test
    void getMe_mapsUser() {
        when(authService.getCurrentUser(1)).thenReturn(ApiResponse.<UserResponse>builder()
                .status("success").message("ok").data(user()).build());

        TestStreamObserver<com.sanedge.ecommerce.pb.auth.ApiResponseGetMe> observer = new TestStreamObserver<>();
        handler.getMe(GetMeRequest.newBuilder().setUserId(1).build(), observer);

        assertThat(observer.error()).isNull();
        assertThat(observer.value().getData().getId()).isEqualTo(1);
        assertThat(observer.value().getData().getEmail()).isEqualTo("alice@example.com");
    }

    @Test
    void registerUser_whenServiceThrows_propagatesNotFound() {
        when(authService.register(any())).thenThrow(new ResourceNotFoundException("role missing"));

        TestStreamObserver<com.sanedge.ecommerce.pb.auth.ApiResponseRegister> observer = new TestStreamObserver<>();
        handler.registerUser(RegisterRequest.newBuilder().setEmail("alice@example.com").build(), observer);

        assertThat(observer.value()).isNull();
        assertThat(observer.error()).isInstanceOf(StatusRuntimeException.class);
        StatusRuntimeException ex = (StatusRuntimeException) observer.error();
        assertThat(ex.getStatus().getCode()).isEqualTo(Status.Code.NOT_FOUND);
        assertThat(ex.getStatus().getDescription()).isEqualTo("role missing");
    }
}
