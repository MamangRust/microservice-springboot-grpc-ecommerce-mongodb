package com.sanedge.ecommerce.role.handler;

import java.util.List;

import org.springframework.stereotype.Service;

import com.google.protobuf.StringValue;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.common.grpc.GrpcErrorMapper;
import com.sanedge.ecommerce.common.grpc.ProtoMappers;
import com.sanedge.ecommerce.pb.role.ApiResponsePaginationRole;
import com.sanedge.ecommerce.pb.role.ApiResponsePaginationRoleDeleteAt;
import com.sanedge.ecommerce.pb.role.ApiResponseRole;
import com.sanedge.ecommerce.pb.role.ApiResponsesRole;
import com.sanedge.ecommerce.pb.role.FindAllRoleRequest;
import com.sanedge.ecommerce.pb.role.FindByIdRoleRequest;
import com.sanedge.ecommerce.pb.role.FindByIdUserRoleRequest;
import com.sanedge.ecommerce.pb.role.FindByNameRoleRequest;
import com.sanedge.ecommerce.pb.role.RoleQueryServiceGrpc;
import com.sanedge.ecommerce.pb.role.RoleResponse;
import com.sanedge.ecommerce.pb.role.RoleResponseDeleteAt;
import com.sanedge.ecommerce.role.domain.requests.FindAllRoles;
import com.sanedge.ecommerce.role.service.RoleQueryService;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleQueryHandler extends RoleQueryServiceGrpc.RoleQueryServiceImplBase {

    private final RoleQueryService roleQueryService;

    @Override
    public void findAllRole(FindAllRoleRequest request, StreamObserver<ApiResponsePaginationRole> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.role.domain.responses.RoleResponse>> res = roleQueryService
                    .findAll(toFindAll(request));

            ApiResponsePaginationRole.Builder builder = ApiResponsePaginationRole.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .setPagination(ProtoMappers.toProtoPagination(res.getPagination()));
            res.getData().forEach(r -> builder.addData(toProto(r)));

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findByIdRole(FindByIdRoleRequest request, StreamObserver<ApiResponseRole> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.role.domain.responses.RoleResponse> res = roleQueryService
                    .findById(request.getRoleId());

            observer.onNext(ApiResponseRole.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .setData(toProto(res.getData()))
                    .build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findByNameRole(FindByNameRoleRequest request, StreamObserver<ApiResponseRole> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.role.domain.responses.RoleResponse> res = roleQueryService
                    .findByName(request.getName());

            observer.onNext(ApiResponseRole.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .setData(toProto(res.getData()))
                    .build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findByUserId(FindByIdUserRoleRequest request, StreamObserver<ApiResponsesRole> observer) {
        try {
            ApiResponse<List<com.sanedge.ecommerce.role.domain.responses.RoleResponse>> res = roleQueryService
                    .findByUserId(request.getUserId());

            ApiResponsesRole.Builder builder = ApiResponsesRole.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            res.getData().forEach(r -> builder.addData(toProto(r)));

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findByActive(FindAllRoleRequest request, StreamObserver<ApiResponsePaginationRoleDeleteAt> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.role.domain.responses.RoleResponseDeleteAt>> res = roleQueryService
                    .findByActive(toFindAll(request));

            ApiResponsePaginationRoleDeleteAt.Builder builder = ApiResponsePaginationRoleDeleteAt.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .setPagination(ProtoMappers.toProtoPagination(res.getPagination()));
            res.getData().forEach(r -> builder.addData(toProtoDeleteAt(r)));

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findByTrashed(FindAllRoleRequest request, StreamObserver<ApiResponsePaginationRoleDeleteAt> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.role.domain.responses.RoleResponseDeleteAt>> res = roleQueryService
                    .findByTrashed(toFindAll(request));

            ApiResponsePaginationRoleDeleteAt.Builder builder = ApiResponsePaginationRoleDeleteAt.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .setPagination(ProtoMappers.toProtoPagination(res.getPagination()));
            res.getData().forEach(r -> builder.addData(toProtoDeleteAt(r)));

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    private FindAllRoles toFindAll(FindAllRoleRequest request) {
        FindAllRoles req = new FindAllRoles();
        req.setPage(request.getPage());
        req.setPageSize(request.getPageSize());
        req.setSearch(request.getSearch());
        return req;
    }

    private RoleResponse toProto(com.sanedge.ecommerce.role.domain.responses.RoleResponse r) {
        return RoleResponse.newBuilder()
                .setId(r.getId() == null ? 0 : r.getId())
                .setName(ProtoMappers.nvl(r.getName()))
                .setCreatedAt(ProtoMappers.nvl(r.getCreatedAt()))
                .setUpdatedAt(ProtoMappers.nvl(r.getUpdatedAt()))
                .build();
    }

    private RoleResponseDeleteAt toProtoDeleteAt(
            com.sanedge.ecommerce.role.domain.responses.RoleResponseDeleteAt r) {
        return RoleResponseDeleteAt.newBuilder()
                .setId(r.getId() == null ? 0 : r.getId())
                .setName(ProtoMappers.nvl(r.getName()))
                .setCreatedAt(ProtoMappers.nvl(r.getCreatedAt()))
                .setUpdatedAt(ProtoMappers.nvl(r.getUpdatedAt()))
                .setDeletedAt(StringValue.of(ProtoMappers.nvl(r.getDeletedAt())))
                .build();
    }
}
