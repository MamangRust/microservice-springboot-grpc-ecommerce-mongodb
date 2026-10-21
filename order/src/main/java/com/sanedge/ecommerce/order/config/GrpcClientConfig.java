package com.sanedge.ecommerce.order.config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sanedge.ecommerce.common.adapter.merchant.MerchantAdapter;
import com.sanedge.ecommerce.common.adapter.merchant.MerchantPort;
import com.sanedge.ecommerce.common.adapter.order_item.OrderItemAdapter;
import com.sanedge.ecommerce.common.adapter.order_item.OrderItemPort;
import com.sanedge.ecommerce.common.adapter.product.ProductAdapter;
import com.sanedge.ecommerce.common.adapter.product.ProductPort;
import com.sanedge.ecommerce.common.adapter.shipping_address.ShippingAddressAdapter;
import com.sanedge.ecommerce.common.adapter.shipping_address.ShippingAddressPort;
import com.sanedge.ecommerce.common.adapter.user.UserAdapter;
import com.sanedge.ecommerce.common.adapter.user.UserPort;
import com.sanedge.ecommerce.common.resilience.DependencyGuardInterceptor;
import com.sanedge.ecommerce.pb.merchant.MerchantQueryServiceGrpc;
import com.sanedge.ecommerce.pb.order_item.OrderItemCommandServiceGrpc;
import com.sanedge.ecommerce.pb.order_item.OrderItemQueryServiceGrpc;
import com.sanedge.ecommerce.pb.product.ProductCommandServiceGrpc;
import com.sanedge.ecommerce.pb.product.ProductQueryServiceGrpc;
import com.sanedge.ecommerce.pb.shipping_address.ShippingCommandServiceGrpc;
import com.sanedge.ecommerce.pb.shipping_address.ShippingQueryServiceGrpc;
import com.sanedge.ecommerce.pb.user.UserCommandServiceGrpc;
import com.sanedge.ecommerce.pb.user.UserQueryServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import jakarta.annotation.PreDestroy;

/**
 * One plaintext {@link ManagedChannel} per downstream domain service plus the
 * blocking stubs used by the order command flow.
 */
@Configuration
public class GrpcClientConfig {

    @Value("${grpc.client.merchant.host:localhost}")
    private String merchantHost;
    @Value("${grpc.client.merchant.port:9103}")
    private int merchantPort;

    @Value("${grpc.client.product.host:localhost}")
    private String productHost;
    @Value("${grpc.client.product.port:9110}")
    private int productPort;

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
    public UserQueryServiceGrpc.UserQueryServiceBlockingStub userQueryStub() {
        return UserQueryServiceGrpc.newBlockingStub(channel(userHost, userPort));
    }

    @Bean
    public UserCommandServiceGrpc.UserCommandServiceBlockingStub userCommandStub() {
        return UserCommandServiceGrpc.newBlockingStub(channel(userHost, userPort));
    }

    @Bean
    public ProductQueryServiceGrpc.ProductQueryServiceBlockingStub productQueryStub() {
        return ProductQueryServiceGrpc.newBlockingStub(channel(productHost, productPort));
    }

    @Bean
    public ProductCommandServiceGrpc.ProductCommandServiceBlockingStub productCommandStub() {
        return ProductCommandServiceGrpc.newBlockingStub(channel(productHost, productPort));
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
    public MerchantPort merchantPort(MerchantQueryServiceGrpc.MerchantQueryServiceBlockingStub merchantQueryStub) {
        return new MerchantAdapter(merchantQueryStub);
    }

    @Bean
    public UserPort userPort(UserQueryServiceGrpc.UserQueryServiceBlockingStub userQueryStub,
            UserCommandServiceGrpc.UserCommandServiceBlockingStub userCommandStub) {
        return new UserAdapter(userQueryStub, userCommandStub);
    }

    @Bean
    public ProductPort productPort(ProductQueryServiceGrpc.ProductQueryServiceBlockingStub productQueryStub,
            ProductCommandServiceGrpc.ProductCommandServiceBlockingStub productCommandStub) {
        return new ProductAdapter(productQueryStub, productCommandStub);
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
