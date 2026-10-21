package com.sanedge.ecommerce.transaction.config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sanedge.ecommerce.common.adapter.merchant.MerchantAdapter;
import com.sanedge.ecommerce.common.adapter.merchant.MerchantPort;
import com.sanedge.ecommerce.common.adapter.order.OrderAdapter;
import com.sanedge.ecommerce.common.adapter.order.OrderPort;
import com.sanedge.ecommerce.common.adapter.order_item.OrderItemAdapter;
import com.sanedge.ecommerce.common.adapter.order_item.OrderItemPort;
import com.sanedge.ecommerce.common.adapter.shipping_address.ShippingAddressAdapter;
import com.sanedge.ecommerce.common.adapter.user.UserAdapter;
import com.sanedge.ecommerce.common.adapter.user.UserPort;
import com.sanedge.ecommerce.common.adapter.shipping_address.ShippingAddressPort;
import com.sanedge.ecommerce.common.resilience.DependencyGuardInterceptor;
import com.sanedge.ecommerce.pb.merchant.MerchantQueryServiceGrpc;
import com.sanedge.ecommerce.pb.order.OrderQueryServiceGrpc;
import com.sanedge.ecommerce.pb.order_item.OrderItemCommandServiceGrpc;
import com.sanedge.ecommerce.pb.order_item.OrderItemQueryServiceGrpc;
import com.sanedge.ecommerce.pb.shipping_address.ShippingCommandServiceGrpc;
import com.sanedge.ecommerce.pb.shipping_address.ShippingQueryServiceGrpc;
import com.sanedge.ecommerce.pb.user.UserCommandServiceGrpc;
import com.sanedge.ecommerce.pb.user.UserQueryServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import jakarta.annotation.PreDestroy;

/**
 * One plaintext {@link ManagedChannel} per downstream domain service plus the
 * blocking stubs used by the transaction command flow.
 */
@Configuration
public class GrpcClientConfig {

    @Value("${grpc.client.merchant.host:localhost}")
    private String merchantHost;
    @Value("${grpc.client.merchant.port:9103}")
    private int merchantPort;

    @Value("${grpc.client.order.host:localhost}")
    private String orderHost;
    @Value("${grpc.client.order.port:9114}")
    private int orderPort;

    @Value("${grpc.client.order-item.host:localhost}")
    private String orderItemHost;
    @Value("${grpc.client.order-item.port:9115}")
    private int orderItemPort;

    @Value("${grpc.client.shipping-address.host:localhost}")
    private String shippingAddressHost;
    @Value("${grpc.client.shipping-address.port:9118}")
    private int shippingAddressPort;

    @Value("${grpc.client.user.host:localhost}")
    private String userHost;
    @Value("${grpc.client.user.port:9102}")
    private int userPort;

    private final List<ManagedChannel> channels = new ArrayList<>();

    private final DependencyGuardInterceptor dependencyGuardInterceptor;

    public GrpcClientConfig(DependencyGuardInterceptor dependencyGuardInterceptor) {
        this.dependencyGuardInterceptor = dependencyGuardInterceptor;
    }

    private ManagedChannel channel(String host, int port) {
        ManagedChannel channel = NettyChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .intercept(dependencyGuardInterceptor)
                .build();
        channels.add(channel);
        return channel;
    }

    @Bean
    public MerchantQueryServiceGrpc.MerchantQueryServiceBlockingStub merchantQueryStub() {
        return MerchantQueryServiceGrpc.newBlockingStub(channel(merchantHost, merchantPort));
    }

    @Bean
    public OrderQueryServiceGrpc.OrderQueryServiceBlockingStub orderQueryStub() {
        return OrderQueryServiceGrpc.newBlockingStub(channel(orderHost, orderPort));
    }

    @Bean
    public OrderItemQueryServiceGrpc.OrderItemQueryServiceBlockingStub orderItemQueryStub() {
        return OrderItemQueryServiceGrpc.newBlockingStub(channel(orderItemHost, orderItemPort));
    }

    @Bean
    public OrderItemCommandServiceGrpc.OrderItemCommandServiceBlockingStub orderItemCommandStub() {
        return OrderItemCommandServiceGrpc.newBlockingStub(channel(orderItemHost, orderItemPort));
    }

    @Bean
    public ShippingQueryServiceGrpc.ShippingQueryServiceBlockingStub shippingQueryStub() {
        return ShippingQueryServiceGrpc.newBlockingStub(channel(shippingAddressHost, shippingAddressPort));
    }

    @Bean
    public ShippingCommandServiceGrpc.ShippingCommandServiceBlockingStub shippingCommandStub() {
        return ShippingCommandServiceGrpc.newBlockingStub(channel(shippingAddressHost, shippingAddressPort));
    }

    @Bean
    public UserQueryServiceGrpc.UserQueryServiceBlockingStub userQueryStub() {
        return UserQueryServiceGrpc.newBlockingStub(channel(userHost, userPort));
    }

    @Bean
    public UserCommandServiceGrpc.UserCommandServiceBlockingStub userCommandStub() {
        return UserCommandServiceGrpc.newBlockingStub(channel(userHost, userPort));
    }

    @Bean
    public MerchantPort merchantPort(MerchantQueryServiceGrpc.MerchantQueryServiceBlockingStub merchantQueryStub) {
        return new MerchantAdapter(merchantQueryStub);
    }

    @Bean
    public OrderPort orderPort(OrderQueryServiceGrpc.OrderQueryServiceBlockingStub orderQueryStub) {
        return new OrderAdapter(orderQueryStub);
    }

    @Bean
    public OrderItemPort orderItemPort(OrderItemQueryServiceGrpc.OrderItemQueryServiceBlockingStub orderItemQueryStub,
            OrderItemCommandServiceGrpc.OrderItemCommandServiceBlockingStub orderItemCommandStub) {
        return new OrderItemAdapter(orderItemQueryStub, orderItemCommandStub);
    }

    @Bean
    public ShippingAddressPort shippingAddressPort(
            ShippingQueryServiceGrpc.ShippingQueryServiceBlockingStub shippingQueryStub,
            ShippingCommandServiceGrpc.ShippingCommandServiceBlockingStub shippingCommandStub) {
        return new ShippingAddressAdapter(shippingQueryStub, shippingCommandStub);
    }

    /**
     * UserPort untuk event email transaksi (pemilik akun). Tanpa bean ini context modul
     * transaction tidak bisa start karena {@code TransactionCommandImplService} butuh
     * {@code UserPort}.
     */
    @Bean
    public UserPort userPort(UserQueryServiceGrpc.UserQueryServiceBlockingStub userQueryStub,
            UserCommandServiceGrpc.UserCommandServiceBlockingStub userCommandStub) {
        return new UserAdapter(userQueryStub, userCommandStub);
    }

    @PreDestroy
    public void shutdown() {
        for (ManagedChannel channel : channels) {
            try {
                channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                channel.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}
