package com.sanedge.ecommerce.common.adapter.user_role;

import java.util.List;

import com.sanedge.ecommerce.common.adapter.model.Role;
import com.sanedge.ecommerce.common.adapter.model.UserRole;

/**
 * Port user-role: resolve role milik user + (un)assign. Setara
 * {@code user_role.QueryRepository}/{@code CommandRepository} di Go. Di proto
 * Quarkus, keduanya dilayani oleh RoleQueryService/RoleCommandService.
 */
public interface UserRolePort {

    List<Role> findByUserId(int userId);

    UserRole assignRoleToUser(int userId, int roleId);

    void removeRoleFromUser(int userId, int roleId);
}
