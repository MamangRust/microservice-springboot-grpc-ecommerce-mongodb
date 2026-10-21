package com.sanedge.ecommerce.common.adapter.user_role;

import java.util.ArrayList;
import java.util.List;

import com.sanedge.ecommerce.common.adapter.model.Role;
import com.sanedge.ecommerce.common.adapter.model.UserRole;
import com.sanedge.ecommerce.common.adapter.role.RoleAdapter;
import com.sanedge.ecommerce.common.adapter.support.AdapterException;
import com.sanedge.ecommerce.pb.role.AssignRoleToUserRequest;
import com.sanedge.ecommerce.pb.role.FindByIdUserRoleRequest;
import com.sanedge.ecommerce.pb.role.RemoveRoleFromUserRequest;
import com.sanedge.ecommerce.pb.role.RoleCommandServiceGrpc.RoleCommandServiceBlockingStub;
import com.sanedge.ecommerce.pb.role.RoleQueryServiceGrpc.RoleQueryServiceBlockingStub;
import com.sanedge.ecommerce.pb.role.UserRoleResponse;

public class UserRoleAdapter implements UserRolePort {

    private final RoleQueryServiceBlockingStub query;

    private final RoleCommandServiceBlockingStub command;

    public UserRoleAdapter(RoleQueryServiceBlockingStub query, RoleCommandServiceBlockingStub command) {
        this.query = query;
        this.command = command;
    }

    @Override
    public List<Role> findByUserId(int userId) {
        var resp = query.findByUserId(FindByIdUserRoleRequest.newBuilder().setUserId(userId).build());
        List<Role> roles = new ArrayList<>(resp.getDataList().size());
        for (var role : resp.getDataList()) {
            roles.add(RoleAdapter.toModel(role));
        }
        return roles;
    }

    @Override
    public UserRole assignRoleToUser(int userId, int roleId) {
        var resp = command.assignRoleToUser(AssignRoleToUserRequest.newBuilder()
                .setUserId(userId)
                .setRoleId(roleId)
                .build());
        if (!resp.hasData()) {
            throw new AdapterException("Failed to assign role " + roleId + " to user " + userId);
        }
        return toModel(resp.getData());
    }

    @Override
    public void removeRoleFromUser(int userId, int roleId) {
        command.removeRoleFromUser(RemoveRoleFromUserRequest.newBuilder()
                .setUserId(userId)
                .setRoleId(roleId)
                .build());
    }

    static UserRole toModel(UserRoleResponse ur) {
        if (ur == null) {
            return null;
        }
        return new UserRole(
                ur.getUserRoleId(),
                ur.getUserId(),
                ur.getRoleId(),
                com.sanedge.ecommerce.common.adapter.support.ProtoTime.parse(ur.getCreatedAt()),
                com.sanedge.ecommerce.common.adapter.support.ProtoTime.parse(ur.getUpdatedAt()));
    }
}
