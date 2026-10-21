package com.sanedge.ecommerce.order_item.service.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.exception.ResourceNotFoundException;
import com.sanedge.ecommerce.common.observability.TracingMetrics;
import com.sanedge.ecommerce.order_item.domain.requests.CreateOrderItemRequest;
import com.sanedge.ecommerce.order_item.domain.requests.UpdateOrderItemRequest;
import com.sanedge.ecommerce.order_item.domain.responses.OrderItemResponse;
import com.sanedge.ecommerce.order_item.domain.responses.OrderItemResponseDeleteAt;
import com.sanedge.ecommerce.order_item.models.OrderItem;
import com.sanedge.ecommerce.order_item.repository.OrderItemCommandRepository;
import com.sanedge.ecommerce.order_item.repository.OrderItemQueryRepository;
import com.sanedge.ecommerce.order_item.service.OrderItemCommandService;

import io.opentelemetry.api.common.Attributes;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderItemCommandImplService implements OrderItemCommandService {

    private final OrderItemQueryRepository orderItemQueryRepository;
    private final OrderItemCommandRepository orderItemCommandRepository;
    private final Validator validator;
    private final TracingMetrics tracingMetrics;

    @Override
    public ApiResponse<OrderItemResponse> create(CreateOrderItemRequest request) {
        return tracingMetrics.traceAndMeasure("createOrderItem", "create_order_item", () -> {
            try {
                validateRequest(request);

                log.info("🆕 Creating order item for orderId={} productId={}", request.getOrderId(),
                        request.getProductId());

                OrderItem orderItem = new OrderItem();
                orderItem.setOrderId(request.getOrderId());
                orderItem.setProductId(request.getProductId());
                orderItem.setQuantity(request.getQuantity());
                orderItem.setPrice(request.getPrice());
                OrderItem saved = orderItemCommandRepository.save(orderItem);

                log.info("✅ Order item created successfully with id={}", saved.getOrderItemId());

                return ApiResponse.<OrderItemResponse>builder()
                        .status("success")
                        .message("Order item created successfully")
                        .data(OrderItemResponse.from(saved))
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to create order item", e);
                return ApiResponse.<OrderItemResponse>builder()
                        .status("error")
                        .message(e.getMessage())
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<OrderItemResponse> update(UpdateOrderItemRequest request) {
        Attributes attrs = Attributes.builder()
                .put("order_item.id", String.valueOf(request.getOrderItemId()))
                .build();

        return tracingMetrics.traceAndMeasure("updateOrderItem", "update_order_item", attrs, () -> {
            try {
                validateRequest(request);

                log.info("🔄 Updating order item id={}", request.getOrderItemId());

                OrderItem orderItem = orderItemCommandRepository
                        .findById(String.valueOf(request.getOrderItemId()))
                        .orElseThrow(() -> new ResourceNotFoundException("Order item not found"));

                orderItem.setQuantity(request.getQuantity());
                orderItem.setPrice(request.getPrice());
                OrderItem updated = orderItemCommandRepository.save(orderItem);

                log.info("✅ Order item updated successfully with id={}", updated.getOrderItemId());

                return ApiResponse.<OrderItemResponse>builder()
                        .status("success")
                        .message("Order item updated successfully")
                        .data(OrderItemResponse.from(updated))
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to update order item id={}", request.getOrderItemId(), e);
                return ApiResponse.<OrderItemResponse>builder()
                        .status("error")
                        .message(e.getMessage())
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<OrderItemResponseDeleteAt> trash(Long id) {
        Attributes attrs = Attributes.builder()
                .put("order_item.id", String.valueOf(id))
                .build();

        return tracingMetrics.traceAndMeasure("trashOrderItem", "trash_order_item", attrs, () -> {
            log.info("🗑️ Trashing order item id={}", id);
            try {
                OrderItem orderItem = orderItemCommandRepository.trashed(String.valueOf(id));
                if (orderItem == null) {
                    return ApiResponse.<OrderItemResponseDeleteAt>builder()
                            .status("error")
                            .message("Order item not found")
                            .data(null)
                            .build();
                }
                return ApiResponse.<OrderItemResponseDeleteAt>builder()
                        .status("success")
                        .message("Order item trashed successfully")
                        .data(OrderItemResponseDeleteAt.from(orderItem))
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to trash order item id={}", id, e);
                return ApiResponse.<OrderItemResponseDeleteAt>builder()
                        .status("error")
                        .message("Failed to trash order item: " + e.getMessage())
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<OrderItemResponseDeleteAt> restore(Long id) {
        Attributes attrs = Attributes.builder()
                .put("order_item.id", String.valueOf(id))
                .build();

        return tracingMetrics.traceAndMeasure("restoreOrderItem", "restore_order_item", attrs, () -> {
            log.info("♻️ Restoring order item id={}", id);
            try {
                OrderItem orderItem = orderItemCommandRepository.restore(String.valueOf(id));
                if (orderItem == null) {
                    return ApiResponse.<OrderItemResponseDeleteAt>builder()
                            .status("error")
                            .message("Order item not found in trash")
                            .data(null)
                            .build();
                }
                return ApiResponse.<OrderItemResponseDeleteAt>builder()
                        .status("success")
                        .message("Order item restored successfully")
                        .data(OrderItemResponseDeleteAt.from(orderItem))
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to restore order item id={}", id, e);
                return ApiResponse.<OrderItemResponseDeleteAt>builder()
                        .status("error")
                        .message("Failed to restore order item: " + e.getMessage())
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<Boolean> deletePermanent(Long id) {
        Attributes attrs = Attributes.builder()
                .put("order_item.id", String.valueOf(id))
                .build();

        return tracingMetrics.traceAndMeasure("deleteOrderItemPermanent", "delete_order_item_permanent", attrs, () -> {
            log.info("🧨 Permanently deleting order item id={}", id);
            try {
                OrderItem orderItem = orderItemCommandRepository.deletePermanent(String.valueOf(id));
                if (orderItem == null) {
                    return ApiResponse.<Boolean>builder()
                            .status("error")
                            .message("Order item not found")
                            .data(false)
                            .build();
                }
                return ApiResponse.<Boolean>builder()
                        .status("success")
                        .message("Order item permanently deleted")
                        .data(true)
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to permanently delete order item id={}", id, e);
                return ApiResponse.<Boolean>builder()
                        .status("error")
                        .message("Failed to permanently delete order item: " + e.getMessage())
                        .data(false)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<Boolean> restoreAll() {
        return tracingMetrics.traceAndMeasure("restoreAllOrderItems", "restore_all_order_items", () -> {
            log.info("🔄 Restoring ALL trashed order items");
            try {
                boolean success = orderItemCommandRepository.restoreAllDeleted();
                if (!success) {
                    return ApiResponse.<Boolean>builder()
                            .status("error")
                            .message("No trashed order items found")
                            .data(false)
                            .build();
                }
                return ApiResponse.<Boolean>builder()
                        .status("success")
                        .message("All order items restored")
                        .data(true)
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to restore all order items", e);
                return ApiResponse.<Boolean>builder()
                        .status("error")
                        .message("Failed to restore all order items: " + e.getMessage())
                        .data(false)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<Boolean> deleteAll() {
        return tracingMetrics.traceAndMeasure("deleteAllOrderItemsPermanent", "delete_all_order_items_permanent", () -> {
            log.info("💣 Permanently deleting ALL trashed order items");
            try {
                boolean success = orderItemCommandRepository.deleteAllDeleted();
                if (!success) {
                    return ApiResponse.<Boolean>builder()
                            .status("error")
                            .message("No trashed order items found")
                            .data(false)
                            .build();
                }
                return ApiResponse.<Boolean>builder()
                        .status("success")
                        .message("All trashed order items permanently deleted")
                        .data(true)
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to delete all order items", e);
                return ApiResponse.<Boolean>builder()
                        .status("error")
                        .message("Failed to delete all order items: " + e.getMessage())
                        .data(false)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<Boolean> deleteByOrderPermanent(Long orderId) {
        Attributes attrs = Attributes.builder()
                .put("order.id", String.valueOf(orderId))
                .build();

        return tracingMetrics.traceAndMeasure("deleteByOrderPermanent", "delete_order_items_by_order_permanent", attrs,
                () -> {
                    log.info("🧨 Permanently deleting all trashed order items for orderId={}", orderId);
                    try {
                        boolean success = orderItemCommandRepository.deleteByOrderPermanent(orderId);
                        if (!success) {
                            return ApiResponse.<Boolean>builder()
                                    .status("error")
                                    .message("No order items found for order id: " + orderId)
                                    .data(false)
                                    .build();
                        }
                        return ApiResponse.<Boolean>builder()
                                .status("success")
                                .message("Order items permanently deleted for order id: " + orderId)
                                .data(true)
                                .build();
                    } catch (Exception e) {
                        log.error("💥 Failed to permanently delete order items for orderId={}", orderId, e);
                        return ApiResponse.<Boolean>builder()
                                .status("error")
                                .message("Failed to permanently delete order items: " + e.getMessage())
                                .data(false)
                                .build();
                    }
                });
    }

    @Override
    public ApiResponse<Boolean> deleteByOrderRollback(Long orderId) {
        Attributes attrs = Attributes.builder()
                .put("order.id", String.valueOf(orderId))
                .build();

        return tracingMetrics.traceAndMeasure("deleteByOrderRollback", "delete_order_items_by_order_rollback", attrs,
                () -> {
                    log.info("↩️ Rolling back order items for orderId={}", orderId);
                    try {
                        boolean success = orderItemCommandRepository.deleteByOrderRollback(orderId);
                        return ApiResponse.<Boolean>builder()
                                .status("success")
                                .message("Order items rolled back")
                                .data(success)
                                .build();
                    } catch (Exception e) {
                        log.error("💥 Failed to rollback order items for orderId={}", orderId, e);
                        return ApiResponse.<Boolean>builder()
                                .status("error")
                                .message("Failed to rollback order items: " + e.getMessage())
                                .data(false)
                                .build();
                    }
                });
    }

    @Override
    public ApiResponse<Integer> calculateTotalPrice(Long orderId) {
        Attributes attrs = Attributes.builder()
                .put("order.id", String.valueOf(orderId))
                .build();

        return tracingMetrics.traceAndMeasure("calculateTotalPrice", "calculate_total_price", attrs, () -> {
            log.info("🔢 Calculating total price for orderId={}", orderId);
            try {
                List<OrderItem> items = orderItemQueryRepository.findOrderItemByOrder(orderId);
                int total = items.stream()
                        .mapToInt(item -> item.getPrice() * item.getQuantity())
                        .sum();

                log.info("✅ Total price for orderId={} is {}", orderId, total);

                return ApiResponse.<Integer>builder()
                        .status("success")
                        .message("Total price calculated")
                        .data(total)
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to calculate total price for orderId={}", orderId, e);
                return ApiResponse.<Integer>builder()
                        .status("error")
                        .message("Failed to calculate total price")
                        .data(0)
                        .build();
            }
        });
    }

    private <T> void validateRequest(T req) {
        Set<ConstraintViolation<T>> violations = validator.validate(req);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<T> violation : violations) {
                sb.append(violation.getPropertyPath()).append(": ").append(violation.getMessage()).append("; ");
            }
            log.error("Validation failed: {}", sb);
            throw new ConstraintViolationException("Validation failed: " + sb, violations);
        }
    }
}
