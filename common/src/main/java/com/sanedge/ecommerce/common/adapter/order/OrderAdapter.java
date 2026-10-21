package com.sanedge.ecommerce.common.adapter.order;

import java.util.ArrayList;
import java.util.List;

import com.sanedge.ecommerce.common.adapter.model.Order;
import com.sanedge.ecommerce.common.adapter.support.Paged;
import com.sanedge.ecommerce.common.adapter.support.ProtoTime;
import com.sanedge.ecommerce.pb.order.FindAllOrderRequest;
import com.sanedge.ecommerce.pb.order.FindByIdOrderRequest;
import com.sanedge.ecommerce.pb.order.OrderQueryServiceGrpc.OrderQueryServiceBlockingStub;
import com.sanedge.ecommerce.pb.order.OrderResponse;

public class OrderAdapter implements OrderPort {

    private final OrderQueryServiceBlockingStub client;

    public OrderAdapter(OrderQueryServiceBlockingStub client) {
        this.client = client;
    }

    @Override
    public Order findById(int orderId) {
        var resp = client.findById(FindByIdOrderRequest.newBuilder().setId(orderId).build());
        return toModel(resp.getData());
    }

    @Override
    public Paged<Order> findAll(int page, int pageSize) {
        var resp = client.findAll(FindAllOrderRequest.newBuilder()
                .setPage(page)
                .setPageSize(pageSize)
                .build());
        List<Order> items = new ArrayList<>(resp.getDataList().size());
        for (OrderResponse o : resp.getDataList()) {
            items.add(toModel(o));
        }
        int total = resp.hasPagination() ? resp.getPagination().getTotalRecords() : items.size();
        return Paged.of(items, total);
    }

    static Order toModel(OrderResponse o) {
        if (o == null) {
            return null;
        }
        return new Order(
                o.getId(),
                o.getMerchantId(),
                o.getUserId(),
                o.getTotalPrice(),
                ProtoTime.parse(o.getCreatedAt()),
                ProtoTime.parse(o.getUpdatedAt()));
    }
}
