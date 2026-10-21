package com.sanedge.ecommerce.role.repository;

import java.util.List;

import com.sanedge.ecommerce.role.models.Role;

public interface RoleQueryRepositoryCustom {
    List<Role> findUserRoles(Long userId);
}
