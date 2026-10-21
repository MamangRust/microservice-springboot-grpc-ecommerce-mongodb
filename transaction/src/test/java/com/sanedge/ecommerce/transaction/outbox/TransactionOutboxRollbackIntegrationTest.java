package com.sanedge.ecommerce.transaction.outbox;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.sanedge.ecommerce.BaseIntegrationTest;
import com.sanedge.ecommerce.common.adapter.merchant.MerchantPort;
import com.sanedge.ecommerce.common.adapter.model.Merchant;
import com.sanedge.ecommerce.common.adapter.model.Order;
import com.sanedge.ecommerce.common.adapter.model.OrderItem;
import com.sanedge.ecommerce.common.adapter.model.ShippingAddress;
import com.sanedge.ecommerce.common.adapter.model.User;
import com.sanedge.ecommerce.common.adapter.order.OrderPort;
import com.sanedge.ecommerce.common.adapter.order_item.OrderItemPort;
import com.sanedge.ecommerce.common.adapter.shipping_address.ShippingAddressPort;
import com.sanedge.ecommerce.common.adapter.user.UserPort;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.transaction.domain.requests.CreateTransactionRequest;
import com.sanedge.ecommerce.transaction.domain.responses.TransactionResponse;
import com.sanedge.ecommerce.transaction.models.Transaction;
import com.sanedge.ecommerce.transaction.repository.TransactionOutboxRepository;
import com.sanedge.ecommerce.transaction.service.TransactionCommandService;

import jakarta.validation.Validator;

/**
 * When the outbox insert fails, the whole transaction is rolled back: tidak ada transaction yang
 * tersisa tanpa event.
 */
class TransactionOutboxRollbackIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TransactionCommandService commandService;

    @MockitoBean
    private TransactionOutboxRepository transactionOutboxRepository;

    @MockitoBean
    private Validator validator;

    @MockitoBean
    private MerchantPort merchantPort;

    @MockitoBean
    private OrderPort orderPort;

    @MockitoBean
    private OrderItemPort orderItemPort;

    @MockitoBean
    private ShippingAddressPort shippingPort;

    @MockitoBean
    private UserPort userPort;

    @BeforeEach
    void setupMocks() {
        when(merchantPort.findById(any(Integer.class)))
                .thenReturn(new Merchant(1, 7, "Merchant", "desc", "addr", "m@example.com", "0800",
                        "active", Instant.now(), Instant.now()));

        when(orderPort.findById(any(Integer.class)))
                .thenReturn(new Order(1, 1, 7, 35000, Instant.now(), Instant.now()));

        when(userPort.findById(any(Integer.class)))
                .thenReturn(new User(7, "Ada", "Lovelace", "ada@example.com", null, Instant.now(), Instant.now()));

        when(orderItemPort.findOrderItemByOrder(any(Integer.class)))
                .thenReturn(List.of(new OrderItem(1, 1, 101, 2, 15000, Instant.now(), Instant.now())));

        when(shippingPort.findByOrder(any(Integer.class)))
                .thenReturn(new ShippingAddress(1, 1, "Jl. Merdeka 1", "DKI", "Indonesia", "Jakarta",
                        "REG", 15000, Instant.now(), Instant.now()));

        doThrow(new IllegalStateException("outbox insert failed"))
                .when(transactionOutboxRepository).enqueue(any());
    }

    @Test
    void transactionRowIsRolledBackWhenTheOutboxInsertFails() {
        CreateTransactionRequest req = new CreateTransactionRequest();
        req.setOrderID(1);
        req.setMerchantID(1);
        req.setPaymentMethod("CREDIT_CARD");
        req.setAmount(100000);
        req.setPaymentStatus("PENDING");

        ApiResponse<TransactionResponse> response = commandService.create(req);

        assertThat(response.getStatus()).isEqualTo("error");
        assertThat(mongoTemplate.find(new Query(), Transaction.class)).isEmpty();
    }
}
