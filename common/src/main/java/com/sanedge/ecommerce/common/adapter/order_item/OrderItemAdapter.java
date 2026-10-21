package com.sanedge.ecommerce.common.adapter.order_item;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.Empty;
import com.sanedge.ecommerce.common.adapter.model.OrderItem;
import com.sanedge.ecommerce.common.adapter.support.AdapterException;
import com.sanedge.ecommerce.common.adapter.support.Paged;
import com.sanedge.ecommerce.common.adapter.support.ProtoTime;
import com.sanedge.ecommerce.pb.order_item.ApiResponseOrderItem;
import com.sanedge.ecommerce.pb.order_item.CalculateTotalPriceRequest;
import com.sanedge.ecommerce.pb.order_item.CreateOrderItemRecordRequest;
import com.sanedge.ecommerce.pb.order_item.FindAllOrderItemRequest;
import com.sanedge.ecommerce.pb.order_item.FindByIdOrderItemRequest;
import com.sanedge.ecommerce.pb.order_item.OrderItemCommandServiceGrpc.OrderItemCommandServiceBlockingStub;
import com.sanedge.ecommerce.pb.order_item.OrderItemQueryServiceGrpc.OrderItemQueryServiceBlockingStub;
import com.sanedge.ecommerce.pb.order_item.OrderItemResponse;
import com.sanedge.ecommerce.pb.order_item.UpdateOrderItemRecordRequest;

public class OrderItemAdapter implements OrderItemPort {

    private final OrderItemQueryServiceBlockingStub query;

    private final OrderItemCommandServiceBlockingStub command;

    public OrderItemAdapter(OrderItemQueryServiceBlockingStub query, OrderItemCommandServiceBlockingStub command) {
        this.query = query;
        this.command = command;
    }

    @Override
    public List<OrderItem> findOrderItemByOrder(int orderId) {
        var resp = query.findOrderItemByOrder(FindByIdOrderItemRequest.newBuilder().setId(orderId).build());
        List<OrderItem> items = new ArrayList<>(resp.getDataList().size());
        for (OrderItemResponse item : resp.getDataList()) {
            items.add(toModel(item));
        }
        return items;
    }

    @Override
    public Integer calculateTotalPrice(int orderId) {
        var resp = command.calculateTotalPrice(CalculateTotalPriceRequest.newBuilder().setOrderId(orderId).build());
        return resp.getTotalPrice();
    }

    @Override
    public OrderItem create(CreateData data) {
        var resp = command.createOrderItem(CreateOrderItemRecordRequest.newBuilder()
                .setOrderId(data.orderId())
                .setProductId(data.productId())
                .setQuantity(data.quantity())
                .setPrice(data.price())
                .build());
        return requireData(resp, "Failed to create order item");
    }

    @Override
    public OrderItem update(UpdateData data) {
        var resp = command.updateOrderItem(UpdateOrderItemRecordRequest.newBuilder()
                .setOrderItemId(data.orderItemId())
                .setQuantity(data.quantity())
                .setPrice(data.price())
                .build());
        return requireData(resp, "Failed to update order item " + data.orderItemId());
    }

    @Override
    public OrderItem trash(int orderItemId) {
        var resp = command.trashOrderItem(FindByIdOrderItemRequest.newBuilder().setId(orderItemId).build());
        return requireData(resp, "Failed to trash order item " + orderItemId);
    }

    @Override
    public OrderItem restore(int orderItemId) {
        var resp = command.restoreOrderItem(FindByIdOrderItemRequest.newBuilder().setId(orderItemId).build());
        return requireData(resp, "Failed to restore order item " + orderItemId);
    }

    @Override
    public Boolean deletePermanent(int orderItemId) {
        var resp = command.deleteOrderItemPermanent(FindByIdOrderItemRequest.newBuilder().setId(orderItemId).build());
        return "success".equals(resp.getStatus());
    }

    @Override
    public Boolean deleteByOrderIdPermanent(int orderId) {
        var resp = command.deleteOrderItemByOrderPermanent(FindByIdOrderItemRequest.newBuilder().setId(orderId).build());
        return "success".equals(resp.getStatus());
    }

    @Override
    public Boolean deleteByOrderIdRollback(int orderId) {
        var resp = command.deleteOrderItemByOrderRollback(FindByIdOrderItemRequest.newBuilder().setId(orderId).build());
        return "success".equals(resp.getStatus());
    }

    @Override
    public Boolean restoreAll() {
        var resp = command.restoreAllOrdersItem(Empty.getDefaultInstance());
        return "success".equals(resp.getStatus());
    }

    @Override
    public Boolean deleteAll() {
        var resp = command.deleteAllPermanentOrdersItem(Empty.getDefaultInstance());
        return "success".equals(resp.getStatus());
    }

    @Override
    public Paged<OrderItem> findAll(int page, int pageSize) {
        var resp = query.findAll(FindAllOrderItemRequest.newBuilder()
                .setPage(page)
                .setPageSize(pageSize)
                .build());
        List<OrderItem> items = new ArrayList<>(resp.getDataList().size());
        for (OrderItemResponse item : resp.getDataList()) {
            items.add(toModel(item));
        }
        int total = resp.hasPagination() ? resp.getPagination().getTotalRecords() : items.size();
        return Paged.of(items, total);
    }

    private static OrderItem requireData(ApiResponseOrderItem resp, String message) {
        if (resp == null || !resp.hasData()) {
            throw new AdapterException(message);
        }
        return toModel(resp.getData());
    }

    static OrderItem toModel(OrderItemResponse item) {
        if (item == null) {
            return null;
        }
        return new OrderItem(
                item.getId(),
                item.getOrderId(),
                item.getProductId(),
                item.getQuantity(),
                item.getPrice(),
                ProtoTime.parse(item.getCreatedAt()),
                ProtoTime.parse(item.getUpdatedAt()));
    }
}
