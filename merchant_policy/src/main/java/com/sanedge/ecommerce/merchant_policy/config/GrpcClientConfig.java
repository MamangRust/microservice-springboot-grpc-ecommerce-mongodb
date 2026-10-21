package com.sanedge.ecommerce.merchant_policy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sanedge.ecommerce.common.adapter.merchant.MerchantAdapter;
import com.sanedge.ecommerce.common.adapter.merchant.MerchantPort;
import com.sanedge.ecommerce.common.resilience.DependencyGuardInterceptor;
import com.sanedge.ecommerce.pb.merchant.MerchantQueryServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;

@Configuration
public class GrpcClientConfig {

    @Bean(destroyMethod = "shutdownNow")
    public ManagedChannel merchantChannel(
            @Value("${grpc.client.merchant.host:localhost}") String host,
            @Value("${grpc.client.merchant.port:9103}") int port,
            DependencyGuardInterceptor dependencyGuardInterceptor) {
        return NettyChannelBuilder.forAddress(host, port).usePlaintext()
                .intercept(dependencyGuardInterceptor)
                .build();
    }

    @Bean
    public MerchantQueryServiceGrpc.MerchantQueryServiceBlockingStub merchantQueryServiceBlockingStub(
            ManagedChannel merchantChannel) {
        return MerchantQueryServiceGrpc.newBlockingStub(merchantChannel);
    }

    @Bean
    public MerchantPort merchantPort(MerchantQueryServiceGrpc.MerchantQueryServiceBlockingStub merchantQueryStub) {
        return new MerchantAdapter(merchantQueryStub);
    }
}
