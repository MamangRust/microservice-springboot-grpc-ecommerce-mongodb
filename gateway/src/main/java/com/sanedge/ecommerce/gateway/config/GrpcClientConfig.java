package com.sanedge.ecommerce.gateway.config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sanedge.ecommerce.pb.auth.AuthServiceGrpc;
import com.sanedge.ecommerce.pb.banner.BannerCommandServiceGrpc;
import com.sanedge.ecommerce.pb.banner.BannerQueryServiceGrpc;
import com.sanedge.ecommerce.pb.cart.CartCommandServiceGrpc;
import com.sanedge.ecommerce.pb.cart.CartQueryServiceGrpc;
import com.sanedge.ecommerce.pb.category.CategoryCommandServiceGrpc;
import com.sanedge.ecommerce.pb.category.CategoryQueryServiceGrpc;
import com.sanedge.ecommerce.pb.merchant.MerchantCommandServiceGrpc;
import com.sanedge.ecommerce.pb.merchant.MerchantQueryServiceGrpc;
import com.sanedge.ecommerce.pb.merchant_award.MerchantAwardCommandServiceGrpc;
import com.sanedge.ecommerce.pb.merchant_award.MerchantAwardQueryServiceGrpc;
import com.sanedge.ecommerce.pb.merchant_business.MerchantBusinessCommandServiceGrpc;
import com.sanedge.ecommerce.pb.merchant_business.MerchantBusinessQueryServiceGrpc;
import com.sanedge.ecommerce.pb.merchant_detail.MerchantDetailCommandServiceGrpc;
import com.sanedge.ecommerce.pb.merchant_detail.MerchantDetailQueryServiceGrpc;
import com.sanedge.ecommerce.pb.merchant_policy.MerchantPolicyCommandServiceGrpc;
import com.sanedge.ecommerce.pb.merchant_policy.MerchantPolicyQueryServiceGrpc;
import com.sanedge.ecommerce.pb.merchant_social_link.MerchantSocialCommandServiceGrpc;
import com.sanedge.ecommerce.pb.order.OrderCommandServiceGrpc;
import com.sanedge.ecommerce.pb.order.OrderQueryServiceGrpc;
import com.sanedge.ecommerce.pb.order_item.OrderItemCommandServiceGrpc;
import com.sanedge.ecommerce.pb.order_item.OrderItemQueryServiceGrpc;
import com.sanedge.ecommerce.pb.product.ProductCommandServiceGrpc;
import com.sanedge.ecommerce.pb.product.ProductQueryServiceGrpc;
import com.sanedge.ecommerce.pb.review.ReviewCommandServiceGrpc;
import com.sanedge.ecommerce.pb.review.ReviewQueryServiceGrpc;
import com.sanedge.ecommerce.pb.review_detail.ReviewDetailCommandServiceGrpc;
import com.sanedge.ecommerce.pb.review_detail.ReviewDetailQueryServiceGrpc;
import com.sanedge.ecommerce.pb.role.RoleCommandServiceGrpc;
import com.sanedge.ecommerce.pb.role.RoleQueryServiceGrpc;
import com.sanedge.ecommerce.pb.shipping_address.ShippingCommandServiceGrpc;
import com.sanedge.ecommerce.pb.shipping_address.ShippingQueryServiceGrpc;
import com.sanedge.ecommerce.pb.slider.SliderCommandServiceGrpc;
import com.sanedge.ecommerce.pb.slider.SliderQueryServiceGrpc;
import com.sanedge.ecommerce.pb.stats.StatsReaderServiceGrpc;
import com.sanedge.ecommerce.pb.transaction.TransactionCommandServiceGrpc;
import com.sanedge.ecommerce.pb.transaction.TransactionQueryServiceGrpc;
import com.sanedge.ecommerce.pb.user.UserCommandServiceGrpc;
import com.sanedge.ecommerce.pb.user.UserQueryServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import jakarta.annotation.PreDestroy;

/**
 * One plaintext {@link ManagedChannel} per downstream domain service plus the
 * blocking stubs used by the REST controllers.
 */
@Configuration
public class GrpcClientConfig {

    @Value("${grpc.client.role.host:localhost}")
    private String roleHost;
    @Value("${grpc.client.role.port:9101}")
    private int rolePort;

    @Value("${grpc.client.user.host:localhost}")
    private String userHost;
    @Value("${grpc.client.user.port:9102}")
    private int userPort;

    @Value("${grpc.client.merchant.host:localhost}")
    private String merchantHost;
    @Value("${grpc.client.merchant.port:9103}")
    private int merchantPort;

    @Value("${grpc.client.merchant-award.host:localhost}")
    private String merchantAwardHost;
    @Value("${grpc.client.merchant-award.port:9104}")
    private int merchantAwardPort;

    @Value("${grpc.client.merchant-business.host:localhost}")
    private String merchantBusinessHost;
    @Value("${grpc.client.merchant-business.port:9105}")
    private int merchantBusinessPort;

    @Value("${grpc.client.merchant-detail.host:localhost}")
    private String merchantDetailHost;
    @Value("${grpc.client.merchant-detail.port:9106}")
    private int merchantDetailPort;

    @Value("${grpc.client.merchant-policy.host:localhost}")
    private String merchantPolicyHost;
    @Value("${grpc.client.merchant-policy.port:9107}")
    private int merchantPolicyPort;

    @Value("${grpc.client.merchant-social-link.host:localhost}")
    private String merchantSocialHost;
    @Value("${grpc.client.merchant-social-link.port:9108}")
    private int merchantSocialPort;

    @Value("${grpc.client.category.host:localhost}")
    private String categoryHost;
    @Value("${grpc.client.category.port:9109}")
    private int categoryPort;

    @Value("${grpc.client.product.host:localhost}")
    private String productHost;
    @Value("${grpc.client.product.port:9110}")
    private int productPort;

    @Value("${grpc.client.banner.host:localhost}")
    private String bannerHost;
    @Value("${grpc.client.banner.port:9111}")
    private int bannerPort;

    @Value("${grpc.client.slider.host:localhost}")
    private String sliderHost;
    @Value("${grpc.client.slider.port:9112}")
    private int sliderPort;

    @Value("${grpc.client.cart.host:localhost}")
    private String cartHost;
    @Value("${grpc.client.cart.port:9113}")
    private int cartPort;

    @Value("${grpc.client.order.host:localhost}")
    private String orderHost;
    @Value("${grpc.client.order.port:9114}")
    private int orderPort;

    @Value("${grpc.client.order-item.host:localhost}")
    private String orderItemHost;
    @Value("${grpc.client.order-item.port:9115}")
    private int orderItemPort;

    @Value("${grpc.client.review.host:localhost}")
    private String reviewHost;
    @Value("${grpc.client.review.port:9116}")
    private int reviewPort;

    @Value("${grpc.client.review-detail.host:localhost}")
    private String reviewDetailHost;
    @Value("${grpc.client.review-detail.port:9117}")
    private int reviewDetailPort;

    @Value("${grpc.client.shipping-address.host:localhost}")
    private String shippingHost;
    @Value("${grpc.client.shipping-address.port:9118}")
    private int shippingPort;

    @Value("${grpc.client.transaction.host:localhost}")
    private String transactionHost;
    @Value("${grpc.client.transaction.port:9119}")
    private int transactionPort;

    @Value("${grpc.client.auth.host:localhost}")
    private String authHost;
    @Value("${grpc.client.auth.port:9120}")
    private int authPort;

    @Value("${grpc.client.stats.host:localhost}")
    private String statsHost;
    @Value("${grpc.client.stats.port:9121}")
    private int statsPort;

    private final List<ManagedChannel> channels = new ArrayList<>();

    private ManagedChannel channel(String host, int port) {
        ManagedChannel channel = NettyChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        channels.add(channel);
        return channel;
    }

    // --- auth ---
    @Bean
    public AuthServiceGrpc.AuthServiceBlockingStub authStub() {
        return AuthServiceGrpc.newBlockingStub(channel(authHost, authPort));
    }

    // --- role ---
    @Bean
    public RoleQueryServiceGrpc.RoleQueryServiceBlockingStub roleQueryStub() {
        return RoleQueryServiceGrpc.newBlockingStub(channel(roleHost, rolePort));
    }

    @Bean
    public RoleCommandServiceGrpc.RoleCommandServiceBlockingStub roleCommandStub() {
        return RoleCommandServiceGrpc.newBlockingStub(channel(roleHost, rolePort));
    }

    // --- user ---
    @Bean
    public UserQueryServiceGrpc.UserQueryServiceBlockingStub userQueryStub() {
        return UserQueryServiceGrpc.newBlockingStub(channel(userHost, userPort));
    }

    @Bean
    public UserCommandServiceGrpc.UserCommandServiceBlockingStub userCommandStub() {
        return UserCommandServiceGrpc.newBlockingStub(channel(userHost, userPort));
    }

    // --- merchant ---
    @Bean
    public MerchantQueryServiceGrpc.MerchantQueryServiceBlockingStub merchantQueryStub() {
        return MerchantQueryServiceGrpc.newBlockingStub(channel(merchantHost, merchantPort));
    }

    @Bean
    public MerchantCommandServiceGrpc.MerchantCommandServiceBlockingStub merchantCommandStub() {
        return MerchantCommandServiceGrpc.newBlockingStub(channel(merchantHost, merchantPort));
    }

    // --- merchant award ---
    @Bean
    public MerchantAwardQueryServiceGrpc.MerchantAwardQueryServiceBlockingStub merchantAwardQueryStub() {
        return MerchantAwardQueryServiceGrpc.newBlockingStub(channel(merchantAwardHost, merchantAwardPort));
    }

    @Bean
    public MerchantAwardCommandServiceGrpc.MerchantAwardCommandServiceBlockingStub merchantAwardCommandStub() {
        return MerchantAwardCommandServiceGrpc.newBlockingStub(channel(merchantAwardHost, merchantAwardPort));
    }

    // --- merchant business ---
    @Bean
    public MerchantBusinessQueryServiceGrpc.MerchantBusinessQueryServiceBlockingStub merchantBusinessQueryStub() {
        return MerchantBusinessQueryServiceGrpc.newBlockingStub(channel(merchantBusinessHost, merchantBusinessPort));
    }

    @Bean
    public MerchantBusinessCommandServiceGrpc.MerchantBusinessCommandServiceBlockingStub merchantBusinessCommandStub() {
        return MerchantBusinessCommandServiceGrpc.newBlockingStub(channel(merchantBusinessHost, merchantBusinessPort));
    }

    // --- merchant detail ---
    @Bean
    public MerchantDetailQueryServiceGrpc.MerchantDetailQueryServiceBlockingStub merchantDetailQueryStub() {
        return MerchantDetailQueryServiceGrpc.newBlockingStub(channel(merchantDetailHost, merchantDetailPort));
    }

    @Bean
    public MerchantDetailCommandServiceGrpc.MerchantDetailCommandServiceBlockingStub merchantDetailCommandStub() {
        return MerchantDetailCommandServiceGrpc.newBlockingStub(channel(merchantDetailHost, merchantDetailPort));
    }

    // --- merchant policy ---
    @Bean
    public MerchantPolicyQueryServiceGrpc.MerchantPolicyQueryServiceBlockingStub merchantPolicyQueryStub() {
        return MerchantPolicyQueryServiceGrpc.newBlockingStub(channel(merchantPolicyHost, merchantPolicyPort));
    }

    @Bean
    public MerchantPolicyCommandServiceGrpc.MerchantPolicyCommandServiceBlockingStub merchantPolicyCommandStub() {
        return MerchantPolicyCommandServiceGrpc.newBlockingStub(channel(merchantPolicyHost, merchantPolicyPort));
    }

    // --- merchant social link ---
    @Bean
    public MerchantSocialCommandServiceGrpc.MerchantSocialCommandServiceBlockingStub merchantSocialCommandStub() {
        return MerchantSocialCommandServiceGrpc.newBlockingStub(channel(merchantSocialHost, merchantSocialPort));
    }

    // --- category ---
    @Bean
    public CategoryQueryServiceGrpc.CategoryQueryServiceBlockingStub categoryQueryStub() {
        return CategoryQueryServiceGrpc.newBlockingStub(channel(categoryHost, categoryPort));
    }

    @Bean
    public CategoryCommandServiceGrpc.CategoryCommandServiceBlockingStub categoryCommandStub() {
        return CategoryCommandServiceGrpc.newBlockingStub(channel(categoryHost, categoryPort));
    }

    // --- product ---
    @Bean
    public ProductQueryServiceGrpc.ProductQueryServiceBlockingStub productQueryStub() {
        return ProductQueryServiceGrpc.newBlockingStub(channel(productHost, productPort));
    }

    @Bean
    public ProductCommandServiceGrpc.ProductCommandServiceBlockingStub productCommandStub() {
        return ProductCommandServiceGrpc.newBlockingStub(channel(productHost, productPort));
    }

    // --- banner ---
    @Bean
    public BannerQueryServiceGrpc.BannerQueryServiceBlockingStub bannerQueryStub() {
        return BannerQueryServiceGrpc.newBlockingStub(channel(bannerHost, bannerPort));
    }

    @Bean
    public BannerCommandServiceGrpc.BannerCommandServiceBlockingStub bannerCommandStub() {
        return BannerCommandServiceGrpc.newBlockingStub(channel(bannerHost, bannerPort));
    }

    // --- slider ---
    @Bean
    public SliderQueryServiceGrpc.SliderQueryServiceBlockingStub sliderQueryStub() {
        return SliderQueryServiceGrpc.newBlockingStub(channel(sliderHost, sliderPort));
    }

    @Bean
    public SliderCommandServiceGrpc.SliderCommandServiceBlockingStub sliderCommandStub() {
        return SliderCommandServiceGrpc.newBlockingStub(channel(sliderHost, sliderPort));
    }

    // --- cart ---
    @Bean
    public CartQueryServiceGrpc.CartQueryServiceBlockingStub cartQueryStub() {
        return CartQueryServiceGrpc.newBlockingStub(channel(cartHost, cartPort));
    }

    @Bean
    public CartCommandServiceGrpc.CartCommandServiceBlockingStub cartCommandStub() {
        return CartCommandServiceGrpc.newBlockingStub(channel(cartHost, cartPort));
    }

    // --- order ---
    @Bean
    public OrderQueryServiceGrpc.OrderQueryServiceBlockingStub orderQueryStub() {
        return OrderQueryServiceGrpc.newBlockingStub(channel(orderHost, orderPort));
    }

    @Bean
    public OrderCommandServiceGrpc.OrderCommandServiceBlockingStub orderCommandStub() {
        return OrderCommandServiceGrpc.newBlockingStub(channel(orderHost, orderPort));
    }

    // --- order item ---
    @Bean
    public OrderItemQueryServiceGrpc.OrderItemQueryServiceBlockingStub orderItemQueryStub() {
        return OrderItemQueryServiceGrpc.newBlockingStub(channel(orderItemHost, orderItemPort));
    }

    @Bean
    public OrderItemCommandServiceGrpc.OrderItemCommandServiceBlockingStub orderItemCommandStub() {
        return OrderItemCommandServiceGrpc.newBlockingStub(channel(orderItemHost, orderItemPort));
    }

    // --- review ---
    @Bean
    public ReviewQueryServiceGrpc.ReviewQueryServiceBlockingStub reviewQueryStub() {
        return ReviewQueryServiceGrpc.newBlockingStub(channel(reviewHost, reviewPort));
    }

    @Bean
    public ReviewCommandServiceGrpc.ReviewCommandServiceBlockingStub reviewCommandStub() {
        return ReviewCommandServiceGrpc.newBlockingStub(channel(reviewHost, reviewPort));
    }

    // --- review detail ---
    @Bean
    public ReviewDetailQueryServiceGrpc.ReviewDetailQueryServiceBlockingStub reviewDetailQueryStub() {
        return ReviewDetailQueryServiceGrpc.newBlockingStub(channel(reviewDetailHost, reviewDetailPort));
    }

    @Bean
    public ReviewDetailCommandServiceGrpc.ReviewDetailCommandServiceBlockingStub reviewDetailCommandStub() {
        return ReviewDetailCommandServiceGrpc.newBlockingStub(channel(reviewDetailHost, reviewDetailPort));
    }

    // --- shipping address ---
    @Bean
    public ShippingQueryServiceGrpc.ShippingQueryServiceBlockingStub shippingQueryStub() {
        return ShippingQueryServiceGrpc.newBlockingStub(channel(shippingHost, shippingPort));
    }

    @Bean
    public ShippingCommandServiceGrpc.ShippingCommandServiceBlockingStub shippingCommandStub() {
        return ShippingCommandServiceGrpc.newBlockingStub(channel(shippingHost, shippingPort));
    }

    // --- transaction ---
    @Bean
    public TransactionQueryServiceGrpc.TransactionQueryServiceBlockingStub transactionQueryStub() {
        return TransactionQueryServiceGrpc.newBlockingStub(channel(transactionHost, transactionPort));
    }

    @Bean
    public TransactionCommandServiceGrpc.TransactionCommandServiceBlockingStub transactionCommandStub() {
        return TransactionCommandServiceGrpc.newBlockingStub(channel(transactionHost, transactionPort));
    }

    // --- stats reader (ClickHouse analytics) ---
    @Bean
    public StatsReaderServiceGrpc.StatsReaderServiceBlockingStub statsReaderStub() {
        return StatsReaderServiceGrpc.newBlockingStub(channel(statsHost, statsPort));
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
