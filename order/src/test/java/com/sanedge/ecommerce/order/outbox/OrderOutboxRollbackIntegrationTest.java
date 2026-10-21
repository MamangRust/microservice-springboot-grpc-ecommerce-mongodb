package com.sanedge.ecommerce.order.outbox;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
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
import com.sanedge.ecommerce.order.repository.OrderOutboxRepository;
import com.sanedge.ecommerce.order.service.OrderCommandService;

import jakarta.validation.Validator;

/**
 * When the outbox insert fails, the whole transaction is rolled back: tidak ada order yang
 * tersisa tanpa event.
 */
class OrderOutboxRollbackIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private OrderCommandService commandService;

    @MockitoBean
    private OrderOutboxRepository orderOutboxRepository;

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

        doThrow(new IllegalStateException("outbox insert failed"))
                .when(orderOutboxRepository).enqueue(any());
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
    void orderRowIsRolledBackWhenTheOutboxInsertFails() {
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

        ApiResponse<OrderResponse> response = commandService.create(req);

        assertThat(response.getStatus()).isEqualTo("error");
        assertThat(mongoTemplate.find(new Query(), Order.class)).isEmpty();
    }
}
