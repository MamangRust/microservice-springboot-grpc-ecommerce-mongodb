package com.sanedge.ecommerce.order_item.service.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.common.domain.responses.api.PaginationMeta;
import com.sanedge.ecommerce.common.observability.TracingMetrics;
import com.sanedge.ecommerce.order_item.domain.requests.FindAllOrderItems;
import com.sanedge.ecommerce.order_item.domain.responses.OrderItemResponse;
import com.sanedge.ecommerce.order_item.domain.responses.OrderItemResponseDeleteAt;
import com.sanedge.ecommerce.order_item.models.OrderItem;
import com.sanedge.ecommerce.order_item.repository.OrderItemQueryRepository;
import com.sanedge.ecommerce.order_item.service.OrderItemQueryService;

import io.opentelemetry.api.common.Attributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderItemQueryImplService implements OrderItemQueryService {

    private final OrderItemQueryRepository orderItemQueryRepository;
    private final TracingMetrics tracingMetrics;

    @Override
    public ApiResponsePagination<List<OrderItemResponse>> findAll(FindAllOrderItems req) {
        return tracingMetrics.traceAndMeasure("findAllOrderItems", "find_all_order_items", () -> {
            int page = req.getPage() != null && req.getPage() > 0 ? req.getPage() - 1 : 0;
            int pageSize = req.getPageSize() != null && req.getPageSize() > 0 ? req.getPageSize() : 10;

            log.info("🔍 Searching all order items | Page: {}, Size: {}", page + 1, pageSize);

            try {
                Pageable pageable = PageRequest.of(page, pageSize);
                Page<OrderItem> orderItemPage = orderItemQueryRepository.findOrderItems(pageable);

                List<OrderItemResponse> responses = orderItemPage.getContent()
                        .stream()
                        .map(OrderItemResponse::from)
                        .toList();

                log.info("✅ Found {} order items", responses.size());

                return ApiResponsePagination.<List<OrderItemResponse>>builder()
                        .status("success")
                        .message("Order items retrieved successfully")
                        .data(responses)
                        .pagination(PaginationMeta.fromSpringPage(orderItemPage))
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to fetch order items", e);
                return ApiResponsePagination.<List<OrderItemResponse>>builder()
                        .status("error")
                        .message("Failed to fetch order items")
                        .data(Collections.emptyList())
                        .pagination(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponsePagination<List<OrderItemResponseDeleteAt>> findByActive(FindAllOrderItems req) {
        return tracingMetrics.traceAndMeasure("findActiveOrderItems", "find_active_order_items", () -> {
            int page = req.getPage() != null && req.getPage() > 0 ? req.getPage() - 1 : 0;
            int pageSize = req.getPageSize() != null && req.getPageSize() > 0 ? req.getPageSize() : 10;

            log.info("🔍 Searching active order items | Page: {}, Size: {}", page + 1, pageSize);

            try {
                Pageable pageable = PageRequest.of(page, pageSize);
                Page<OrderItem> orderItemPage = orderItemQueryRepository.findActiveOrderItems(pageable);

                List<OrderItemResponseDeleteAt> responses = orderItemPage.getContent()
                        .stream()
                        .map(OrderItemResponseDeleteAt::from)
                        .toList();

                log.info("✅ Found {} active order items", responses.size());

                return ApiResponsePagination.<List<OrderItemResponseDeleteAt>>builder()
                        .status("success")
                        .message("Active order items retrieved successfully")
                        .data(responses)
                        .pagination(PaginationMeta.fromSpringPage(orderItemPage))
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to fetch active order items", e);
                return ApiResponsePagination.<List<OrderItemResponseDeleteAt>>builder()
                        .status("error")
                        .message("Failed to fetch active order items")
                        .data(Collections.emptyList())
                        .pagination(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponsePagination<List<OrderItemResponseDeleteAt>> findByTrashed(FindAllOrderItems req) {
        return tracingMetrics.traceAndMeasure("findTrashedOrderItems", "find_trashed_order_items", () -> {
            int page = req.getPage() != null && req.getPage() > 0 ? req.getPage() - 1 : 0;
            int pageSize = req.getPageSize() != null && req.getPageSize() > 0 ? req.getPageSize() : 10;

            log.info("🔍 Searching trashed order items | Page: {}, Size: {}", page + 1, pageSize);

            try {
                Pageable pageable = PageRequest.of(page, pageSize);
                Page<OrderItem> orderItemPage = orderItemQueryRepository.findTrashedOrderItems(pageable);

                List<OrderItemResponseDeleteAt> responses = orderItemPage.getContent()
                        .stream()
                        .map(OrderItemResponseDeleteAt::from)
                        .toList();

                log.info("✅ Found {} trashed order items", responses.size());

                return ApiResponsePagination.<List<OrderItemResponseDeleteAt>>builder()
                        .status("success")
                        .message("Trashed order items retrieved successfully")
                        .data(responses)
                        .pagination(PaginationMeta.fromSpringPage(orderItemPage))
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to fetch trashed order items", e);
                return ApiResponsePagination.<List<OrderItemResponseDeleteAt>>builder()
                        .status("error")
                        .message("Failed to fetch trashed order items")
                        .data(Collections.emptyList())
                        .pagination(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<List<OrderItemResponse>> findByOrder(Long orderId) {
        Attributes attrs = Attributes.builder()
                .put("order.id", String.valueOf(orderId))
                .build();

        return tracingMetrics.traceAndMeasure("findOrderItemByOrder", "find_order_items_by_order", attrs, () -> {
            log.info("🔍 Finding order items for orderId={}", orderId);
            try {
                List<OrderItemResponse> responses = orderItemQueryRepository
                        .findOrderItemByOrder(orderId)
                        .stream()
                        .map(OrderItemResponse::from)
                        .toList();

                return ApiResponse.<List<OrderItemResponse>>builder()
                        .status("success")
                        .message("Order items for order retrieved successfully")
                        .data(responses)
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to fetch order items for orderId={}: {}", orderId, e.getMessage(), e);
                return ApiResponse.<List<OrderItemResponse>>builder()
                        .status("error")
                        .message("Failed to fetch order items")
                        .data(List.of())
                        .build();
            }
        });
    }
}
