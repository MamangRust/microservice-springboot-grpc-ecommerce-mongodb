package com.sanedge.ecommerce.user.service;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.user.domain.requests.CreateUserRequest;
import com.sanedge.ecommerce.user.domain.requests.UpdateUserRequest;
import com.sanedge.ecommerce.user.domain.responses.UserResponse;
import com.sanedge.ecommerce.user.domain.responses.UserResponseDeleteAt;
import com.sanedge.ecommerce.user.domain.responses.VerifyPasswordResponse;

public interface UserCommandService {

    ApiResponse<UserResponse> create(CreateUserRequest req);

    ApiResponse<UserResponse> update(UpdateUserRequest req);

    ApiResponse<UserResponse> updatePassword(Integer userId, String password);

    ApiResponse<UserResponseDeleteAt> trashed(Integer userId);

    ApiResponse<UserResponseDeleteAt> restore(Integer userId);

    ApiResponse<Boolean> deletePermanent(Integer userId);

    ApiResponse<Boolean> restoreAll();

    ApiResponse<Boolean> deleteAll();

    VerifyPasswordResponse verifyPassword(String email, String password);
}
