package com.sanedge.ecommerce.gateway.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.protobuf.Empty;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.common.grpc.ProtoMappers;
import com.sanedge.ecommerce.gateway.dto.requests.role.CreateRoleRequest;
import com.sanedge.ecommerce.gateway.dto.requests.role.FindAllRoles;
import com.sanedge.ecommerce.gateway.dto.requests.role.UpdateRoleRequest;
import com.sanedge.ecommerce.gateway.dto.responses.role.RoleResponse;
import com.sanedge.ecommerce.gateway.dto.responses.role.RoleResponseDeleteAt;
import com.sanedge.ecommerce.gateway.mapper.ProtoMapper;
import com.sanedge.ecommerce.pb.role.RoleCommandServiceGrpc;
import com.sanedge.ecommerce.pb.role.RoleQueryServiceGrpc;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleQueryServiceGrpc.RoleQueryServiceBlockingStub roleQueryStub;
    private final RoleCommandServiceGrpc.RoleCommandServiceBlockingStub roleCommandStub;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<RoleResponse>>> findAllRoles(@ModelAttribute FindAllRoles req) {
        var resp = roleQueryStub.findAllRole(com.sanedge.ecommerce.pb.role.FindAllRoleRequest.newBuilder()
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<RoleResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.roles(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<RoleResponseDeleteAt>>> findActiveRoles(
            @ModelAttribute FindAllRoles req) {
        var resp = roleQueryStub.findByActive(com.sanedge.ecommerce.pb.role.FindAllRoleRequest.newBuilder()
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<RoleResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.rolesDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @GetMapping("/trashed")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<RoleResponseDeleteAt>>> findTrashedRoles(
            @ModelAttribute FindAllRoles req) {
        var resp = roleQueryStub.findByTrashed(com.sanedge.ecommerce.pb.role.FindAllRoleRequest.newBuilder()
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<RoleResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.rolesDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<RoleResponse>> findRoleById(@PathVariable Integer id) {
        var resp = roleQueryStub.findByIdRole(com.sanedge.ecommerce.pb.role.FindByIdRoleRequest.newBuilder()
                .setRoleId(id)
                .build());
        return ResponseEntity.ok(ApiResponse.<RoleResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.role(resp.getData()))
                .build());
    }

    @GetMapping("/by-user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponse<List<RoleResponse>>> findRolesByUserId(@PathVariable Integer userId) {
        var resp = roleQueryStub.findByUserId(com.sanedge.ecommerce.pb.role.FindByIdUserRoleRequest.newBuilder()
                .setUserId(userId)
                .build());
        return ResponseEntity.ok(ApiResponse.<List<RoleResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.roles(resp.getDataList()))
                .build());
    }

    @GetMapping("/by-name")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<RoleResponse>> findRoleByName(@RequestParam String name) {
        var resp = roleQueryStub.findByNameRole(com.sanedge.ecommerce.pb.role.FindByNameRoleRequest.newBuilder()
                .setName(nvl(name))
                .build());
        return ResponseEntity.ok(ApiResponse.<RoleResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.role(resp.getData()))
                .build());
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<RoleResponse>> createRole(@RequestBody CreateRoleRequest request) {
        var resp = roleCommandStub.createRole(com.sanedge.ecommerce.pb.role.CreateRoleRequest.newBuilder()
                .setName(nvl(request.getName()))
                .build());
        return ResponseEntity.ok(ApiResponse.<RoleResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.role(resp.getData()))
                .build());
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<RoleResponse>> updateRole(@RequestBody UpdateRoleRequest request) {
        var resp = roleCommandStub.updateRole(com.sanedge.ecommerce.pb.role.UpdateRoleRequest.newBuilder()
                .setId(nvl(request.getId()))
                .setName(nvl(request.getName()))
                .build());
        return ResponseEntity.ok(ApiResponse.<RoleResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.role(resp.getData()))
                .build());
    }

    @PostMapping("/trash/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<RoleResponseDeleteAt>> trashRole(@PathVariable Integer id) {
        var resp = roleCommandStub.trashedRole(com.sanedge.ecommerce.pb.role.FindByIdRoleRequest.newBuilder()
                .setRoleId(id)
                .build());
        return ResponseEntity.ok(ApiResponse.<RoleResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.roleDeleteAt(resp.getData()))
                .build());
    }

    @PostMapping("/restore/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<RoleResponseDeleteAt>> restoreRole(@PathVariable Integer id) {
        var resp = roleCommandStub.restoreRole(com.sanedge.ecommerce.pb.role.FindByIdRoleRequest.newBuilder()
                .setRoleId(id)
                .build());
        return ResponseEntity.ok(ApiResponse.<RoleResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.roleDeleteAt(resp.getData()))
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteRole(@PathVariable Integer id) {
        var resp = roleCommandStub.deleteRolePermanent(com.sanedge.ecommerce.pb.role.FindByIdRoleRequest.newBuilder()
                .setRoleId(id)
                .build());
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build());
    }

    @PostMapping("/restore-all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> restoreAllRoles() {
        var resp = roleCommandStub.restoreAllRole(Empty.getDefaultInstance());
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build());
    }

    @PostMapping("/delete-all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteAllRoles() {
        var resp = roleCommandStub.deleteAllRolePermanent(Empty.getDefaultInstance());
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build());
    }

    private static int nvl(Integer value) {
        return value == null ? 0 : value;
    }

    private static String nvl(String value) {
        return value == null ? "" : value;
    }

    private static Boolean success(String status) {
        return "success".equalsIgnoreCase(status);
    }
}
