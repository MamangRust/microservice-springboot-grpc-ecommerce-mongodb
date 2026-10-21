package com.sanedge.ecommerce.role.service.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.role.domain.requests.FindAllRoles;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.common.domain.responses.api.PaginationMeta;
import com.sanedge.ecommerce.common.observability.TracingMetrics;
import com.sanedge.ecommerce.role.domain.responses.RoleResponse;
import com.sanedge.ecommerce.role.domain.responses.RoleResponseDeleteAt;
import com.sanedge.ecommerce.role.models.Role;
import com.sanedge.ecommerce.role.repository.RoleQueryRepository;
import com.sanedge.ecommerce.role.service.RoleQueryService;

import io.opentelemetry.api.common.Attributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class RoleQueryImplService implements RoleQueryService {
        private final RoleQueryRepository roleQueryRepository;
        private final TracingMetrics tracingMetrics;

        @Override
        public ApiResponsePagination<List<RoleResponse>> findAll(FindAllRoles req) {
                Attributes attrs = Attributes.builder()
                                .put("role.page", String.valueOf(req.getPage()))
                                .put("role.size", String.valueOf(req.getPageSize()))
                                .build();

                return tracingMetrics.traceAndMeasure("findAllRoles", "find_all_roles", attrs, () -> {
                        int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                        String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                        log.info("🔍 Searching all roles | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                                        keyword.isEmpty() ? "None" : keyword);

                        try {
                                Pageable pageable = PageRequest.of(page, pageSize);
                                Page<Role> rolePage = roleQueryRepository.findRoles(keyword, pageable);

                                List<RoleResponse> responses = rolePage.getContent()
                                                .stream()
                                                .map(RoleResponse::from)
                                                .toList();

                                log.info("✅ Found {} roles", responses.size());

                                return ApiResponsePagination.<List<RoleResponse>>builder()
                                                .status("success")
                                                .message("Roles retrieved successfully")
                                                .data(responses)
                                                .pagination(PaginationMeta.fromSpringPage(rolePage))
                                                .build();
                        } catch (Exception e) {
                                log.error("💥 Failed to fetch roles", e);
                                return ApiResponsePagination.<List<RoleResponse>>builder()
                                                .status("error")
                                                .message("Failed to fetch roles")
                                                .data(Collections.emptyList())
                                                .pagination(null)
                                                .build();
                        }
                });
        }

        @Override
        public ApiResponsePagination<List<RoleResponseDeleteAt>> findByActive(FindAllRoles req) {
                Attributes attrs = Attributes.builder()
                                .put("role.page", String.valueOf(req.getPage()))
                                .put("role.size", String.valueOf(req.getPageSize()))
                                .build();

                return tracingMetrics.traceAndMeasure("findActiveRoles", "find_active_roles", attrs, () -> {
                        int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                        String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                        log.info("🔍 Searching active roles | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                                        keyword.isEmpty() ? "None" : keyword);

                        try {
                                Pageable pageable = PageRequest.of(page, pageSize);
                                Page<Role> rolePage = roleQueryRepository.findActiveRoles(keyword, pageable);

                                List<RoleResponseDeleteAt> responses = rolePage.getContent()
                                                .stream()
                                                .map(RoleResponseDeleteAt::from)
                                                .toList();

                                log.info("✅ Found {} active roles", responses.size());

                                return ApiResponsePagination.<List<RoleResponseDeleteAt>>builder()
                                                .status("success")
                                                .message("Active roles retrieved successfully")
                                                .data(responses)
                                                .pagination(PaginationMeta.fromSpringPage(rolePage))
                                                .build();
                        } catch (Exception e) {
                                log.error("💥 Failed to fetch active roles", e);
                                return ApiResponsePagination.<List<RoleResponseDeleteAt>>builder()
                                                .status("error")
                                                .message("Failed to fetch active roles")
                                                .data(Collections.emptyList())
                                                .pagination(null)
                                                .build();
                        }
                });
        }

        @Override
        public ApiResponsePagination<List<RoleResponseDeleteAt>> findByTrashed(FindAllRoles req) {
                Attributes attrs = Attributes.builder()
                                .put("role.page", String.valueOf(req.getPage()))
                                .put("role.size", String.valueOf(req.getPageSize()))
                                .build();

                return tracingMetrics.traceAndMeasure("findTrashedRoles", "find_trashed_roles", attrs, () -> {
                        int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                        String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                        log.info("🔍 Searching trashed roles | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                                        keyword.isEmpty() ? "None" : keyword);

                        try {
                                Pageable pageable = PageRequest.of(page, pageSize);
                                Page<Role> rolePage = roleQueryRepository.findTrashedRoles(keyword, pageable);

                                List<RoleResponseDeleteAt> responses = rolePage.getContent()
                                                .stream()
                                                .map(RoleResponseDeleteAt::from)
                                                .toList();

                                log.info("✅ Found {} trashed roles", responses.size());

                                return ApiResponsePagination.<List<RoleResponseDeleteAt>>builder()
                                                .status("success")
                                                .message("Trashed roles retrieved successfully")
                                                .data(responses)
                                                .pagination(PaginationMeta.fromSpringPage(rolePage))
                                                .build();
                        } catch (Exception e) {
                                log.error("💥 Failed to fetch trashed roles", e);
                                return ApiResponsePagination.<List<RoleResponseDeleteAt>>builder()
                                                .status("error")
                                                .message("Failed to fetch trashed roles")
                                                .data(Collections.emptyList())
                                                .pagination(null)
                                                .build();
                        }
                });
        }

        @Override
        public ApiResponse<RoleResponse> findById(Integer id) {
                Attributes attrs = Attributes.builder()
                                .put("role.id", String.valueOf(id))
                                .build();

                return tracingMetrics.traceAndMeasure("findRoleById", "find_role_by_id", attrs, () -> {
                        log.info("🔍 Finding role by id={}", id);
                        try {
                                return roleQueryRepository.findById(String.valueOf(id))
                                                .map(role -> ApiResponse.<RoleResponse>builder()
                                                                .status("success")
                                                                .message("Role retrieved successfully")
                                                                .data(RoleResponse.from(role))
                                                                .build())
                                                .orElseGet(() -> {
                                                        log.warn("❌ Role not found with id={}", id);
                                                        return ApiResponse.<RoleResponse>builder()
                                                                        .status("error")
                                                                        .message("Role not found")
                                                                        .data(null)
                                                                        .build();
                                                });
                        } catch (Exception e) {
                                log.error("💥 Failed to fetch role by id={}", id, e);
                                return ApiResponse.<RoleResponse>builder()
                                                .status("error")
                                                .message("Failed to fetch role")
                                                .data(null)
                                                .build();
                        }
                });
        }

        @Override
        public ApiResponse<List<RoleResponse>> findByUserId(Integer userId) {
                Attributes attrs = Attributes.builder()
                                .put("user.id", String.valueOf(userId))
                                .build();

                return tracingMetrics.traceAndMeasure("findRolesByUserId", "find_role_by_user_id", attrs, () -> {
                        log.info("🔍 Finding roles by user_id={}", userId);
                        try {
                                List<Role> roles = roleQueryRepository.findUserRoles(userId.longValue());

                                List<RoleResponse> responses = roles.stream()
                                                .map(RoleResponse::from)
                                                .toList();

                                return ApiResponse.<List<RoleResponse>>builder()
                                                .status("success")
                                                .message("Roles retrieved successfully")
                                                .data(responses)
                                                .build();
                        } catch (Exception e) {
                                log.error("💥 Failed to fetch roles for user_id={}", userId, e);
                                return ApiResponse.<List<RoleResponse>>builder()
                                                .status("error")
                                                .message("Failed to fetch roles by user")
                                                .data(List.of())
                                                .build();
                        }
                });
        }

        @Override
        public ApiResponse<RoleResponse> findByName(String name) {
                Attributes attrs = Attributes.builder()
                                .put("role.name", name != null ? name : "")
                                .build();

                return tracingMetrics.traceAndMeasure("findRoleByName", "find_role_by_name", attrs, () -> {
                        log.info("🔍 Finding role by name={}", name);
                        try {
                                return roleQueryRepository.findByRoleName(name)
                                                .map(role -> ApiResponse.<RoleResponse>builder()
                                                                .status("success")
                                                                .message("Role retrieved successfully")
                                                                .data(RoleResponse.from(role))
                                                                .build())
                                                .orElseGet(() -> {
                                                        log.warn("❌ Role not found with name={}", name);
                                                        return ApiResponse.<RoleResponse>builder()
                                                                        .status("error")
                                                                        .message("Role not found")
                                                                        .data(null)
                                                                        .build();
                                                });
                        } catch (Exception e) {
                                log.error("💥 Failed to fetch role by name={}", name, e);
                                return ApiResponse.<RoleResponse>builder()
                                                .status("error")
                                                .message("Failed to fetch role by name")
                                                .data(null)
                                                .build();
                        }
                });
        }
}
