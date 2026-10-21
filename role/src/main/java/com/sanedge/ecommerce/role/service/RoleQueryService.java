package com.sanedge.ecommerce.role.service;

import java.util.List;

import com.sanedge.ecommerce.role.domain.requests.FindAllRoles;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.role.domain.responses.RoleResponse;
import com.sanedge.ecommerce.role.domain.responses.RoleResponseDeleteAt;

public interface RoleQueryService {
    ApiResponsePagination<List<RoleResponse>> findAll(FindAllRoles req);

    ApiResponsePagination<List<RoleResponseDeleteAt>> findByActive(FindAllRoles req);

    ApiResponsePagination<List<RoleResponseDeleteAt>> findByTrashed(FindAllRoles req);

    ApiResponse<RoleResponse> findById(Integer id);

    ApiResponse<List<RoleResponse>> findByUserId(Integer user_id);

    ApiResponse<RoleResponse> findByName(String name);
}
