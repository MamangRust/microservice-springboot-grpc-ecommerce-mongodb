package com.sanedge.ecommerce.common.adapter.order_item;

import java.util.List;

import com.sanedge.ecommerce.common.adapter.model.OrderItem;
import com.sanedge.ecommerce.common.adapter.support.Paged;

/**
 * Port order item: baca + tulis + enumerasi. Setara
 * {@code order_item.QueryRepository}/{@code CommandRepository}/{@code BulkRepository}
 * di Go.
 */
public interface OrderItemPort {

    List<OrderItem> findOrderItemByOrder(int orderId);

    Integer calculateTotalPrice(int orderId);

    OrderItem create(CreateData data);

    OrderItem update(UpdateData data);

    OrderItem trash(int orderItemId);

    OrderItem restore(int orderItemId);

    Boolean deletePermanent(int orderItemId);

    Boolean deleteByOrderIdPermanent(int orderId);

    /** Rollback kompensasi: hapus item milik order (dipakai saat create order gagal). */
    Boolean deleteByOrderIdRollback(int orderId);

    Boolean restoreAll();

    Boolean deleteAll();

    Paged<OrderItem> findAll(int page, int pageSize);

    record CreateData(int orderId, int productId, int quantity, int price) {
    }

    record UpdateData(int orderItemId, int quantity, int price) {
    }
}
