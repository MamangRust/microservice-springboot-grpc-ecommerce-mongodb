package com.sanedge.ecommerce.common.adapter.role;

import com.sanedge.ecommerce.common.adapter.model.Role;

/**
 * Port baca role. Setara {@code role.QueryRepository} di Go. Penugasan role
 * ada di {@link com.sanedge.ecommerce.common.adapter.user_role.UserRolePort}.
 */
public interface RolePort {

    /** @throws com.sanedge.ecommerce.common.exception.ResourceNotFoundException bila tidak ada. */
    Role findById(int roleId);

    /** @throws com.sanedge.ecommerce.common.exception.ResourceNotFoundException bila tidak ada. */
    Role findByName(String name);
}
