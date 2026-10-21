package com.sanedge.ecommerce.merchant_business.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sanedge.ecommerce.common.adapter.merchant.MerchantAdapter;
import com.sanedge.ecommerce.common.adapter.merchant.MerchantPort;
import com.sanedge.ecommerce.common.resilience.DependencyGuardInterceptor;
import com.sanedge.ecommerce.pb.merchant.MerchantQueryServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;

/** Outbound gRPC channels/stubs used by the merchant business service. */
@Configuration
public class GrpcClientConfig {

    @Value("${grpc.client.merchant.host:localhost}")
    private String merchantHost;

    @Value("${grpc.client.merchant.port:9103}")
    private int merchantPort;

    @Bean(destroyMethod = "shutdownNow")
    public ManagedChannel merchantChannel(DependencyGuardInterceptor dependencyGuardInterceptor) {
        return NettyChannelBuilder.forAddress(merchantHost, merchantPort)
                .usePlaintext()
                .intercept(dependencyGuardInterceptor)
                .build();
    }

    @Bean
    public MerchantQueryServiceGrpc.MerchantQueryServiceBlockingStub merchantQueryStub(
            @Qualifier("merchantChannel") ManagedChannel merchantChannel) {
        return MerchantQueryServiceGrpc.newBlockingStub(merchantChannel);
    }

    @Bean
    public MerchantPort merchantPort(MerchantQueryServiceGrpc.MerchantQueryServiceBlockingStub merchantQueryStub) {
        return new MerchantAdapter(merchantQueryStub);
    }
}
