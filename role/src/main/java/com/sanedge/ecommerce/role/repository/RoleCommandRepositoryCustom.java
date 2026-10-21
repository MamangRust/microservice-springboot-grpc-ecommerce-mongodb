package com.sanedge.ecommerce.role.repository;

import com.sanedge.ecommerce.role.models.Role;

public interface RoleCommandRepositoryCustom {
    Role trashed(String roleId);

    Role restore(String roleId);

    Role deletePermanent(String roleId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}
