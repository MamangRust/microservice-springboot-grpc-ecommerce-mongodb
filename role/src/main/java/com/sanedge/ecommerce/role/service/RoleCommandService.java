package com.sanedge.ecommerce.role.service;

import com.sanedge.ecommerce.role.domain.requests.CreateRoleRequest;
import com.sanedge.ecommerce.role.domain.requests.UpdateRoleRequest;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.role.domain.responses.RoleResponse;
import com.sanedge.ecommerce.role.domain.responses.RoleResponseDeleteAt;

public interface RoleCommandService {
    ApiResponse<RoleResponse> create(CreateRoleRequest request);

    ApiResponse<RoleResponse> update(UpdateRoleRequest request);

    ApiResponse<RoleResponseDeleteAt> trash(Integer id);

    ApiResponse<RoleResponseDeleteAt> restore(Integer id);

    ApiResponse<Boolean> delete(Integer id);

    ApiResponse<Boolean> restoreAll();

    ApiResponse<Boolean> deleteAll();
}
