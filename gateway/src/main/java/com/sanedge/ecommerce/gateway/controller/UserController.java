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
import org.springframework.web.bind.annotation.RestController;

import com.google.protobuf.Empty;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.common.grpc.ProtoMappers;
import com.sanedge.ecommerce.gateway.dto.requests.user.CreateUserRequest;
import com.sanedge.ecommerce.gateway.dto.requests.user.FindAllUserRequest;
import com.sanedge.ecommerce.gateway.dto.requests.user.UpdateUserRequest;
import com.sanedge.ecommerce.gateway.dto.responses.user.UserResponse;
import com.sanedge.ecommerce.gateway.dto.responses.user.UserResponseDeleteAt;
import com.sanedge.ecommerce.gateway.mapper.ProtoMapper;
import com.sanedge.ecommerce.pb.user.UserCommandServiceGrpc;
import com.sanedge.ecommerce.pb.user.UserQueryServiceGrpc;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserQueryServiceGrpc.UserQueryServiceBlockingStub userQueryStub;
    private final UserCommandServiceGrpc.UserCommandServiceBlockingStub userCommandStub;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<UserResponse>>> findAll(@ModelAttribute FindAllUserRequest req) {
        var resp = userQueryStub.findAll(com.sanedge.ecommerce.pb.user.FindAllUserRequest.newBuilder()
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<UserResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.users(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponse<UserResponse>> findById(@PathVariable Integer id) {
        var resp = userQueryStub.findById(com.sanedge.ecommerce.pb.user.FindByIdUserRequest.newBuilder()
                .setId(id)
                .build());
        return ResponseEntity.ok(ApiResponse.<UserResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.user(resp.getData()))
                .build());
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<UserResponseDeleteAt>>> findActive(
            @ModelAttribute FindAllUserRequest req) {
        var resp = userQueryStub.findByActive(com.sanedge.ecommerce.pb.user.FindAllUserRequest.newBuilder()
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<UserResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.usersDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @GetMapping("/trashed")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<UserResponseDeleteAt>>> findTrashed(
            @ModelAttribute FindAllUserRequest req) {
        var resp = userQueryStub.findByTrashed(com.sanedge.ecommerce.pb.user.FindAllUserRequest.newBuilder()
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<UserResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.usersDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> create(@RequestBody CreateUserRequest req) {
        var resp = userCommandStub.create(com.sanedge.ecommerce.pb.user.CreateUserRequest.newBuilder()
                .setFirstname(nvl(req.getFirstname()))
                .setLastname(nvl(req.getLastname()))
                .setEmail(nvl(req.getEmail()))
                .setPassword(nvl(req.getPassword()))
                .setConfirmPassword(nvl(req.getConfirmPassword()))
                .build());
        return ResponseEntity.ok(ApiResponse.<UserResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.user(resp.getData()))
                .build());
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> update(@RequestBody UpdateUserRequest req) {
        var resp = userCommandStub.update(com.sanedge.ecommerce.pb.user.UpdateUserRequest.newBuilder()
                .setId(nvl(req.getId()))
                .setFirstname(nvl(req.getFirstname()))
                .setLastname(nvl(req.getLastname()))
                .setEmail(nvl(req.getEmail()))
                .setPassword(nvl(req.getPassword()))
                .setConfirmPassword(nvl(req.getConfirmPassword()))
                .build());
        return ResponseEntity.ok(ApiResponse.<UserResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.user(resp.getData()))
                .build());
    }

    @PostMapping("/trash/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponseDeleteAt>> trash(@PathVariable Integer id) {
        var resp = userCommandStub.trashedUser(com.sanedge.ecommerce.pb.user.FindByIdUserRequest.newBuilder()
                .setId(id)
                .build());
        return ResponseEntity.ok(ApiResponse.<UserResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.userDeleteAt(resp.getData()))
                .build());
    }

    @PostMapping("/restore/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponseDeleteAt>> restore(@PathVariable Integer id) {
        var resp = userCommandStub.restoreUser(com.sanedge.ecommerce.pb.user.FindByIdUserRequest.newBuilder()
                .setId(id)
                .build());
        return ResponseEntity.ok(ApiResponse.<UserResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.userDeleteAt(resp.getData()))
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deletePermanent(@PathVariable Integer id) {
        var resp = userCommandStub.deleteUserPermanent(com.sanedge.ecommerce.pb.user.FindByIdUserRequest.newBuilder()
                .setId(id)
                .build());
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build());
    }

    @PostMapping("/restore-all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> restoreAll() {
        var resp = userCommandStub.restoreAllUser(Empty.getDefaultInstance());
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build());
    }

    @PostMapping("/delete-all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteAll() {
        var resp = userCommandStub.deleteAllUserPermanent(Empty.getDefaultInstance());
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
