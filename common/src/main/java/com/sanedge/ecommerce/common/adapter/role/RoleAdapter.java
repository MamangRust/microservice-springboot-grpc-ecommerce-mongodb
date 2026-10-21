package com.sanedge.ecommerce.common.adapter.role;

import com.sanedge.ecommerce.common.adapter.model.Role;
import com.sanedge.ecommerce.common.adapter.support.ProtoTime;
import com.sanedge.ecommerce.common.exception.ResourceNotFoundException;
import com.sanedge.ecommerce.pb.role.FindByIdRoleRequest;
import com.sanedge.ecommerce.pb.role.FindByNameRoleRequest;
import com.sanedge.ecommerce.pb.role.RoleQueryServiceGrpc.RoleQueryServiceBlockingStub;
import com.sanedge.ecommerce.pb.role.RoleResponse;

public class RoleAdapter implements RolePort {

    private final RoleQueryServiceBlockingStub client;

    public RoleAdapter(RoleQueryServiceBlockingStub client) {
        this.client = client;
    }

    @Override
    public Role findById(int roleId) {
        var resp = client.findByIdRole(FindByIdRoleRequest.newBuilder().setRoleId(roleId).build());
        if (!resp.hasData()) {
            throw new ResourceNotFoundException("Role not found: " + roleId);
        }
        return toModel(resp.getData());
    }

    @Override
    public Role findByName(String name) {
        var resp = client.findByNameRole(FindByNameRoleRequest.newBuilder().setName(name).build());
        if (!resp.hasData()) {
            throw new ResourceNotFoundException("Role not found: " + name);
        }
        return toModel(resp.getData());
    }

    public static Role toModel(RoleResponse r) {
        if (r == null) {
            return null;
        }
        return new Role(
                r.getId(),
                r.getName(),
                ProtoTime.parse(r.getCreatedAt()),
                ProtoTime.parse(r.getUpdatedAt()));
    }
}
