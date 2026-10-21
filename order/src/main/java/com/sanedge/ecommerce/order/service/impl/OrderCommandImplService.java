package com.sanedge.ecommerce.order.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanedge.ecommerce.common.adapter.merchant.MerchantPort;
import com.sanedge.ecommerce.common.adapter.model.OrderItem;
import com.sanedge.ecommerce.common.adapter.model.Product;
import com.sanedge.ecommerce.common.adapter.order_item.OrderItemPort;
import com.sanedge.ecommerce.common.adapter.product.ProductPort;
import com.sanedge.ecommerce.common.adapter.shipping_address.ShippingAddressPort;
import com.sanedge.ecommerce.common.adapter.user.UserPort;
import com.sanedge.ecommerce.common.config.MongoTransactionRunner;
import com.sanedge.ecommerce.common.config.NumericIdGenerator;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.exception.ResourceNotFoundException;
import com.sanedge.ecommerce.common.grpc.ProtoMappers;
import com.sanedge.ecommerce.common.observability.TracingMetrics;
import com.sanedge.ecommerce.order.domain.requests.order.CreateOrderItemRequest;
import com.sanedge.ecommerce.order.domain.requests.order.CreateOrderRequest;
import com.sanedge.ecommerce.order.domain.requests.order.UpdateOrderItemRequest;
import com.sanedge.ecommerce.order.domain.requests.order.UpdateOrderRequest;
import com.sanedge.ecommerce.order.domain.stats.OrderItemStats;
import com.sanedge.ecommerce.order.domain.requests.shipping.CreateShippingAddressRequest;
import com.sanedge.ecommerce.order.domain.requests.shipping.UpdateShippingAddressRequest;
import com.sanedge.ecommerce.order.domain.responses.order.OrderResponse;
import com.sanedge.ecommerce.order.domain.responses.order.OrderResponseDeleteAt;
import com.sanedge.ecommerce.order.models.Order;
import com.sanedge.ecommerce.order.models.OrderOutbox;
import com.sanedge.ecommerce.order.repository.OrderCommandRepository;
import com.sanedge.ecommerce.order.repository.OrderOutboxRepository;
import com.sanedge.ecommerce.order.repository.OrderQueryRepository;
import com.sanedge.ecommerce.order.service.OrderCommandService;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.opentelemetry.api.common.Attributes;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderCommandImplService implements OrderCommandService {
    private final OrderQueryRepository orderQueryRepository;
    private final OrderCommandRepository orderCommandRepository;
    private final Validator validator;
    private final MerchantPort merchantPort;
    private final UserPort userPort;
    private final ProductPort productPort;
    private final OrderItemPort orderItemPort;
    private final ShippingAddressPort shippingPort;
    private final TracingMetrics tracingMetrics;
    private final OrderOutboxRepository orderOutboxRepository;
    private final NumericIdGenerator idGenerator;
    private final MongoTransactionRunner transactionRunner;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public ApiResponse<OrderResponse> create(CreateOrderRequest request) {
        Attributes attrs = Attributes.builder()
                .put("merchant.id", String.valueOf(request.getMerchantId()))
                .put("user.id", String.valueOf(request.getUserId()))
                .build();

        return tracingMetrics.traceAndMeasure("createOrder", "create_order", attrs, () -> {
            try {
                log.info("🆕 Creating new order for merchantId={} and userId={}", request.getMerchantId(),
                        request.getUserId());

                validateRequest(request);

                ensureMerchant(request.getMerchantId());
                ensureUser(request.getUserId());

                Order order = new Order();
                // Id dipesan di sisi klien supaya bisa dipakai sebagai foreign key untuk
                // panggilan gRPC di bawah tanpa menyimpan dokumen lebih dulu — transaksi
                // MongoDB tetap singkat (hanya 1 write order + N write outbox).
                order.setOrderId(idGenerator.nextId());
                order.setMerchantId(request.getMerchantId());
                order.setUserId(request.getUserId());
                order.setCreatedAt(LocalDateTime.now());
                order.setUpdatedAt(LocalDateTime.now());
                order.setTotalPrice(0);

                int orderId = Integer.parseInt(order.getOrderId());
                int totalPrice = 0;
                int merchantId = order.getMerchantId() != null ? order.getMerchantId() : 0;
                List<OrderItemStats> itemStats = new ArrayList<>();

                for (CreateOrderItemRequest itemReq : request.getItems()) {
                    Product product = findProduct(itemReq.getProductId());

                    if (product.countInStock() < itemReq.getQuantity()) {
                        throw new IllegalArgumentException(
                                "Insufficient stock for product id=" + itemReq.getProductId());
                    }

                    OrderItem created = orderItemPort.create(new OrderItemPort.CreateData(
                            orderId,
                            itemReq.getProductId(),
                            itemReq.getQuantity(),
                            itemReq.getPrice()));

                    updateProductStock(product, itemReq.getQuantity());

                    int subtotal = itemReq.getQuantity() * itemReq.getPrice();
                    totalPrice += subtotal;

                    itemStats.add(new OrderItemStats(
                            created.id(),
                            order.getOrderId(),
                            merchantId,
                            product.categoryId(),
                            itemReq.getProductId(),
                            itemReq.getQuantity(),
                            itemReq.getPrice(),
                            subtotal));
                }

                createShipping(orderId, request.getShippingAddress());

                order.setTotalPrice(totalPrice);

                // Baris order + event outbox ditulis dalam satu transaksi MongoDB: event
                // ada jika dan hanya jika baris order ada. Kegagalan enqueue membatalkan
                // seluruh transaksi (bukan lagi ditelan dengan log.warn).
                transactionRunner.run(() -> {
                    orderCommandRepository.save(order);
                    enqueueStatsEvents(order, itemStats);
                });


                log.info("✅ Order created successfully with id={}", order.getOrderId());
                return ApiResponse.<OrderResponse>builder()
                        .status("success")
                        .message("Order created successfully")
                        .data(OrderResponse.from(order))
                        .build();

            } catch (Exception e) {
                log.error("💥 Failed to create order", e);
                return ApiResponse.<OrderResponse>builder()
                        .status("error")
                        .message("Failed to create your order. Please try again later.")
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<OrderResponse> update(UpdateOrderRequest request) {
        Attributes attrs = Attributes.builder()
                .put("order.id", String.valueOf(request.getOrderId()))
                .build();

        return tracingMetrics.traceAndMeasure("updateOrder", "update_order", attrs, () -> {
            try {
                log.info("🔄 Updating order id={}", request.getOrderId());

                Order order = orderQueryRepository.findOrderById(String.valueOf(request.getOrderId()))
                        .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

                ensureUser(request.getUserId());

                int orderId = Integer.parseInt(order.getOrderId());
                int totalPrice = 0;

                for (UpdateOrderItemRequest itemReq : request.getItems()) {
                    Product product = findProduct(itemReq.getProductId());

                    if (itemReq.getOrderItemId() != null && itemReq.getOrderItemId() > 0) {
                        ensureOrderItem(orderId, itemReq.getOrderItemId());
                        orderItemPort.update(new OrderItemPort.UpdateData(
                                itemReq.getOrderItemId(),
                                itemReq.getQuantity(),
                                itemReq.getPrice()));
                    } else {
                        if (product.countInStock() < itemReq.getQuantity()) {
                            throw new IllegalArgumentException(
                                    "Insufficient stock for product id=" + itemReq.getProductId());
                        }
                        orderItemPort.create(new OrderItemPort.CreateData(
                                orderId,
                                itemReq.getProductId(),
                                itemReq.getQuantity(),
                                itemReq.getPrice()));

                        updateProductStock(product, itemReq.getQuantity());
                    }

                    totalPrice += itemReq.getQuantity() * itemReq.getPrice();
                }

                updateShipping(orderId, request.getShippingAddress());

                order.setTotalPrice(totalPrice);
                orderCommandRepository.save(order);

                log.info("✅ Order updated successfully id={}", order.getOrderId());
                return ApiResponse.<OrderResponse>builder()
                        .status("success")
                        .message("Order updated successfully")
                        .data(OrderResponse.from(order))
                        .build();

            } catch (Exception e) {
                log.error("💥 Failed to update order id={}", request.getOrderId(), e);
                return ApiResponse.<OrderResponse>builder()
                        .status("error")
                        .message("Failed to update your order. Please try again later.")
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<OrderResponseDeleteAt> trash(Integer id) {
        Attributes attrs = Attributes.builder()
                .put("order.id", String.valueOf(id))
                .build();

        return tracingMetrics.traceAndMeasure("trashOrder", "trash_order", attrs, () -> {
            log.info("🗑️ Trashing order id={}", id);
            try {
                Order order = orderCommandRepository.trashed(String.valueOf(id));

                return ApiResponse.<OrderResponseDeleteAt>builder()
                        .status("success")
                        .message("🗑️ Order trashed successfully!")
                        .data(OrderResponseDeleteAt.from(order))
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to trash order id={}", id, e);
                return ApiResponse.<OrderResponseDeleteAt>builder()
                        .status("error")
                        .message("Failed to trash order: " + e.getMessage())
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<OrderResponseDeleteAt> restore(Integer id) {
        Attributes attrs = Attributes.builder()
                .put("order.id", String.valueOf(id))
                .build();

        return tracingMetrics.traceAndMeasure("restoreOrder", "restore_order", attrs, () -> {
            log.info("♻️ Restoring order id={}", id);
            try {
                Order order = orderCommandRepository.restore(String.valueOf(id));
                return ApiResponse.<OrderResponseDeleteAt>builder()
                        .status("success")
                        .message("♻️ Order restored successfully!")
                        .data(OrderResponseDeleteAt.from(order))
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to restore order id={}", id, e);
                return ApiResponse.<OrderResponseDeleteAt>builder()
                        .status("error")
                        .message("Failed to restore order: " + e.getMessage())
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<Boolean> delete(Integer id) {
        Attributes attrs = Attributes.builder()
                .put("order.id", String.valueOf(id))
                .build();

        return tracingMetrics.traceAndMeasure("deleteOrderPermanent", "delete_order_permanent", attrs, () -> {
            log.info("🧨 Permanently deleting order id={}", id);
            try {
                orderCommandRepository.deletePermanent(String.valueOf(id));
                return ApiResponse.<Boolean>builder()
                        .status("success")
                        .message("🧨 Order permanently deleted!")
                        .data(true)
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to permanently delete order id={}", id, e);
                return ApiResponse.<Boolean>builder()
                        .status("error")
                        .message("Failed to permanently delete order: " + e.getMessage())
                        .data(false)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<Boolean> restoreAll() {
        return tracingMetrics.traceAndMeasure("restoreAllOrders", "restore_all_orders", () -> {
            log.info("🔄 Restoring ALL trashed orders");
            try {
                orderCommandRepository.restoreAllDeleted();
                return ApiResponse.<Boolean>builder()
                        .status("success")
                        .message("🔄 All orders restored successfully!")
                        .data(true)
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to restore all orders", e);
                return ApiResponse.<Boolean>builder()
                        .status("error")
                        .message("Failed to restore all orders: " + e.getMessage())
                        .data(false)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<Boolean> deleteAll() {
        return tracingMetrics.traceAndMeasure("deleteAllOrdersPermanent", "delete_all_orders_permanent", () -> {
            log.info("💣 Permanently deleting ALL trashed orders");
            try {
                orderCommandRepository.deleteAllDeleted();
                return ApiResponse.<Boolean>builder()
                        .status("success")
                        .message("💣 All orders permanently deleted!")
                        .data(true)
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to delete all orders", e);
                return ApiResponse.<Boolean>builder()
                        .status("error")
                        .message("Failed to delete all orders: " + e.getMessage())
                        .data(false)
                        .build();
            }
        });
    }

    private void ensureMerchant(Integer merchantId) {
        merchantPort.findById(merchantId);
    }

    private void ensureUser(Integer userId) {
        userPort.findById(userId);
    }

    private Product findProduct(Integer productId) {
        try {
            return productPort.findById(productId);
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new ResourceNotFoundException("Product not found with id=" + productId);
            }
            throw e;
        }
    }

    private void updateProductStock(Product product, int quantity) {
        productPort.updateCountStock(product.id(), product.countInStock() - quantity);
    }

    private void ensureOrderItem(Integer orderId, Integer orderItemId) {
        try {
            List<OrderItem> items = orderItemPort.findOrderItemByOrder(orderId);
            boolean found = items.stream().anyMatch(i -> i.id() == orderItemId);
            if (!found) {
                throw new ResourceNotFoundException("Order item not found");
            }
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new ResourceNotFoundException("Order item not found");
            }
            throw e;
        }
    }

    private void createShipping(Integer orderId, CreateShippingAddressRequest shipping) {
        shippingPort.create(new ShippingAddressPort.CreateData(
                orderId,
                ProtoMappers.nvl(shipping.getAlamat()),
                ProtoMappers.nvl(shipping.getProvinsi()),
                ProtoMappers.nvl(shipping.getKota()),
                ProtoMappers.nvl(shipping.getNegara()),
                ProtoMappers.nvl(shipping.getCourier()),
                ProtoMappers.nvl(shipping.getShippingMethod()),
                shipping.getShippingCost() == null ? 0 : shipping.getShippingCost()));
    }

    private void updateShipping(Integer orderId, UpdateShippingAddressRequest shipping) {
        ensureShipping(shipping.getShippingId());
        shippingPort.update(new ShippingAddressPort.UpdateData(
                shipping.getShippingId(),
                ProtoMappers.nvl(shipping.getAlamat()),
                ProtoMappers.nvl(shipping.getProvinsi()),
                ProtoMappers.nvl(shipping.getKota()),
                ProtoMappers.nvl(shipping.getNegara()),
                ProtoMappers.nvl(shipping.getCourier()),
                ProtoMappers.nvl(shipping.getShippingMethod()),
                shipping.getShippingCost() == null ? 0 : shipping.getShippingCost(),
                orderId));
    }

    private void ensureShipping(Integer shippingId) {
        try {
            if (shippingPort.findById(shippingId) == null) {
                throw new ResourceNotFoundException("Shipping address not found");
            }
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new ResourceNotFoundException("Shipping address not found");
            }
            throw e;
        }
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

    private void enqueueStatsEvents(Order order, List<OrderItemStats> items) {
        enqueueOrderStatsEvent(order);
        for (OrderItemStats item : items) {
            enqueueOrderItemStatsEvent(item);
        }
    }

    private void enqueueOrderStatsEvent(Order order) {
        String eventId = UUID.randomUUID().toString();
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("order_id", order.getOrderId() != null ? order.getOrderId() : "");
        payload.put("merchant_id", order.getMerchantId() != null ? order.getMerchantId() : 0);
        payload.put("status", "completed");
        payload.put("total_amount", order.getTotalPrice() != null ? order.getTotalPrice().doubleValue() : 0.0);
        enqueueOutbox("stats.ecommerce.order.event",
                order.getOrderId() != null ? order.getOrderId() : "", payload, "order.created", eventId);
    }

    private void enqueueOrderItemStatsEvent(OrderItemStats item) {
        String eventId = UUID.randomUUID().toString();
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("order_item_id", item.orderItemId());
        payload.put("order_id", item.orderId());
        payload.put("merchant_id", item.merchantId());
        payload.put("category_id", item.categoryId());
        payload.put("product_id", item.productId());
        payload.put("quantity", item.quantity());
        payload.put("unit_price", (double) item.unitPrice());
        payload.put("subtotal", (double) item.subtotal());
        enqueueOutbox("stats.ecommerce.order_item.event", item.orderId(), payload, "order_item.created", eventId);
    }

    private void enqueueOutbox(String topic, String key, Map<String, Object> payload, String eventType, String eventId) {
        Map<String, Object> envelope = new LinkedHashMap<>();
        envelope.put("event_id", eventId);
        envelope.put("event_type", eventType);
        envelope.put("occurred_at", Instant.now().toString());
        envelope.put("schema_version", 1);
        envelope.put("payload", payload);

        OrderOutbox event = new OrderOutbox();
        event.setId(eventId);
        event.setTopic(topic);
        event.setEventKey(key);
        event.setPayload(serialize(envelope));
        orderOutboxRepository.enqueue(event);
    }

    private static String serialize(Object value) {
        try {
            return MAPPER.writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException("failed to serialize stats envelope", e);
        }
    }
}
