package com.sanedge.ecommerce.order.outbox;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.sanedge.ecommerce.BaseIntegrationTest;
import com.sanedge.ecommerce.common.adapter.merchant.MerchantPort;
import com.sanedge.ecommerce.common.adapter.model.Merchant;
import com.sanedge.ecommerce.common.adapter.model.OrderItem;
import com.sanedge.ecommerce.common.adapter.model.Product;
import com.sanedge.ecommerce.common.adapter.model.User;
import com.sanedge.ecommerce.common.adapter.order_item.OrderItemPort;
import com.sanedge.ecommerce.common.adapter.product.ProductPort;
import com.sanedge.ecommerce.common.adapter.shipping_address.ShippingAddressPort;
import com.sanedge.ecommerce.common.adapter.user.UserPort;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.order.domain.requests.order.CreateOrderItemRequest;
import com.sanedge.ecommerce.order.domain.requests.order.CreateOrderRequest;
import com.sanedge.ecommerce.order.domain.requests.shipping.CreateShippingAddressRequest;
import com.sanedge.ecommerce.order.domain.responses.order.OrderResponse;
import com.sanedge.ecommerce.order.models.Order;
import com.sanedge.ecommerce.order.models.OrderOutbox;
import com.sanedge.ecommerce.order.service.OrderCommandService;

import jakarta.validation.Validator;

/**
 * Baris order dan event outbox ditulis dalam satu transaksi MongoDB: keduanya ada atau tidak
 * ada sama sekali.
 */
class OrderOutboxAtomicityIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private OrderCommandService commandService;

    @MockitoBean
    private Validator validator;

    @MockitoBean
    private MerchantPort merchantPort;

    @MockitoBean
    private UserPort userPort;

    @MockitoBean
    private ProductPort productPort;

    @MockitoBean
    private OrderItemPort orderItemPort;

    @MockitoBean
    private ShippingAddressPort shippingPort;

    @BeforeEach
    void setupMocks() {
        when(merchantPort.findById(any(Integer.class)))
                .thenReturn(new Merchant(1, 7, "Merchant", "desc", "addr", "m@example.com", "0800",
                        "active", Instant.now(), Instant.now()));

        when(userPort.findById(any(Integer.class)))
                .thenReturn(new User(7, "Ada", "Lovelace", "ada@example.com", null, Instant.now(), Instant.now()));

        when(productPort.findById(any(Integer.class)))
                .thenReturn(new Product(101, 1, 5, "Product", "desc", 15000, 10, "brand", 1, 4.5f,
                        "slug", "img", Instant.now(), Instant.now()));

        when(productPort.updateCountStock(any(Integer.class), any(Integer.class)))
                .thenReturn(new Product(101, 1, 5, "Product", "desc", 15000, 8, "brand", 1, 4.5f,
                        "slug", "img", Instant.now(), Instant.now()));

        when(orderItemPort.create(any(OrderItemPort.CreateData.class)))
                .thenReturn(new OrderItem(1, 1, 101, 2, 15000, Instant.now(), Instant.now()));

        when(shippingPort.create(any(ShippingAddressPort.CreateData.class)))
                .thenReturn(null);
    }

    private CreateOrderRequest request() {
        CreateOrderItemRequest item = new CreateOrderItemRequest();
        item.setProductId(101);
        item.setQuantity(2);
        item.setPrice(15000);

        List<CreateOrderItemRequest> items = new ArrayList<>();
        items.add(item);

        CreateOrderRequest req = new CreateOrderRequest();
        req.setMerchantId(1);
        req.setUserId(7);
        req.setItems(items);
        req.setShippingAddress(shippingAddress());
        return req;
    }

    private CreateShippingAddressRequest shippingAddress() {
        CreateShippingAddressRequest shipping = new CreateShippingAddressRequest();
        shipping.setAlamat("Jl. Merdeka 1");
        shipping.setProvinsi("DKI Jakarta");
        shipping.setKota("Jakarta");
        shipping.setNegara("Indonesia");
        shipping.setCourier("JNE");
        shipping.setShippingMethod("REG");
        shipping.setShippingCost(15000);
        return shipping;
    }

    @Test
    void orderRowAndOutboxEventsAreCommittedTogether() {
        ApiResponse<OrderResponse> response = commandService.create(request());

        assertThat(response.getStatus()).isEqualTo("success");

        assertThat(mongoTemplate.find(new org.springframework.data.mongodb.core.query.Query(), Order.class))
                .hasSize(1);

        List<OrderOutbox> events = mongoTemplate.find(new org.springframework.data.mongodb.core.query.Query(),
                OrderOutbox.class);
        assertThat(events).hasSize(2);
        assertThat(events).extracting(OrderOutbox::getTopic)
                .containsExactlyInAnyOrder("stats.ecommerce.order.event", "stats.ecommerce.order_item.event");
        assertThat(events).allSatisfy(event -> {
            assertThat(event.getStatus()).isEqualTo("PENDING");
            assertThat(event.getPayload()).contains("event_id");
        });
    }
}
