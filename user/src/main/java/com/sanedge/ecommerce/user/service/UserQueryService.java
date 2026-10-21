package com.sanedge.ecommerce.user.service;

import java.util.List;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.user.domain.requests.FindAllUserRequest;
import com.sanedge.ecommerce.user.domain.responses.UserResponse;
import com.sanedge.ecommerce.user.domain.responses.UserResponseDeleteAt;
import com.sanedge.ecommerce.user.domain.responses.UserResponseWithPassword;

public interface UserQueryService {

    ApiResponsePagination<List<UserResponse>> findAll(FindAllUserRequest req);

    ApiResponse<UserResponse> findById(Integer userId);

    ApiResponse<UserResponseWithPassword> findByEmail(String email);

    ApiResponsePagination<List<UserResponseDeleteAt>> findByActive(FindAllUserRequest req);

    ApiResponsePagination<List<UserResponseDeleteAt>> findByTrashed(FindAllUserRequest req);
}
